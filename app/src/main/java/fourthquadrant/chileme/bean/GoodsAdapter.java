package fourthquadrant.chileme.bean;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.NumberFormat;
import java.util.ArrayList;

import fourthquadrant.chileme.Activity.ShopDetailActivity;
import fourthquadrant.chileme.R;

/**
 * Created by Euphoria on 2017/4/3.
 */

public class GoodsAdapter extends BaseAdapter {

    private ArrayList<GoodsItem> dataList;
    private ShopDetailActivity mContext;
    private NumberFormat nf;
    private LayoutInflater mInflater;

    public GoodsAdapter(ArrayList<GoodsItem> dataList, ShopDetailActivity mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        if(dataList==null){
            return 0;
        }
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.good_list_item,parent,false);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ItemViewHolder) convertView.getTag();
        }
        GoodsItem item = dataList.get(position);
        holder.bindData(item);
        return convertView;
    }

    class ItemViewHolder implements View.OnClickListener{
        private TextView name,price,tvAdd,tvMinus,tvCount;
        private SimpleDraweeView imgUrl;
        private GoodsItem item;

        public ItemViewHolder(View itemView)
        {
            name = (TextView) itemView.findViewById(R.id.good_item_name);
            price = (TextView) itemView.findViewById(R.id.good_item_price);
            tvCount = (TextView) itemView.findViewById(R.id.good_item_count);
            tvMinus = (TextView) itemView.findViewById(R.id.good_item_minus);
            tvAdd = (TextView) itemView.findViewById(R.id.good_item_add);
            imgUrl = (SimpleDraweeView) itemView.findViewById(R.id.good_item_img);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
        }

        public void bindData(GoodsItem item){
            this.item = item;
            imgUrl.setImageURI(item.imgUrl);
            Log.d(String.valueOf(item.id),item.imgUrl);
            name.setText(item.name);
            item.count = mContext.getSelectedItemCountById(item.id);
            tvCount.setText(String.valueOf(item.count));
            price.setText(nf.format(item.price));
            if(item.count < 1){
                tvCount.setVisibility(View.GONE);
                tvMinus.setVisibility(View.GONE);
            } else {
                tvCount.setVisibility(View.VISIBLE);
                tvMinus.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            ShopDetailActivity activity = mContext;
            switch (v.getId()) {
                case R.id.good_item_add: {
                    int count = activity.getSelectedItemCountById(item.id);
                    if (count < 1) {
                        tvMinus.setAnimation(getShowAnimation());
                        tvMinus.setVisibility(View.VISIBLE);
                        tvCount.setVisibility(View.VISIBLE);
                    }
                    activity.add(item, false);
                    count++;
                    tvCount.setText(String.valueOf(count));
                    int[] loc = new int[2];
                    v.getLocationInWindow(loc);
                    activity.playAnimation(loc);
                }
                break;
                case R.id.good_item_minus: {
                    int count = activity.getSelectedItemCountById(item.id);
                    if (count < 2) {
                        tvMinus.setAnimation(getHiddenAnimation());
                        tvMinus.setVisibility(View.GONE);
                        tvCount.setVisibility(View.GONE);
                    }
                    count--;
                    activity.remove(item, false);//activity.getSelectedItemCountById(item.id)
                    tvCount.setText(String.valueOf(count));

                }
                break;
                default:
                    break;
            }
        }
    }

    private Animation getShowAnimation(){
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,2f
                ,TranslateAnimation.RELATIVE_TO_SELF,0
                ,TranslateAnimation.RELATIVE_TO_SELF,0
                ,TranslateAnimation.RELATIVE_TO_SELF,0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0,1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    private Animation getHiddenAnimation(){
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0,720,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,0
                ,TranslateAnimation.RELATIVE_TO_SELF,2f
                ,TranslateAnimation.RELATIVE_TO_SELF,0
                ,TranslateAnimation.RELATIVE_TO_SELF,0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1,0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }
}
