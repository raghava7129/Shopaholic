package com.raghava.shopaholic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class RateView extends AppCompatActivity {

    ImageView backBtn;
    ImageView rateImg;
    TextView rateName,ratePrice;
    EditText review;
    RatingBar ratingBar;
    AppCompatButton submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_view);

        getSupportActionBar().hide();

        init_view();

        Intent intent = getIntent();

        String prodName = intent.getStringExtra("RateProdName");
        String prodPrice = intent.getStringExtra("RateProdPrice");

        rateName.setText(prodName);
        ratePrice.setText(prodPrice);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RateView.this,last_order_products_view.class));
                overridePendingTransition(0,0);
                finish();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RateView.this, "Thank your for sharing your experience !!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(RateView.this,ShowHistory.class));
                overridePendingTransition(0,0);
                finish();
            }
        });

    }

    private void init_view() {
        backBtn = findViewById(R.id.backBtn);
        rateImg = findViewById(R.id.rateImg);
        rateName = findViewById(R.id.rateName);
        ratePrice = findViewById(R.id.ratePrice);
        review = findViewById(R.id.reviewText);
        ratingBar = findViewById(R.id.product_rating);
        submit_btn = findViewById(R.id.submit_btn);
    }
}