package com.raghava.shopaholic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class introActivity extends AppCompatActivity {

    TextView introSignIn, introSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_views();

        introSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(introActivity.this,LoginActivity.class));
                overridePendingTransition(0,0);
            }
        });

        introSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(introActivity.this,RegisterActivity.class));
                overridePendingTransition(0,0);
            }
        });

    }

    private void init_views() {
        introSignIn = findViewById(R.id.signInTextView);
        introSignUp = findViewById(R.id.signUpTextView);

        introSignIn.setBackgroundResource(R.drawable.intro_signin);
        introSignUp.setBackgroundResource(R.drawable.intro_signup);
    }
}