package fourthquadrant.chileme.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fourthquadrant.chileme.Activity.FavouriteDetailActivity;
import fourthquadrant.chileme.App;
import fourthquadrant.chileme.R;
import fourthquadrant.chileme.bean.GlobalValue;

/**
 * Created by Euphoria on 2017/3/23.
 */

public class MeFragment extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.me_fragment,null);
        initView(view);
        VolleyPost();
        return view;
    }

    public void onStop()
    {
        super.onStop();
        App.getHttpQueue().cancelAll("GetFavourite");
    }

    // 定义GET请求的方法
    private void VolleyPost() {
        // 请求地址
        String url = GlobalValue.baseUrl + "FavouriteShow";

        // 创建StringRequest，定义字符串请求的请求方式为POST，
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray JArray = new JSONArray(s);
                    for (int i=0;i<JArray.length();i++)
                    {
                        JSONObject JObject = JArray.getJSONObject(i);
                        Integer shop_num = JObject.getInt("stoNum");
                        GlobalValue.favourite_num.add(shop_num);
                    }
                } catch (JSONException JException){}
                //mAdapter.notifyDataSetChanged();

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
        request.setTag("GetFavourite");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    private void initView(View mView)
    {
        LinearLayout mFavourite = (LinearLayout) mView.findViewById(R.id.my_favourite);
        LinearLayout mContact = (LinearLayout) mView.findViewById(R.id.my_contact);
        LinearLayout mAddress = (LinearLayout) mView.findViewById(R.id.my_address);
        mFavourite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), FavouriteDetailActivity.class);
                startActivity(intent);
            }
        });
        mContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "您的联系方式为：15624952054", Toast.LENGTH_SHORT).show();
            }
        });
        mAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "您的地址为：北京市海淀区北京理工大学3号宿舍楼", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
}

