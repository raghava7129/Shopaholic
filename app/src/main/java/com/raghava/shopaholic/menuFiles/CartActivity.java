package com.raghava.shopaholic.menuFiles;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
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
import com.raghava.shopaholic.PlaceOrderActivity;
import com.raghava.shopaholic.R;
import com.raghava.shopaholic.interfaces.DataParcelable;
import com.raghava.shopaholic.model.Cart;
import com.raghava.shopaholic.viewholder.CartViewHolder;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends baseActivity {

    ProgressDialog dialog;

    LinearLayout dynamicContent,bottonNavBar;
    RadioGroup rg;
    RadioButton rb;
    CardView no_item_view;

    int productNumb=1;

    FirebaseAuth auth;

    ProgressDialog progressDialog;

    Map<String, Map<String,String>> productsMap;
    Map<String,String> innerMap;

    private RecyclerView recyclerView;
    private AppCompatButton nextBtn;
    RecyclerView.LayoutManager layoutManager;
    TextView totalprice;
    private int overallPrice=0;

    DatabaseReference couponRef;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cart);

        getSupportActionBar().hide();

        couponRef = FirebaseDatabase.getInstance().getReference();
        couponRef = couponRef.child("Cart List").child("user View").child("Coupons");
        Map<String,Object> couponMap = new HashMap<>();
        couponMap.put("overAllCoupon","shopoholic");  // use this coupon code to avail 5% discount
                                        // on the overall purchase { use this coupon code at the time of payment !! }
        couponRef.updateChildren(couponMap);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("please wait...");

        progressDialog = new ProgressDialog(CartActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        dynamicContent = (LinearLayout) findViewById(R.id.dynamicContent);
        bottonNavBar = (LinearLayout) findViewById(R.id.bottomNavBar);

        View wizard = getLayoutInflater().inflate(R.layout.activity_cart, null);
        dynamicContent.addView(wizard);

        rg = findViewById(R.id.radioGroup1);
        rb = findViewById(R.id.bottom_cart);
        rb.setBackgroundColor(R.color.item_selected);
        rb.setTextColor(Color.parseColor("#3F51B5"));

        init_view();

        auth = FirebaseAuth.getInstance();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        nextBtn.setBackgroundResource(R.drawable.search_btn_bg);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalprice.getText().toString().equals("₹0") || totalprice.getText().toString().length()==0){
                    Toast.makeText(CartActivity.this, "Cannot place order with 0 items", Toast.LENGTH_SHORT).show();
                }
                else{

                    DataParcelable dataParcelable = new DataParcelable(productsMap);

                    Intent intent = new Intent(CartActivity.this, PlaceOrderActivity.class);
                    intent.putExtra("dataParcelable",dataParcelable);
                    intent.putExtra("totalAmount", totalprice.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();

        innerMap = new HashMap<>();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("user View").
                        child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position,@NonNull @NotNull Cart model) {
//                String name=model.getName().replaceAll("\n"," ");
                productsMap = new HashMap<>();
                String name = Objects.toString(model.getName(), "").replaceAll("\n", " ");

                holder.cartProductName.setText(name);
                holder.cartProductPrice.setText(model.getPrice());

                innerMap.put(name,model.getPrice());

                productsMap.put("product "+productNumb++,innerMap);

                String intPrice= model.getPrice().replace("₹","").replace(",","");
                overallPrice+=Integer.valueOf(intPrice);
                totalprice.setText("₹"+String.valueOf(overallPrice));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Delete item");
                        builder.setMessage("Do you want to remove this product from cart?")
                                .setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        progressDialog.show();

                                        cartListRef.child("user View")
                                                .child(auth.getCurrentUser().getUid())
                                                .child("Products").child(model.getPid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            progressDialog.dismiss();

                                                            String intPrice= model.getPrice().replace("₹","").replace(",","");
                                                            overallPrice-=Integer.valueOf(intPrice);
                                                            totalprice.setText("₹"+String.valueOf(overallPrice));
                                                            Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
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
            public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
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

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void init_view() {
        recyclerView = findViewById(R.id.cart_list);
        nextBtn = findViewById(R.id.next_button);
        totalprice=findViewById(R.id.totalprice);
        no_item_view = findViewById(R.id.no_item_view);
    }
}