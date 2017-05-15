package fourthquadrant.chileme.Activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fourthquadrant.chileme.App;
import fourthquadrant.chileme.R;
import fourthquadrant.chileme.bean.GlobalValue;

/**
 * Created by Euphoria on 2017/3/31.
 */

public class OrderDetailActivity extends Activity {

    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> goods_list = new ArrayList<>();
    Integer order_num;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.awesome_orange));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.awesome_orange));
        }
        setContentView(R.layout.activity_order_detail);
        Bundle mBundle = getIntent().getExtras();
        order_num = mBundle.getInt("order_num");

        SimpleDraweeView mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.order_detail_shop_img);
        mSimpleDraweeView.setImageURI(GlobalValue.shop_imgUrl.get(GlobalValue.order_shop_num.get(order_num)));

        TextView mTextView = (TextView) findViewById(R.id.order_detail_shopname);
        mTextView.setText(GlobalValue.shop_names.get(GlobalValue.order_shop_num.get(order_num)));
        mTextView = (TextView) findViewById(R.id.order_detail_total);
        mTextView.setText("总价 ¥" + String.valueOf(GlobalValue.order_total.get(order_num)));
        mTextView = (TextView) findViewById(R.id.order_detail_time);
        mTextView.setText(GlobalValue.order_time.get(order_num));
        mTextView = (TextView) findViewById (R.id.order_detail_num);
        mTextView.setText(String.valueOf(order_num));

        //创建一个simpleAdapter
        mAdapter = new SimpleAdapter(
                this, goods_list, R.layout.order_detail_goods_list_item,
                new String[]{ "goods_name", "goods_cnt", "goods_price"},
                new int[]{
                        R.id.order_detail_goods_name,
                        R.id.order_detail_goods_cnt,
                        R.id.order_detail_goods_price
                });
        ListView order_detail_goods_list = (ListView) findViewById(R.id.order_detail_goods_list);
        order_detail_goods_list.setAdapter(mAdapter);
        VolleyPost();
    }

    public void onStop()
    {
        super.onStop();
        App.getHttpQueue().cancelAll("GetOrderDetail");
    }

    // 定义GET请求的方法
    private void VolleyPost() {
        // 请求地址
        String url = GlobalValue.baseUrl + "OrdersShow";

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
                        String gooNum = JObject.getString("gooNum");
                        Integer gooAmo = JObject.getInt("gooAmo");
                        Double gooTotal = JObject.getDouble("gooTotal");

                        Map<String,Object> item = new HashMap<>();
                        item.put("goods_name", GlobalValue.goods_name.get(GlobalValue.goods_num_map.get(gooNum)));
                        item.put("goods_cnt", "x" + gooAmo);
                        item.put("goods_price", "¥" + gooTotal);
                        goods_list.add(item);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException JException){}
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(OrderDetailActivity.this, "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("orfNum", String.valueOf( order_num ));
                return hashMap;
            }
        };
        // 设置该请求的标签
        request.setTag("GetOrderDetail");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

}
