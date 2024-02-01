package com.raghava.shopaholic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raghava.shopaholic.interfaces.RatingCallback;

import org.checkerframework.checker.nullness.qual.NonNull;

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

    FirebaseStorage storage;

    EditText reviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_view);

        getSupportActionBar().hide();

        init_view();

        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait!!");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();

        String prodName = intent.getStringExtra("RateProdName");
        String prodPrice = intent.getStringExtra("RateProdPrice");

//        Toast.makeText(this, prodName, Toast.LENGTH_SHORT).show();
        
        String imgPathJpeg = "products/"+prodName.trim()+".jpeg";
        String imgPathJpg = "products/"+prodName.trim()+".jpg";

        StorageReference storageRefJpeg = storage.getReference().child(imgPathJpeg);
        StorageReference storageRefJpg = storage.getReference().child(imgPathJpg);

        storageRefJpeg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(RateView.this).load(uri).into(rateImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                storageRefJpg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(RateView.this).load(uri).into(rateImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(RateView.this, "Image not found", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

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

                       if(reviewText.getText() == null || reviewText.getText().equals("")){
                           reviewText.setError("Please share your experience!!");
                           ratingMap.put("review", null);
                       }else{
                           ratingMap.put("review", reviewText.getText().toString());
                       }

                       reviewRef.child(prodName).child("Rating").child(auth.getCurrentUser().getUid()).setValue(ratingMap)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(Task<Void> task){
                                       if(task.isSuccessful()){

                                           progressDialog.dismiss();

                                           getOverAllRating(reviewRef, prodName, new RatingCallback() {

                                               @Override
                                               public void onRatingCountReceived(float rating) {

                                                   updateOverAllRating(reviewRef, prodName, rating, ratingBar.getRating());

                                                   Toast.makeText(RateView.this, "Thank your for sharing your experience !!", Toast.LENGTH_SHORT).show();
                                                   startActivity(new Intent(RateView.this,ShowHistory.class));
                                                   overridePendingTransition(0,0);
                                                   finish();

                                               }
                                           });
                                       } else {
                                           Toast.makeText(RateView.this, "Something went wrong, please try again later!", Toast.LENGTH_SHORT).show();
                                       }



                                   }
                               });
                   }

                   @Override
                   public void onCancelled(DatabaseError error) {

                   }
               });




            }
        });





    }

    private void updateOverAllRating(DatabaseReference reviewRef, String prodName, float overAllRating, float currRating) {
        getOverAllRating(reviewRef, prodName, new RatingCallback() {
            @Override
            public void onRatingCountReceived(float rating) {
                if (!Float.isNaN(rating) && !Float.isInfinite(rating)) {
                    reviewRef.child(prodName).child("overAllRating").setValue(rating);
                }
            }
        });
    }


    private void getOverAllRating(DatabaseReference reviewRef,String prodName, RatingCallback callback) {
        final float[] overAllRating = {0};
        final int[] numberOfRatings = {0};

        reviewRef.child(prodName).child("Rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object val = snapshot.getValue();

                if(val != null){

                    numberOfRatings[0] = (int) snapshot.getChildrenCount();

//                    Toast.makeText(RateView.this, "count : "+numberOfRatings[0], Toast.LENGTH_SHORT).show();

                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        Object childValue = snapshot1.child("Rating").getValue();
                        if (childValue instanceof Number) {
                            overAllRating[0] += ((Number) childValue).floatValue();
                        }
                    }

//                    Toast.makeText(RateView.this, "overAll : "+overAllRating[0], Toast.LENGTH_SHORT).show();
                }
                float overallRating = (numberOfRatings[0] > 0) ? overAllRating[0] / numberOfRatings[0] : 0;
                callback.onRatingCountReceived(overallRating);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void init_view() {
        backBtn = findViewById(R.id.backBtn);
        rateImg = findViewById(R.id.rateImg);
        rateName = findViewById(R.id.rateName);
        ratePrice = findViewById(R.id.ratePrice);
        ratingBar = findViewById(R.id.product_rating);
        submit_btn = findViewById(R.id.submit_btn);

        reviewText =findViewById(R.id.reviewText);
    }
}