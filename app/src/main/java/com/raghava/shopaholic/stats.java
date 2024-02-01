package com.raghava.shopaholic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raghava.shopaholic.interfaces.ReviewCountCallback;

public class stats extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth auth;

    TextView name;
    ImageView backBtn;

    ProgressBar p5,p4,p3,p2,p1;
    TextView perct5,perct4,perct3,perct2,perct1,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        init_views();

        getSupportActionBar().hide();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(stats.this,ProductDetails.class));
                overridePendingTransition(0,0);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        String productName = i.getStringExtra("productName");

        name.setText(productName);

        setProgressValues(productName);

    }

    private void setProgressValues(String productName) {
        final DatabaseReference updateProref =  databaseReference.child("View All")
                .child("user View").child("products").child(productName).child("Rating");

        int[] numStars = new int[5];

        numStars[0]=0; numStars[1]=0; numStars[2]=0; numStars[3]=0; numStars[4]=0;

        updateProref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot childSnap: snapshot.getChildren()){
                    Object childValue = childSnap.child("Rating").getValue();

                    if(childValue != null){
                        float val = ( (Number) childValue).floatValue();

                        int startVal = (int) Math.ceil(val);

                       if(startVal >= 1) numStars[startVal-1]++;


                    }
                }

                getTotalReviewsCount(updateProref, new ReviewCountCallback() {
                    @Override
                    public void onReviewCountReceived(int total_reviews) {
                        title.setText(total_reviews+" customer ratings");

//                for(int i=0;i<5;i++) total_reviews += numStars[i];

                        if(total_reviews == 0){
                            p5.setProgress(0); p4.setProgress(0); p3.setProgress(0);
                            p2.setProgress(0); p1.setProgress(0);

                            String s5 = "0%"; String s4 = "0%"; String s3 = "0%";
                            String s2 = "0%"; String s1 = "0%";

                            perct5.setText(s5); perct4.setText(s4); perct3.setText(s3);
                            perct2.setText(s2); perct1.setText(s1);

                        }else {

                            p5.setProgress((int) (100 * ((float) numStars[4] / total_reviews)));
                            p4.setProgress((int) (100 * ((float) numStars[3] / total_reviews)));
                            p3.setProgress((int) (100 * ((float) numStars[2] / total_reviews)));
                            p2.setProgress((int) (100 * ((float) numStars[1] / total_reviews)));
                            p1.setProgress((int) (100 * ((float) numStars[0] / total_reviews)));


                            String s5 = String.format("%.1f%%", 100.0 * ((float) numStars[4] / total_reviews));
                            String s4 = String.format("%.1f%%", 100.0 * ((float) numStars[3] / total_reviews));
                            String s3 = String.format("%.1f%%", 100.0 * ((float) numStars[2] / total_reviews));
                            String s2 = String.format("%.1f%%", 100.0 * ((float) numStars[1] / total_reviews));
                            String s1 = String.format("%.1f%%", 100.0 * ((float) numStars[0] / total_reviews));

                            perct5.setText(s5);
                            perct4.setText(s4);
                            perct3.setText(s3);
                            perct2.setText(s2);
                            perct1.setText(s1);
                        }
                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



    }

    private void getTotalReviewsCount(DatabaseReference ref,  ReviewCountCallback callback) {
        int[] totalCount = {0};
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    totalCount[0] = (int) snapshot.getChildrenCount();
                    callback.onReviewCountReceived(totalCount[0]);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void init_views() {
        name = findViewById(R.id.ProductName);
        backBtn = findViewById(R.id.product_details_back);
        title = findViewById(R.id.title);

        p5 = findViewById(R.id.pro5);
        p4 = findViewById(R.id.pro4);
        p3 = findViewById(R.id.pro3);
        p2 = findViewById(R.id.pro2);
        p1 = findViewById(R.id.pro1);

        perct5 = findViewById(R.id.per5);
        perct4 = findViewById(R.id.per4);
        perct3 = findViewById(R.id.per3);
        perct2 = findViewById(R.id.per2);
        perct1 = findViewById(R.id.per1);

    }
}