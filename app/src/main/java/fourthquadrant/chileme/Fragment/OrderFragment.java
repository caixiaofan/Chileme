package fourthquadrant.chileme.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fourthquadrant.chileme.Activity.OrderDetailActivity;
import fourthquadrant.chileme.App;
import fourthquadrant.chileme.R;
import fourthquadrant.chileme.bean.GetDataTask;
import fourthquadrant.chileme.bean.GlobalValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Euphoria on 2017/3/23.
 */

public class OrderFragment extends Fragment
{
    private SimpleAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private  List<String> str;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStop()
    {
        super.onStop();
        App.getHttpQueue().cancelAll("GetOrder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_fragment,null);
        VolleyPost();
        initPTRListView(view);
        initViews(view);
        return view;
    }

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView(View mView) {
        mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.order_list);
        //设置拉动监听器
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),//getApplication
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // 开始执行异步任务，传入适配器来进行数据改变
                new GetDataTask(mPullToRefreshListView, mAdapter,str).execute();
            }
        });

        // 添加滑动到底部的监听器
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(getContext(), "已经到底了", Toast.LENGTH_SHORT).show();//
            }
        });

        //mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
        //在刷新时允许继续滑动
        mPullToRefreshListView.setScrollingWhileRefreshingEnabled(true);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

    }

    public void initViews(View mView)
    {
        mAdapter = new SimpleAdapter(
                getActivity(),
                GlobalValue.order_list,
                R.layout.order_list_item,
                new String[]{"shop_names","shop_imgUrl", "order_total", "order_time"},
                new int[]{
                        R.id.order_shop_name,
                        R.id.order_shop_img,
                        R.id.order_total,
                        R.id.order_time
                });
        ListView mListView = mPullToRefreshListView.getRefreshableView();

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>arg0, View arg1, int arg2,long arg3) {
                Bundle mBundle = new Bundle();
                mBundle.putInt("order_num", GlobalValue.order_list.size() - arg2);
                Intent intent = new Intent();
                intent.putExtras(mBundle);
                intent.setClass(getActivity(), OrderDetailActivity.class);
                startActivity(intent);
            }
        });

    }

    // 定义GET请求的方法
    private void VolleyPost() {
        // 请求地址
        String url = GlobalValue.baseUrl + "OrderformShow";
        GlobalValue.order_list.clear();
        // 创建StringRequest，定义字符串请求的请求方式为POST，
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray JArray = new JSONArray(s);
                    for (int i = 0; i < JArray.length(); i++)
                    {
                        JSONObject JObject = JArray.getJSONObject(i);
                        Double orfTotal = JObject.getDouble("orfTotal");
                        Integer stoNum = JObject.getInt("stoNum");
                        String orfTime = JObject.getString("orfTime");
                        GlobalValue.order_shop_num.add(stoNum);
                        GlobalValue.order_total.add(orfTotal);
                        GlobalValue.order_time.add(orfTime);

                        Map<String, Object> showitem = new HashMap<>();
                        showitem.put("shop_names", GlobalValue.shop_names.get(GlobalValue.order_shop_num.get(i)));
                        showitem.put("shop_imgUrl", GlobalValue.shop_imgUrl.get(GlobalValue.order_shop_num.get(i)));
                        showitem.put("order_total", "¥ " + GlobalValue.order_total.get(i));
                        showitem.put("order_time", GlobalValue.order_time.get(i));
                        GlobalValue.order_list.add(0,showitem);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException JException){}
                App.getHttpQueue().cancelAll("GetOrder");
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<String, String>();
                //hashMap.put("phone", "11111");
                return hashMap;
            }
        };
        // 设置该请求的标签
        request.setTag("GetOrder");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
}