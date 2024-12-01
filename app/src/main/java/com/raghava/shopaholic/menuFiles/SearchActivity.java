package com.raghava.shopaholic.menuFiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.raghava.shopaholic.HomeActivity;
import com.raghava.shopaholic.ProductDetails;
import com.raghava.shopaholic.R;
import com.raghava.shopaholic.model.AddProdModel;
import com.raghava.shopaholic.viewholder.ViewProductsHolder;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SearchActivity extends baseActivity {

    LinearLayout dynamicContent,bottonNavBar;
    RadioGroup rg;
    RadioButton rb;

    EditText inputText;
    AppCompatButton searchBtn;
    RecyclerView searchList;
    String searchInput;
    ImageView backHome;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);

        getSupportActionBar().hide();

        dynamicContent = (LinearLayout) findViewById(R.id.dynamicContent);
        bottonNavBar = (LinearLayout) findViewById(R.id.bottomNavBar);

        View wizard = getLayoutInflater().inflate(R.layout.activity_search, null);
        dynamicContent.addView(wizard);

        rg = findViewById(R.id.radioGroup1);
        rb = findViewById(R.id.bottom_search);
        rb.setBackgroundColor(R.color.item_selected);
        rb.setTextColor(Color.parseColor("#3F51B5"));

        inputText = findViewById(R.id.search_bar);
        searchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.searchList);
        backHome = findViewById(R.id.backHome);

        searchList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = inputText.getEditableText().toString();
                onStart();
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, HomeActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("View All").child("user View").child("products");

        FirebaseRecyclerOptions<AddProdModel> options = new FirebaseRecyclerOptions.Builder<AddProdModel>()
                .setQuery(databaseReference.orderByChild("name").startAt(searchInput),AddProdModel.class).build();

        FirebaseRecyclerAdapter<AddProdModel, ViewProductsHolder> adapter =
                new FirebaseRecyclerAdapter<AddProdModel, ViewProductsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ViewProductsHolder holder, int position, @NonNull @NotNull AddProdModel model) {
                String name = model.getName();
                       if(name != null) name.replaceAll("\n", " ");
                       else name = "";
                String price = model.getPrice();
                String imgUri = model.getImg();

                Picasso.get().load(imgUri).into(holder.addProductImg);
                holder.addProductName.setText(name);
                holder.addProductPrice.setText(price);

                String finalName = name;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(SearchActivity.this, ProductDetails.class);
                        intent.putExtra("id", 4);
                        intent.putExtra("uniqueId", finalName);
                        intent.putExtra("addProdName", finalName);
                        intent.putExtra("addProdPrice", price);
                        intent.putExtra("addProdDesc", model.getDescription());
                        intent.putExtra("addProdCategory", model.getCategory());
                        intent.putExtra("img", imgUri);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @NotNull
            @Override
            public ViewProductsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_products_adapter,parent,false);
                ViewProductsHolder holder = new ViewProductsHolder(view);
                return holder;
            }
        };

        searchList.setAdapter(adapter);
        adapter.startListening();

    }
}