package com.raghava.shopaholic.menuFiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.raghava.shopaholic.HomeActivity;
import com.raghava.shopaholic.R;

public class baseActivity extends AppCompatActivity {

    RadioGroup radioGroup1;
    RadioButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        init_views();

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent i;


                if (checkedId == R.id.bottom_home) {
                    i = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }

                else if(checkedId ==  R.id.bottom_addprod) {
                    i = new Intent(getBaseContext(), AddProduct.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }

                else if(checkedId ==  R.id.bottom_search) {
                    i = new Intent(getBaseContext(), SearchActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }

                else if(checkedId == R.id.bottom_cart) {
                i = new Intent(getBaseContext(), CartActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
                    else if (checkedId == R.id.bottom_profile){
                    i = new Intent(getBaseContext(), ProfileActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }

            }
        });

    }

    private void init_views() {
        radioGroup1= (RadioGroup)findViewById(R.id.radioGroup1);
        home = (RadioButton)findViewById(R.id.bottom_home);
    }
}