package com.raghava.shopaholic;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class address_error_handling extends AppCompatActivity {

    AppCompatButton retry_btn;
    ImageView errorImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_error_handling);

        init_views();

        getSupportActionBar().hide();

        Intent i = getIntent();
        int error_code = i.getIntExtra("errorCode",0);
        if(error_code == 0){
            // location error !!
            errorImg.setImageResource(R.drawable.no_gps);
        }
        else{
            // Internet Error !!
            errorImg.setImageResource(R.drawable.no_wifi);
        }

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(error_code == 0){
                    if(isLocationEnabled(getApplicationContext())){
                        startActivity(new Intent(address_error_handling.this,PlaceOrderActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }else{
                        Toast.makeText(address_error_handling.this, "Check your Location connection or try again !!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(isInternetConnected()){
                        startActivity(new Intent(address_error_handling.this,PlaceOrderActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    else{
                        Toast.makeText(address_error_handling.this, "Check your Internet connection or try again !!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

    private void init_views() {
        retry_btn = findViewById(R.id.error_retry_btn);
        errorImg = findViewById(R.id.error_img);
    }
}