package com.raghava.shopaholic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Locale;

public class PlaceOrderActivity extends AppCompatActivity implements PaymentResultListener {

    EditText shipName,shipPhone,shipAddress,shipCity;
    AppCompatButton confirmOrder_btn;
    TextView priceView;

    String totalAmount;
    int amount;

    FirebaseAuth auth;
    Intent intent;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng lastLatLng;
    private ProgressDialog progressDialog;

    private final static int REQUEST_CODE = 0;
    String setErrorString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        init_views();

        progressDialog = new ProgressDialog(PlaceOrderActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

        getSupportActionBar().hide();

        if(isInternetConnected()){

            if(isLocationEnabled(this)){

//                Toast.makeText(this, "inside if - 2", Toast.LENGTH_SHORT).show();

                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    LatLng lastLatLng = getLastLatLng();

//                    if(lastLatLng!=null){
//                        Toast.makeText(this, "got LatLng", Toast.LENGTH_SHORT).show();
//                        String address = getAddressFromLatLng(lastLatLng);
//                        if(address != null){
//                            Toast.makeText(this, "got Address", Toast.LENGTH_SHORT).show();
//                            shipAddress.setText("hey!!");
//                        }
//                        else{
//                            Toast.makeText(this, "Fill Address manually", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else{
//                        Toast.makeText(this, "LatLng null !!", Toast.LENGTH_SHORT).show();
//                    }
                }

            }else{
                Intent i = new Intent(PlaceOrderActivity.this,address_error_handling.class);
                i.putExtra("errorCode",0);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }

        }else{
            Intent i = new Intent(PlaceOrderActivity.this,address_error_handling.class);
            i.putExtra("errorCode",1);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        }



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

    private String getAddressFromLatLng(LatLng lastLatLng) {
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String location = "";

        progressDialog.show();
        try{

            addressList = geocoder.getFromLocation(lastLatLng.latitude,lastLatLng.longitude,1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                shipCity.setText(address.getLocality());
                if(address.getSubThoroughfare()!=null){
                    location+=address.getSubThoroughfare()+" ,";

                }
                else{
                    setErrorString+= "Your Building number ,";
                }
                if(address.getPremises()!=null){
                    location += address.getPremises()+" ,";

                }
                else{
                    setErrorString+= "Your building name ,";
                }
                if(address.getThoroughfare()!=null){
                    location += address.getThoroughfare()+" ,";

                }
                else{
                    setErrorString+= "Your street name ";
                }

                location+= address.getLocality()+ " ,"+address.getAdminArea()+" ,"+
                                address.getPostalCode()+" ,"+address.getCountryName();

                return location;
            }
            progressDialog.dismiss();
        }catch(Exception e){
            Log.e("GeoCoder Error : ",e.toString());
            Toast.makeText(this, "Please fill the Address manually", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        return null;
    }

    private LatLng getLastLatLng() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                progressDialog.show();
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
//                        Toast.makeText(PlaceOrderActivity.this, location.getLatitude()+" , "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                        lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        shipAddress.setText(getAddressFromLatLng(lastLatLng));
                        if(!setErrorString.equals("")) {

                            setErrorString = "add "+setErrorString+ " too!!";
                            shipAddress.setError(setErrorString);

                            Toast.makeText(PlaceOrderActivity.this, setErrorString, Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PlaceOrderActivity.this, "getLastLng success (location null)", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Location : ", "Error getting location: " + e.getMessage());
                    Toast.makeText(PlaceOrderActivity.this, "Error getting location: " +
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return lastLatLng;
        }
        return lastLatLng;
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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