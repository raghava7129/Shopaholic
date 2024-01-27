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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raghava.shopaholic.model.fav_details;
import com.raghava.shopaholic.viewholder.favListViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class last_order_products_view extends AppCompatActivity {

    ImageView backBtn;
    RecyclerView prev_purch_list;

    FirebaseAuth auth;

    RecyclerView.LayoutManager layoutManager;
    int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_order_products_view);

        init_views();

        getSupportActionBar().hide();

        Intent i= getIntent();
        adapterPosition = i.getIntExtra("adapterPosition",0);

        auth = FirebaseAuth.getInstance();

        prev_purch_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        prev_purch_list.setLayoutManager(layoutManager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(last_order_products_view.this, ShowHistory.class));
                overridePendingTransition(0,0);
                finish();
            }
        });

        fetchData();
    }

    private void init_views() {
        backBtn = findViewById(R.id.backCart);
        prev_purch_list = findViewById(R.id.prev_purch_list);
    }

    private void fetchData() {
        final String currDate, currTime;

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        currDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currTime = timeFormat.format(calendar.getTime());

        final DatabaseReference ordersRef_Products = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(auth.getCurrentUser().getUid()).child("History");

        ordersRef_Products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){

                int currPos = 0;

                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    if(currPos == adapterPosition){
                        DatabaseReference Ref_at_position = childSnapshot.getRef();

                        FirebaseRecyclerOptions<fav_details> options = new FirebaseRecyclerOptions.Builder<fav_details>()
                                .setQuery(Ref_at_position.child("products"),fav_details.class).build();

                        FirebaseRecyclerAdapter<fav_details, favListViewHolder> adapter = new FirebaseRecyclerAdapter<fav_details, favListViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(favListViewHolder holder, int position, fav_details model) {
                                holder.fav_priceView.setText(model.getPrice());
                                holder.fav_nameView.setText(model.getName());

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                       Intent i = new Intent(last_order_products_view.this,RateView.class);
                                       i.putExtra("RateProdName",model.getName());
                                       i.putExtra("RateProdPrice",model.getPrice());
                                       startActivity(i);
                                        overridePendingTransition(0,0);
                                    }
                                });

                            }

                            @Override
                            public favListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_list_child_view,parent,false);
                                favListViewHolder holder=new favListViewHolder(view);
                                return holder;
                            }
                        };

                        prev_purch_list.setAdapter(adapter);
                        adapter.startListening();

                        break;

                    }
                    else currPos++;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

}
