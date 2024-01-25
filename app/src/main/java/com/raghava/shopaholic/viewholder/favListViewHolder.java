package com.raghava.shopaholic.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.raghava.shopaholic.R;
import com.raghava.shopaholic.interfaces.ItemClickListner;



public class favListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView fav_nameView,fav_priceView;

    private ItemClickListner itemClickListener;

    public favListViewHolder(View itemView) {
        super(itemView);

        fav_nameView = itemView.findViewById(R.id.fav_nameView);
        fav_priceView = itemView.findViewById(R.id.fav_priceView);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.OnClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListner itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
