package fourthquadrant.chileme.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import fourthquadrant.chileme.App;
import fourthquadrant.chileme.R;

import fourthquadrant.chileme.bean.GlobalValue;

/**
 * Created by Euphoria on 2017/4/2.
 */

public class FavouriteDetailActivity extends Activity {

    private List<Map<String, Object>> favourite_list = new ArrayList<>();
    public static SimpleAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.awesome_orange));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.awesome_orange));
        }
        setContentView(R.layout.activity_favourite);

        for (int i = 0; i < GlobalValue.favourite_num.size(); i++)
        {
            Map<String,Object> item = new HashMap<>();
            int id = GlobalValue.favourite_num.get(i);
            item.put("shop_names",GlobalValue.shop_names.get(id));
            item.put("shop_imgUrl",GlobalValue.shop_imgUrl.get(id));
            item.put("shop_intro",GlobalValue.shop_intro.get(id));
            favourite_list.add(item);
        }

        //创建一个simpleAdapter
        mAdapter = new SimpleAdapter(
                this, favourite_list, R.layout.favourite_list_item,
                new String[]{"shop_names","shop_imgUrl","shop_intro"},
                new int[]{
                        R.id.favourite_shop_name,
                        R.id.favourite_shop_img,
                        R.id.favourite_shop_intro
                });
        ListView listView = (ListView) findViewById(R.id.favourite_list);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>arg0, View arg1, int arg2,long arg3) {
                Bundle mBundle = new Bundle();
                mBundle.putInt("shop_num", GlobalValue.favourite_num.get(arg2));
                Intent intent = new Intent();
                intent.putExtras(mBundle);
                intent.setClass(FavouriteDetailActivity.this, ShopDetailActivity.class);
                startActivity(intent);
            }
        });

    }




}
