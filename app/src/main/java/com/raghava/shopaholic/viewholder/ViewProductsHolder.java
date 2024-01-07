package com.raghava.shopaholic.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.raghava.shopaholic.R;
import com.raghava.shopaholic.interfaces.ItemClickListner;

public class ViewProductsHolder extends RecyclerView.ViewHolder implements ItemClickListner {

    public TextView addProductName, addProductPrice;
    private ItemClickListner itemClickListener;
    public ImageView addProductImg;

    public ViewProductsHolder(View itemView) {
        super(itemView);

        addProductName=itemView.findViewById(R.id.prodName);
        addProductPrice=itemView.findViewById(R.id.prodPrice);
        addProductImg=itemView.findViewById(R.id.prodImg);

    }

    @Override
    public void OnClick(View view, int position, boolean isLongClick) {
        itemClickListener.OnClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListner itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
