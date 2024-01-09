package com.raghava.shopaholic.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.raghava.shopaholic.R;
import com.raghava.shopaholic.interfaces.ItemClickListner;

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemClickListner itemClickListener;
    public TextView orderName, orderDate, orderAddr, orderCity, orderPrice, orderPhone;

    public OrdersViewHolder(View itemView) {
        super(itemView);

        orderName=itemView.findViewById(R.id.orderName);
        orderPhone=itemView.findViewById(R.id.orderPhone);
        orderAddr=itemView.findViewById(R.id.orderAddr);
        orderCity=itemView.findViewById(R.id.orderCity);
        orderDate=itemView.findViewById(R.id.orderDate);
        orderPrice=itemView.findViewById(R.id.orderPrice);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.OnClick(v,getAdapterPosition(),false);
    }
}
