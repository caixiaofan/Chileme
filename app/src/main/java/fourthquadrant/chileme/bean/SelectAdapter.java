package fourthquadrant.chileme.bean;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import fourthquadrant.chileme.Activity.ShopDetailActivity;
import fourthquadrant.chileme.R;

/**
 * Created by Euphoria on 2017/4/3.
 */

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder>{
    private ShopDetailActivity activity;
    private SparseArray<GoodsItem> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;
    public SelectAdapter(ShopDetailActivity activity, SparseArray<GoodsItem> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.selected_good_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GoodsItem item = dataList.valueAt(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if(dataList==null) {
            return 0;
        }
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private GoodsItem item;
        private TextView tvCost,tvCount,tvAdd,tvMinus,tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.selected_item_name);
            tvCost = (TextView) itemView.findViewById(R.id.selected_item_cost);
            tvCount = (TextView) itemView.findViewById(R.id.selected_item_count);
            tvMinus = (TextView) itemView.findViewById(R.id.selected_item_minus);
            tvAdd = (TextView) itemView.findViewById(R.id.selected_item_add);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.selected_item_add:
                    activity.add(item, true);
                    break;
                case R.id.selected_item_minus:
                    activity.remove(item, true);
                    break;
                default:
                    break;
            }
        }

        public void bindData(GoodsItem item){
            this.item = item;
            tvName.setText(item.name);
            tvCost.setText(nf.format(item.count*item.price));
            tvCount.setText(String.valueOf(item.count));
        }
    }
}
