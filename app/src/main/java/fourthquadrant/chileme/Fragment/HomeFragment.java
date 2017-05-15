package fourthquadrant.chileme.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import cn.bingoogolapple.bgabanner.BGABanner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fourthquadrant.chileme.Activity.MainActivity;
import fourthquadrant.chileme.Activity.OrderDetailActivity;
import fourthquadrant.chileme.Activity.ShopDetailActivity;
import fourthquadrant.chileme.R;
import fourthquadrant.chileme.App;
import fourthquadrant.chileme.bean.Engine;
import fourthquadrant.chileme.bean.GlobalValue;
import fourthquadrant.chileme.bean.MyListView;

/**
 * Created by Euphoria on 2017/3/23.
 */

public class HomeFragment extends Fragment
{
    private BGABanner mBGABanner;
    private List<View> mViews;
    private Engine mEngine;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> shop_list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mEngine= App.getInstance().getEngine();
        super.onCreate(savedInstanceState);
    }

    public void onStop()
    {
        super.onStop();
        App.getHttpQueue().cancelAll("GetShop");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, null);
        VolleyPost();
        initBGABanner(mView);
        initViews(mView);
        return mView;
    }

    private void initBGABanner(View mView)
    {
        mBGABanner = (BGABanner) mView.findViewById(R.id.bgabanner);
        mViews=getViews(5);
        mBGABanner.setData(mViews);
        List<String> imgs = new ArrayList<String>();

        imgs.add(GlobalValue.baseUrl + "images/lunbo/kfc.jpg");
        imgs.add(GlobalValue.baseUrl + "images/lunbo/mdl.jpg");
        imgs.add(GlobalValue.baseUrl + "images/lunbo/bsk.jpg");
        imgs.add(GlobalValue.baseUrl + "images/lunbo/hbw.jpg");
        imgs.add(GlobalValue.baseUrl + "images/lunbo/dks.jpg");

        List<String> tips = new ArrayList<String>();
        tips.add("肯德基");
        tips.add("麦当劳");
        tips.add("必胜客");
        tips.add("汉堡王");
        tips.add("德克士");

        SimpleDraweeView simpleDraweeView;
        for (int i = 0; i < mViews.size(); i++) {
            simpleDraweeView = (SimpleDraweeView) mViews.get(i);
            simpleDraweeView.setImageURI(Uri.parse(imgs.get(i)));
        }
        mBGABanner.setData(mViews, null, tips);
    }

    private List<View> getViews(int cnt){
        List<View> tmpViews = new ArrayList<View>();
        for (int i = 0; i < cnt; i++)
            tmpViews.add(getActivity().getLayoutInflater().inflate(R.layout.bga_background_image, null));
        return tmpViews;
    }

    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }

    public void initViews(View mView)
    {
        mAdapter = new SimpleAdapter(
                getActivity(), shop_list, R.layout.shop_list_item,
                new String[]{"shop_names","shop_imgUrl", "shop_intro"},
                new int[]{R.id.home_shop_name,R.id.home_shop_img, R.id.home_shop_intro});
        ListView mListView = (ListView) mView.findViewById(R.id.shop_list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>arg0, View arg1, int arg2,long arg3) {
                Bundle mBundle = new Bundle();
                mBundle.putInt("shop_num", arg2);
                //mBundle.putString("shop_names", GlobalValue.shop_names.get(arg2));
                //mBundle.putString("shop_imgUrl", GlobalValue.shop_imgUrl.get(arg2));
                Intent intent = new Intent();
                intent.putExtras(mBundle);
                intent.setClass(getActivity(), ShopDetailActivity.class);
                startActivity(intent);
            }
        });

    }

    // 定义GET请求的方法
    private void VolleyPost() {
        // 请求地址
        String url = GlobalValue.baseUrl + "StoreShow";

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
                        String stoName = JObject.getString("stoName");
                        String stoIntr = JObject.getString("stoIntr");
                        String stoPic = GlobalValue.baseUrl + JObject.getString("stoPic");
                        GlobalValue.shop_names.add(stoName);
                        GlobalValue.shop_intro.add(stoIntr);
                        GlobalValue.shop_imgUrl.add(stoPic);

                        Map<String, Object> showitem = new HashMap<>();
                        showitem.put("shop_names", GlobalValue.shop_names.get(i));
                        showitem.put("shop_imgUrl", GlobalValue.shop_imgUrl.get(i));
                        showitem.put("shop_intro", GlobalValue.shop_intro.get(i));

                        shop_list.add(showitem);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException JException){}
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
        request.setTag("GetShop");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

}
