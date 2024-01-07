package com.raghava.shopaholic.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.raghava.shopaholic.R;
import com.raghava.shopaholic.interfaces.ItemClickListner;

public class RelatedProductsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView relatedProdName, relatedProdPrice;
    private ItemClickListner itemClickListener;
    public ImageView relatedProdImg;

    public RelatedProductsHolder(View itemView) {
        super(itemView);

        relatedProdImg=itemView.findViewById(R.id.relatedProdImg);
        relatedProdPrice=itemView.findViewById(R.id.relatedProdPrice);
        relatedProdImg=itemView.findViewById(R.id.relatedProdImg);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.OnClick(v,getAdapterPosition(),false);
    }
}
