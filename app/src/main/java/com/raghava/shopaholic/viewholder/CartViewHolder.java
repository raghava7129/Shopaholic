package com.raghava.shopaholic.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.raghava.shopaholic.R;
import com.raghava.shopaholic.interfaces.ItemClickListner;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cartProductName, cartProductPrice;
    private ItemClickListner itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);
        cartProductName = itemView.findViewById(R.id.cart_product_name);
        cartProductPrice = itemView.findViewById(R.id.cart_product_price);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.OnClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListner itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
