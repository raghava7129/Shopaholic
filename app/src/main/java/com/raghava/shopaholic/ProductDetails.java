package com.raghava.shopaholic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raghava.shopaholic.model.AddProdModel;
import com.raghava.shopaholic.viewholder.RelatedProductsHolder;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ProductDetails extends AppCompatActivity {

    Intent intent;
    ImageView productImg;
    TextView productName, productCategory, productDesc, productPrice;
    AppCompatButton order,addFavBtn,statsBtn;
    Toolbar detailsToolbar;

    RatingBar ratingBar;
    TextView ratingTitle;

    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    String uniqueId;
    ImageView back;
    RecyclerView related_prod_list;
    String relCategory;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        init_views();

        progressDialog = new ProgressDialog(ProductDetails.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        related_prod_list.setLayoutManager(
                new LinearLayoutManager(ProductDetails.this,LinearLayoutManager.HORIZONTAL,true));
        intent = getIntent();
        productCategory.setText(intent.getStringExtra("category"));
        relCategory= productCategory.getText().toString();

        int id = intent.getIntExtra("id",1);
        uniqueId= intent.getStringExtra("uniqueId").replace("\n"," ");

        final DatabaseReference avgRatingRef = databaseReference.child("View All").child("user View").child("products");

        if(id == 1){
//            Toast.makeText(this, "in id =1 block", Toast.LENGTH_SHORT).show();
            String checkName = intent.getStringExtra("name").replace("\n"," ");
            if(checkName.equals("iPhone 11 White")){
                productName.setText(intent.getStringExtra("name").replaceAll("\n"," "));
                productPrice.setText(intent.getStringExtra("price"));
                productDesc.setText(intent.getStringExtra("description"));
                productImg.setImageResource(R.drawable.prod1);
            }
            else{
                productName.setText(intent.getStringExtra("name").replaceAll("\n"," "));
                productPrice.setText(intent.getStringExtra("price"));
                productDesc.setText(intent.getStringExtra("description"));
                productImg.setImageResource(R.drawable.prod3);
            }
        }
        else if(id == 2){
//            Toast.makeText(this, "in id =2 block", Toast.LENGTH_SHORT).show();
            String checkName = intent.getStringExtra("name").replace("\n"," ");
            if(checkName.equals("Apple MacBook Air")){
                productName.setText(intent.getStringExtra("name").replaceAll("\n"," "));
                productPrice.setText(intent.getStringExtra("price"));
                productDesc.setText(intent.getStringExtra("description"));
                productImg.setImageResource(R.drawable.prod2);
            }
            else{
                productName.setText(intent.getStringExtra("name").replaceAll("\n"," "));
                productPrice.setText(intent.getStringExtra("price"));
                productDesc.setText(intent.getStringExtra("description"));
                productImg.setImageResource(R.drawable.prod4);
            }
        }
        else if(id == 3){
            productName.setText(intent.getStringExtra("name").replaceAll("\n"," "));
            productPrice.setText(intent.getStringExtra("pprice"));
            String img = intent.getStringExtra("imageName");
            productDesc.setText(intent.getStringExtra("description"));
            productImg.setImageResource(this.getResources().getIdentifier(img, "drawable", this.getPackageName()));
        }
        else{
            productName.setText(intent.getStringExtra("addProdName"));
            productPrice.setText(intent.getStringExtra("addProdPrice"));
            productCategory.setText(intent.getStringExtra("addProdCategory"));
            productDesc.setText(intent.getStringExtra("addProdDesc"));
            String img = intent.getStringExtra("img");
            Picasso.get().load(img).into(productImg);
        }

        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDetails.this,stats.class);
                i.putExtra("productName",productName.getText().toString());
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });

        avgRatingRef.child(productName.getText().toString()).child("overAllRating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object val = snapshot.getValue();
                if(val !=null){
                    float avgRating = ((Number) val).floatValue();
                    ratingBar.setRating(avgRating);

                    ratingTitle.setText(avgRating+" out-off 5 Rating !!");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetails.this,HomeActivity.class));
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                addingToCartList();
            }
        });

        addFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                addingToFavList();
            }
        });



//        onStart();

    }

    private void addingToCartList() {
        String currDate,currTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        currDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM:ss a");
        currTime = timeFormat.format(calendar.getTime());

        DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("date",currDate);
        cartMap.put("time",currTime);
        cartMap.put("pid",productName.getText().toString());
        cartMap.put("name",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());

        if(auth.getCurrentUser() != null) {
            cartListRef.child("user View").child(auth.getCurrentUser().getUid()).child("Products")
                    .child(uniqueId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(ProductDetails.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                Intent intentcart = new Intent(ProductDetails.this, HomeActivity.class);
                                intentcart.putExtra("cartadd", "yes");
                                startActivity(intentcart);
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }

    }

    private void addingToFavList(){
        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("users").
                child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("Favorite List");

        final HashMap<String,Object> favMap = new HashMap<>();
        favMap.put("name",productName.getText().toString());
        favMap.put("price",productPrice.getText().toString());

        if(auth.getCurrentUser() != null) {
            favListRef.child(uniqueId).updateChildren(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(ProductDetails.this, "Added to Favorite List", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference prodListRef = FirebaseDatabase.getInstance().getReference().child("View All")
                .child("user View").child("Products");

//        Toast.makeText(this, "category: "+relCategory, Toast.LENGTH_SHORT).show();

        FirebaseRecyclerOptions<AddProdModel> options = new FirebaseRecyclerOptions.Builder<AddProdModel>()
                .setQuery(prodListRef.orderByChild("category").startAt(relCategory.toLowerCase(Locale.ROOT)), AddProdModel.class).build();



        FirebaseRecyclerAdapter<AddProdModel, RelatedProductsHolder> adapter =
                new FirebaseRecyclerAdapter<AddProdModel, RelatedProductsHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull RelatedProductsHolder holder, int position,@NonNull @NotNull AddProdModel model) {
                        name = model.getName();
                        String price = model.getPrice();
                        String imgUri = model.getImg();

                        holder.relatedProdName.setText(name);
                        holder.relatedProdPrice.setText(price);
                        Picasso.get().load(imgUri).into(holder.relatedProdImg);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ProductDetails.this, ProductDetails.class);
                                intent.putExtra("id", 4);
                                intent.putExtra("uniqueId", name);
                                intent.putExtra("addProdName", name);
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
                    public RelatedProductsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.related_products_adapter, parent, false);
                        RelatedProductsHolder holder = new RelatedProductsHolder(view);
                        return holder;
                    }
                };

        related_prod_list.setAdapter(adapter);
        adapter.startListening();

    }

    private void init_views() {
        productImg = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productCategory = findViewById(R.id.product_category);
        productDesc=findViewById(R.id.product_description);
        productPrice=findViewById(R.id.product_price);
        detailsToolbar=findViewById(R.id.detailsToolbar);

        back=findViewById(R.id.product_back);
        order=findViewById(R.id.order);

        related_prod_list = findViewById(R.id.related_prod_list);

        addFavBtn = findViewById(R.id.addToFavBtn);

        ratingBar = findViewById(R.id.ratingBar);
        ratingTitle = findViewById(R.id.ratingTitle);

        statsBtn = findViewById(R.id.statsBtn);
    }
}