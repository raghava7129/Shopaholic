package com.raghava.shopaholic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.raghava.shopaholic.model.Orders;
import com.raghava.shopaholic.viewholder.OrdersViewHolder;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ShowHistory extends AppCompatActivity {

    RecyclerView historyList;
    ImageView back_btn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        init_views();

        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        historyList.setLayoutManager(new LinearLayoutManager(ShowHistory.this));
        onStart();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowHistory.this,HomeActivity.class));
                overridePendingTransition(0,0);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference();

        FirebaseRecyclerOptions<Orders> options = new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(ordersRef.child("Orders").child(auth.getCurrentUser().getUid())
                        .child("History"),Orders.class).build();

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull OrdersViewHolder holder, int position, @NonNull @NotNull Orders model) {
                        holder.orderName.setText(model.getName());
                        holder.orderPhone.setText(model.getPhone());
                        holder.orderAddr.setText(model.getAddress());
                        holder.orderCity.setText(model.getCity());
                        holder.orderDate.setText(model.getDate());
                        holder.orderPrice.setText(model.getTotalAmount());
                    }

                    @NonNull
                    @NotNull
                    @Override
                    public OrdersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history,parent,false);
                        OrdersViewHolder holder=new OrdersViewHolder(view);
                        return holder;
                    }
                };

        historyList.setAdapter(adapter);
        adapter.startListening();
    }

    private void init_views() {
        historyList = findViewById(R.id.orders_list);
        back_btn = findViewById(R.id.backProfile);
    }
}