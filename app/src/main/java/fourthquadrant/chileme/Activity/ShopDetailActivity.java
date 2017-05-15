package fourthquadrant.chileme.Activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flipboard.bottomsheet.BottomSheetLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fourthquadrant.chileme.App;
import fourthquadrant.chileme.Fragment.OrderFragment;
import fourthquadrant.chileme.bean.GlobalValue;
import fourthquadrant.chileme.bean.GoodsAdapter;
import fourthquadrant.chileme.bean.GoodsItem;
import fourthquadrant.chileme.bean.SelectAdapter;

import fourthquadrant.chileme.R;

/**
 * Created by Euphoria on 2017/4/2.
 */

public class ShopDetailActivity extends Activity implements View.OnClickListener
{
    private ImageView imgCart;
    private ViewGroup anim_mask_layout;
    private RecyclerView rvSelected;
    private TextView tvCount,tvCost,tvSubmit,tvTips;
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private ListView listView;
    private Double cost;
    private Integer shop_num;

    private ArrayList<GoodsItem> dataList;
    private SparseArray<GoodsItem> selectedList;
    private SparseIntArray groupSelect;
    private boolean isFavourite;
    private ImageView isFavouriteStar;
    private GoodsAdapter myAdapter;
    private SelectAdapter selectAdapter;

    private NumberFormat nf;
    private Handler mHanlder;
    public Integer orfNum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        setContentView(R.layout.activity_shop_detail);

        Bundle mBundle = getIntent().getExtras();
        shop_num = mBundle.getInt("shop_num");

        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mHanlder = new Handler(getMainLooper());
        dataList = new ArrayList<>();
        for (int i = 0; i < GlobalValue.goods_shop.size(); i++)
        {
            if (GlobalValue.goods_shop.get(i) == shop_num)
            {
                dataList.add(new GoodsItem(i,
                        GlobalValue.goods_price.get(i),
                        GlobalValue.goods_name.get(i),
                        GlobalValue.goods_imgUrl.get(i)
                ));
            }
        }

        selectedList = new SparseArray<>();
        groupSelect = new SparseIntArray();
        initView();

    }

    private void initView() {
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvTips = (TextView) findViewById(R.id.tvTips);
        tvSubmit  = (TextView) findViewById(R.id.tvSubmit);
        imgCart = (ImageView) findViewById(R.id.cart_icon);
        anim_mask_layout = (RelativeLayout) findViewById(R.id.containerLayout);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomSheetLayout);
        SimpleDraweeView mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.shop_detail_shop_img);
        mSimpleDraweeView.setImageURI(GlobalValue.shop_imgUrl.get(shop_num));
        TextView mTextView = (TextView) findViewById(R.id.shop_detail_shop_name);
        mTextView.setText(GlobalValue.shop_names.get(shop_num));
        isFavouriteStar = (ImageView) findViewById (R.id.shop_detail_is_favourite);
        isFavourite = false;
        for (int i = 0;i < GlobalValue.favourite_num.size(); i++)
        {
            if (GlobalValue.favourite_num.get(i) == shop_num)
            {
                isFavourite = true;
                break;
            }
        }
        if (isFavourite)
            isFavouriteStar.setImageResource(R.mipmap.btn_star_big_on);
        isFavouriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite)
                {
                    isFavouriteStar.setImageResource(R.mipmap.btn_star_big_off);
                    VolleyPost4();
                    for (int i=0;i<GlobalValue.favourite_num.size();i++)
                    {
                        if (shop_num == GlobalValue.favourite_num.get(i))
                        {
                            GlobalValue.favourite_num.remove(i);
                            break;
                        }
                    }
                }
                else
                {
                    boolean ffflag = false;
                    isFavouriteStar.setImageResource(R.mipmap.btn_star_big_on);
                    VolleyPost3();
                    for (int i = 0;i<GlobalValue.favourite_num.size();i++)
                    {
                        if (shop_num == GlobalValue.favourite_num.get(i))
                        {
                            ffflag = true;
                            break;
                        }
                    }
                    if (ffflag == false)
                        GlobalValue.favourite_num.add(shop_num);
                }
            }
        });
        listView = (ListView) findViewById(R.id.itemListView);
        myAdapter = new GoodsAdapter(dataList,this);
        listView.setAdapter(myAdapter);

    }

    public void playAnimation(int[] start_location){
        ImageView img = new ImageView(this);
        img.setImageResource(R.mipmap.button_add);
        setAnim(img,start_location);
    }

    private Animation createAnim(int startX, int startY){
        int[] des = new int[2];
        imgCart.getLocationInWindow(des);

        AnimationSet set = new AnimationSet(false);

        Animation translationX = new TranslateAnimation(0, des[0]-startX, 0, 0);
        translationX.setInterpolator(new LinearInterpolator());
        Animation translationY = new TranslateAnimation(0, 0, 0, des[1]-startY);
        translationY.setInterpolator(new AccelerateInterpolator());
        Animation alpha = new AlphaAnimation(1,0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        set.setDuration(500);

        return set;
    }

    private void addViewToAnimLayout(final ViewGroup vg, final View view, int[] location) {

        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y-loc[1]);
        vg.addView(view);
    }

    private void setAnim(final View v, int[] start_location) {

        addViewToAnimLayout(anim_mask_layout, v, start_location);
        Animation set = createAnim(start_location[0],start_location[1]);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim_mask_layout.removeView(v);
                    }
                },100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(set);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bottom:
                showBottomSheet();
                break;
            case R.id.clear:
                clearCart();
                break;
            case R.id.tvSubmit:
                VolleyPost1();
                Log.d("orfNum",String.valueOf(GlobalValue.order_list.size()));
                VolleyPost2();
                Toast.makeText(ShopDetailActivity.this, "结算完毕", Toast.LENGTH_SHORT).show();
                //clearCart();
                break;
            default:
                break;
        }
    }

    public void onStop()  {
        super.onStop();
        App.getHttpQueue().cancelAll("AddFavourite");
        App.getHttpQueue().cancelAll("RemoveFavourite");
    }

    private void VolleyPost1() {
        String url = GlobalValue.baseUrl + "OrderformAdd";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray JArray = new JSONArray(s);
                    orfNum = JArray.getJSONObject(0).getInt("orfNum");
                } catch (JSONException JException){}
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ShopDetailActivity.this, "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("orfTotal", String.valueOf(cost));
                String shop_num_str = String.valueOf(shop_num);
                while(shop_num_str.length() < 2)
                    shop_num_str = '0' + shop_num_str;
                hashMap.put("stoNum", shop_num_str);
                hashMap.put("teleNum", "12345678");
                hashMap.put("address", "北京理工大学3号宿舍楼");
                return hashMap;
            }
        };
        // 设置该请求的标签
        request.setTag("SubmitOrder");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    private void VolleyPost2() {
        // 请求地址
        String url = GlobalValue.baseUrl + "OrdersAdd";
        // 创建StringRequest，定义字符串请求的请求方式为POST，
        int size = selectedList.size();
        for (int i = 0; i < size; i++)
        {
            final GoodsItem item = selectedList.valueAt(i);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                // 请求成功后执行的函数
                @Override
                public void onResponse(String s) {
                }
            }, new Response.ErrorListener() {
                // 请求失败时执行的函数
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(ShopDetailActivity.this, "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
                }
            }) {
                // 定义请求数据
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("orfNum", String.valueOf(GlobalValue.order_list.size()));
                    hashMap.put("gooNum", GlobalValue.goods_num.get(item.id));
                    hashMap.put("gooAmo", String.valueOf(item.count));
                    hashMap.put("gooTotal", String.valueOf(item.count * item.price));
                    Log.d("orf",String.valueOf(GlobalValue.order_list.size())+"\n"+
                            GlobalValue.goods_num.get(item.id)+"\n"+
                            String.valueOf(item.count)+"\n"+
                            String.valueOf(item.count * item.price)+"\n"
                    );
                    return hashMap;
                }
            };
            // 设置该请求的标签
            request.setTag("SubmitOrderDetail");

            // 将请求添加到队列中
            App.getHttpQueue().add(request);
        }
    }

    private void VolleyPost3() {
        // 请求地址
        String url = GlobalValue.baseUrl + "FavouriteAdd";
        // 创建StringRequest，定义字符串请求的请求方式为POST，
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                Toast.makeText(ShopDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ShopDetailActivity.this, "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();

                String shop_num_str = String.valueOf(shop_num);
                while(shop_num_str.length() < 2)
                    shop_num_str = '0' + shop_num_str;
                hashMap.put("stoNum", shop_num_str);

                return hashMap;
            }
        };
        // 设置该请求的标签
        request.setTag("AddFavourite");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    private void VolleyPost4() {
        // 请求地址
        String url = GlobalValue.baseUrl + "FavouriteDelete";
        // 创建StringRequest，定义字符串请求的请求方式为POST，
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                Toast.makeText(ShopDetailActivity.this, "取消收藏成功", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ShopDetailActivity.this, "POST: " + "网络连接失败", Toast.LENGTH_LONG).show();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();

                String shop_num_str = String.valueOf(shop_num);
                while(shop_num_str.length() < 2)
                    shop_num_str = '0' + shop_num_str;
                hashMap.put("stoNum", shop_num_str);

                return hashMap;
            }
        };
        // 设置该请求的标签
        request.setTag("RemoveFavourite");

        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    //添加商品
    public void add(GoodsItem item,boolean refreshGoodList)
    {

        GoodsItem temp = selectedList.get(item.id);
        if(temp==null){
            item.count=1;
            selectedList.append(item.id,item);
        } else {
            temp.count++;
        }
        update(refreshGoodList);
    }
    //移除商品
    public void remove(GoodsItem item,boolean refreshGoodList)
    {
        GoodsItem temp = selectedList.get(item.id);
        if(temp!=null){
            if(temp.count<2){
                selectedList.remove(item.id);
            }else{
                item.count--;
            }
        }
        update(refreshGoodList);
    }
    //刷新布局 总价、购买数量等
    private void update(boolean refreshGoodList){
        int size = selectedList.size();
        int count =0;
        cost = 0.0;
        for(int i=0;i<size;i++){
            GoodsItem item = selectedList.valueAt(i);
            count += item.count;
            cost += item.count*item.price;
        }

        if(count<1){
            tvCount.setVisibility(View.GONE);
        }else{
            tvCount.setVisibility(View.VISIBLE);
        }

        tvCount.setText(String.valueOf(count));

        if(cost > 19.99){
            tvTips.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
        }else{
            tvSubmit.setVisibility(View.GONE);
            tvTips.setVisibility(View.VISIBLE);
        }

        tvCost.setText(nf.format(cost));

        if(myAdapter!=null && refreshGoodList){
            myAdapter.notifyDataSetChanged();
        }
        if(selectAdapter!=null){
            selectAdapter.notifyDataSetChanged();
        }
        if(bottomSheetLayout.isSheetShowing() && selectedList.size()<1){
            bottomSheetLayout.dismissSheet();
        }
    }
    //清空购物车
    public void clearCart(){
        selectedList.clear();
        groupSelect.clear();
        update(true);
    }
    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id){
        GoodsItem temp = selectedList.get(id);
        if(temp==null){
            return 0;
        }
        return temp.count;
    }

    private View createBottomSheetView(){
        View view = LayoutInflater.from(this).inflate(R.layout.cart_detail,(ViewGroup) getWindow().getDecorView(),false);
        rvSelected = (RecyclerView) view.findViewById(R.id.selectRecyclerView);
        rvSelected.setLayoutManager(new LinearLayoutManager(this));
        TextView clear = (TextView) view.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        selectAdapter = new SelectAdapter(this,selectedList);
        rvSelected.setAdapter(selectAdapter);
        return view;
    }

    private void showBottomSheet(){
        if(bottomSheet==null){
            bottomSheet = createBottomSheetView();
        }
        if(bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }else {
            if(selectedList.size()!=0){
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }


}
