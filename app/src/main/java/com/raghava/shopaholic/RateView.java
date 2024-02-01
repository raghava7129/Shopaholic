package com.raghava.shopaholic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RateView extends AppCompatActivity {

    ImageView backBtn;
    ImageView rateImg;
    TextView rateName,ratePrice;
    RatingBar ratingBar;
    AppCompatButton submit_btn;

    DatabaseReference databaseReference;
    FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_view);

        getSupportActionBar().hide();

        init_view();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait!!");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();

        String prodName = intent.getStringExtra("RateProdName");
        String prodPrice = intent.getStringExtra("RateProdPrice");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        final DatabaseReference reviewRef = databaseReference.child("View All").child("user View").child("products");

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

                progressDialog.show();

                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().
                        child("users").child(auth.getCurrentUser().getUid()).child("name");

                final String[] userName = new String[1];


               userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot snapshot) {

                       Object value = snapshot.getValue();
                       if(value!=null) {
                           userName[0] = value.toString();
                       }
                       else{
                           Toast.makeText(RateView.this, "could not fetch your name", Toast.LENGTH_SHORT).show();
                       }

                       HashMap<String,Object> ratingMap = new HashMap<>();
                       ratingMap.put("userId",auth.getCurrentUser().getUid());
                       ratingMap.put("Rating",ratingBar.getRating());
                       ratingMap.put("userName", userName[0]);

                       reviewRef.child(prodName).child("Rating").child(auth.getCurrentUser().getUid()).setValue(ratingMap)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(Task<Void> task){
                                       if(task.isSuccessful()){

                                           progressDialog.dismiss();
                                           Toast.makeText(RateView.this, "Thank your for sharing your experience !!", Toast.LENGTH_SHORT).show();
                                           startActivity(new Intent(RateView.this,ShowHistory.class));
                                           overridePendingTransition(0,0);
                                           finish();
                                       }
                                       else{
                                           Toast.makeText(RateView.this, "Something went wrong \nplease try again later!!", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });

                       float overAllRating = getOverAllRating(reviewRef,prodName);

                       int totalReviews = getReviewsCount(reviewRef,prodName);

//                       Toast.makeText(RateView.this, "overAllRating : "+overAllRating +
//                               "\ntotalReviews : "+totalReviews, Toast.LENGTH_LONG).show();

                       updateOverAllRating(reviewRef,prodName,overAllRating,totalReviews,ratingBar.getRating());


                   }

                   @Override
                   public void onCancelled(DatabaseError error) {

                   }
               });




            }
        });





    }

    private void updateOverAllRating(DatabaseReference reviewRef,String prodName, float overAllRating, int totalReviews,float currRating) {
        float newRating = ( (overAllRating*(totalReviews)) + currRating )/(totalReviews+1);
        reviewRef.child(prodName).child("overAllRating").setValue(newRating);
    }

    private int getReviewsCount(DatabaseReference reviewRef, String prodName) {
        final int[] noOfReviews = {0};
        reviewRef.child(prodName).child("Rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object val = snapshot.getChildrenCount();

                if(val != null){
                    noOfReviews[0] = ((Number) val).intValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return noOfReviews[0];
    }

    private float getOverAllRating(DatabaseReference reviewRef,String prodName) {
        final float[] overAllRating = {0};
        reviewRef.child(prodName).child("overAllRating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object val = snapshot.getValue();

                if(val != null){
                    overAllRating[0] = ((Number) val).floatValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        return overAllRating[0];
    }

    private void init_view() {
        backBtn = findViewById(R.id.backBtn);
        rateImg = findViewById(R.id.rateImg);
        rateName = findViewById(R.id.rateName);
        ratePrice = findViewById(R.id.ratePrice);
        ratingBar = findViewById(R.id.product_rating);
        submit_btn = findViewById(R.id.submit_btn);
    }
}