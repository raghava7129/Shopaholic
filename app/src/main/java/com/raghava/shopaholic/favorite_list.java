package com.raghava.shopaholic;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.raghava.shopaholic.model.fav_details;
import com.raghava.shopaholic.viewholder.favListViewHolder;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class favorite_list extends AppCompatActivity {

    RecyclerView fav_list_view;
    ImageView back_btn;
    TextView no_item_view;

    FirebaseAuth auth;

    FirebaseRecyclerAdapter firebaseAdapter;
    RecyclerView.LayoutManager layoutManager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        init_views();

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait...");

        auth = FirebaseAuth.getInstance();

        layoutManager = new LinearLayoutManager(favorite_list.this);
        fav_list_view.setLayoutManager(layoutManager);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(favorite_list.this,HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

//        onStart();

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("users").
                child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("Favorite List");

        FirebaseRecyclerOptions<fav_details> options = new FirebaseRecyclerOptions.Builder<fav_details>().
                setQuery(favListRef,fav_details.class).build();

        FirebaseRecyclerAdapter<fav_details, favListViewHolder> adapter = new FirebaseRecyclerAdapter<fav_details, favListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull favListViewHolder holder, int position,@NonNull @NotNull fav_details model) {

                holder.fav_nameView.setText(model.getName());
                holder.fav_priceView.setText(model.getPrice());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                         AlertDialog.Builder builder = new AlertDialog.Builder(favorite_list.this);

                        builder.setTitle("Delete item");
                        builder.setMessage("Do you want to remove this product from favourite list?")
                                .setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        progressDialog.show();

                                        favListRef.child(model.getName()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            Toast.makeText(favorite_list.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        builder.show();
                    }
                });

            }

            @Override
            public favListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.fav_list_child_view,parent,false);
                favListViewHolder holder = new favListViewHolder(view);
                return holder;
            }

            @Override
            public int getItemCount() {
                int itemCount = super.getItemCount();
                return itemCount;
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();


                int itemCount = getItemCount();

                if (itemCount == 0) {
                    no_item_view.setVisibility(View.VISIBLE);
                } else {
                    no_item_view.setVisibility(View.GONE);
                }
            }



        };

        fav_list_view.setAdapter(adapter);
        adapter.startListening();



    }

    private void init_views() {
        fav_list_view = findViewById(R.id.favorite_list);
        back_btn = findViewById(R.id.back_btn);
        no_item_view = findViewById(R.id.no_item_view);
    }
}