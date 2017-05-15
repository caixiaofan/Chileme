package fourthquadrant.chileme.bean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fourthquadrant.chileme.Activity.MainActivity;
import fourthquadrant.chileme.App;
import fourthquadrant.chileme.Fragment.OrderFragment;

/**
 * Created by Dsw on 2017/3/24.
 * 通过异步任务来加载网络中的数据，进行更新
 */

public class GetDataTask extends AsyncTask<Void, Void, Void>{

    private PullToRefreshListView mPullToRefreshListView;
    private SimpleAdapter mAdapter;
    private List<String> mListItems;


    public GetDataTask(PullToRefreshListView listView,
                       SimpleAdapter adapter,
                       List<String> listItems)
    {
        // TODO 自动生成的构造函数存根
        mPullToRefreshListView = listView;
        mAdapter = adapter;
        //mAdapter.notifyDataSetChanged();
        mListItems = listItems;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //模拟请求
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO 自动生成的方法存根
        super.onPostExecute(result);
        //得到当前的模式
        Mode mode = mPullToRefreshListView.getCurrentMode();
        if(mode == Mode.PULL_FROM_START) {
            VolleyPost();

        }
        else {
            //mListItems.add("这是刷新出来的数据");
        }
        // 通知数据改变了
        mAdapter.notifyDataSetChanged();
        // 加载完成后停止刷新
        mPullToRefreshListView.onRefreshComplete();
    }

    // 定义GET请求的方法
    private void VolleyPost() {
        // 请求地址
        String url = GlobalValue.baseUrl + "OrderformShow";
        GlobalValue.order_list.clear();
        GlobalValue.order_shop_num.clear();
        GlobalValue.order_total.clear();
        GlobalValue.order_time.clear();
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
                        GlobalValue.order_list.add(0, showitem);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException JException){}
                App.getHttpQueue().cancelAll("GetOrder");
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {}
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


}
