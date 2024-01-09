package com.raghava.shopaholic;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity implements PaymentResultListener {

    EditText shipName,shipPhone,shipAddress,shipCity;
    AppCompatButton confirmOrder_btn;
    TextView priceView;

    String totalAmount;
    int amount;

    FirebaseAuth auth;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        init_views();

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        intent = getIntent();
        totalAmount = intent.getStringExtra("totalAmount");
        priceView.setText(totalAmount);

        String sample_amount="1"; // this amount will be displayed on the razorpay payment gateway !!

        //convert and round off
        amount=Math.round(Float.parseFloat(sample_amount)*100);

        confirmOrder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });


    }

    private void check() {
        if(TextUtils.isEmpty(shipName.getText().toString())){
            shipName.setError("Enter name");
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(shipPhone.getText().toString())){
            shipPhone.setError("Enter phone no.");
            Toast.makeText(this, "Please enter your phone no.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(shipAddress.getText().toString())){
            shipAddress.setError("Enter address");
            Toast.makeText(this, "Please provide your address", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(shipCity.getText().toString())){
            shipCity.setError("Enter phone no.");
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
        }else{
            paymentFunc();
        }
    }

    private void paymentFunc() {
        Checkout checkout = new Checkout();

        String id="rzp_test_k2xvsKoKqhTaa0";  // this is a test razor pay gateway id

        checkout.setKeyID(id);
        checkout.setImage(R.drawable.razorpay_logo);

        JSONObject obj = new JSONObject();
        try {
            obj.put("name", shipName.getText().toString());
            obj.put("description","paid: " +amount);
            obj.put("currency","INR");
            obj.put("amount",amount);
            obj.put("prefill.contact",shipPhone.getText().toString());
            obj.put("prefill.email","ShopaholicUser@rzp.com");

            checkout.open(PlaceOrderActivity.this,obj);

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void init_views() {
        shipName = findViewById(R.id.shipName);
        shipCity = findViewById(R.id.shipCity);
        shipAddress = findViewById(R.id.shipAddress);
        shipPhone = findViewById(R.id.shipPhone);

        confirmOrder_btn = findViewById(R.id.confirmOrder);
        priceView = findViewById(R.id.cartpricetotal);
    }

    @Override
    public void onPaymentSuccess(String s) {
        confirmOrderFunc();
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Payment ID");
        builder.setMessage(s);
        builder.show();
    }

    private void confirmOrderFunc() {
        final String currDate, currTime;

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        currDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currTime = timeFormat.format(calendar.getTime());

        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(auth.getCurrentUser().getUid()).child("History")
                .child(currDate.replaceAll("/","-")+" "+currTime);

        HashMap<String, Object> ordersMap= new HashMap<>();
        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",shipName.getText().toString());
        ordersMap.put("phone",shipPhone.getText().toString());
        ordersMap.put("address",shipAddress.getText().toString());
        ordersMap.put("city",shipCity.getText().toString());
        ordersMap.put("date",currDate);
        ordersMap.put("time",currTime);

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("user View").child(auth.getCurrentUser().getUid())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PlaceOrderActivity.this, "Your order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intentcart= new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                        intentcart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intentcart);
                                        finish();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(PlaceOrderActivity.this, "unable to update payment data on database",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}