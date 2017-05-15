package fourthquadrant.chileme.Activity;

import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fourthquadrant.chileme.App;
import fourthquadrant.chileme.R;
import fourthquadrant.chileme.Fragment.HomeFragment;
import fourthquadrant.chileme.Fragment.MeFragment;
import fourthquadrant.chileme.Fragment.OrderFragment;
import fourthquadrant.chileme.bean.GlobalValue;

/**
 * Created by Euphoria on 2017/3/18.
 */

public class MainActivity extends AppCompatActivity {

    private FrameLayout mHomeContent;
    private RadioGroup mHomeRadioGroup;
    private RadioButton mHomeHomeRb;
    private RadioButton mHomeOrderRb;
    private RadioButton mHomeMeRb;
    //private int mFragmentIndex = 0;



    protected void onCreate(Bundle savedInstanceState)
    {
        //Log.d("a","b");
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        VolleyPost();

        setContentView(R.layout.activity_main);
        initView();
    }

    public void onStop()
    {
        super.onStop();
        App.getHttpQueue().cancelAll("GetGoods");
    }

    // 定义GET请求的方法
    private void VolleyPost() {
        // 请求地址
        String url = GlobalValue.baseUrl + "GoodsShow";

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
                        Integer stoNum = JObject.getInt("stoNum");
                        String gooNum = JObject.getString("gooNum");
                        String gooName = JObject.getString("gooName");
                        Double gooPrice = JObject.getDouble("gooPrice");
                        String gooPic = GlobalValue.baseUrl + JObject.getString("gooPic");
                        GlobalValue.goods_num_map.put(gooNum, i);
                        GlobalValue.goods_num.add(gooNum);
                        GlobalValue.goods_name.add(gooName);
                        GlobalValue.goods_imgUrl.add(gooPic);
                        GlobalValue.goods_price.add(gooPrice);
                        GlobalValue.goods_shop.add(stoNum);
                    }
                } catch (JSONException JException){}
                //mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
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
        request.setTag("GetGoods");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    public void initView()
    {
        mHomeContent = (FrameLayout) findViewById(R.id.content_frame); //tab上方的区域
        mHomeRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);  //底部的四个tab
        mHomeHomeRb = (RadioButton) findViewById(R.id.radio_home);
        mHomeOrderRb = (RadioButton) findViewById(R.id.radio_order);
        mHomeMeRb = (RadioButton) findViewById(R.id.radio_me);

        //监听事件：为底部的RadioGroup绑定状态改变的监听事件
        mHomeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int arg1) {
                int mCheckedID = group.getCheckedRadioButtonId();
                int mFragmentIndex = 0;
                switch (mCheckedID) {
                    case R.id.radio_home:
                        mFragmentIndex = 0; break;
                    case R.id.radio_order:
                        mFragmentIndex = 1; break;
                    case R.id.radio_me:
                        mFragmentIndex = 2; break;
                }
                //通过mFragments这个adapter还有index来替换帧布局中的内容，当前帧为mNowFragment
                Fragment mNowFragment = (Fragment) mFragments.instantiateItem(mHomeContent, mFragmentIndex);
                //一开始将帧布局中的内容设置为第一个
                mFragments.setPrimaryItem(mHomeContent, 0, mNowFragment);
                mFragments.finishUpdate(mHomeContent);
            }
        });

        mHomeRadioGroup.check(R.id.radio_home);
    }

    FragmentStatePagerAdapter mFragments = new FragmentStatePagerAdapter(getSupportFragmentManager())
    {
        @Override
        public int getCount() { return 3; }

        @Override
        public Fragment getItem(int mFragmentIndex)
        {
            Fragment mFragment = null;
            switch (mFragmentIndex)
            {
                case 0: //首页
                    mFragment = new HomeFragment();
                    break;
                case 1: //订单
                    mFragment = new OrderFragment();
                    break;
                case 2: //个人
                    mFragment = new MeFragment();
                    break;
            }
            return mFragment;
        }
    };




}
