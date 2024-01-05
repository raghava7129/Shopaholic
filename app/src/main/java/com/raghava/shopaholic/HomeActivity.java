package com.raghava.shopaholic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raghava.shopaholic.menuFiles.CartActivity;
import com.raghava.shopaholic.menuFiles.SearchActivity;
import com.raghava.shopaholic.menuFiles.baseActivity;
import com.raghava.shopaholic.viewholder.SliderAdapter;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class HomeActivity extends baseActivity {

    Toolbar toolbar;
    SliderView sliderView;
    TextView prod1Name,prod2Name,prod3Name,prod4Name;
    TextView prod1Price,prod2Price,prod3Price,prod4Price;
    ListView lv;

    RadioGroup rg;
    RadioButton rb;

    FirebaseStorage storage;
    StorageReference storageReference;

    int[] images = {R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four};

    CardView product1,product2,product3,product4;
    TextView viewAll;

    public static ImageView home_cart;

    Intent intentcart;
    String getcartupdate;

    LinearLayout dynamicContent;
    LinearLayout bottonNavBar;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        init_views();

        storage = FirebaseStorage.getInstance();

        toolbar.setBackgroundColor(R.drawable.toolbar_bg);

        View wizard = getLayoutInflater().inflate(R.layout.activity_home, null);
        dynamicContent.addView(wizard);

        rb.setBackgroundColor(R.color.item_selected);
        rb.setTextColor(Color.parseColor("#3F51B5"));

        SliderAdapter sliderAdapter = new SliderAdapter(images);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(HomeActivity.this,productActivity.class);
              i.putExtra("name",prod1Name.getText().toString());
                i.putExtra("price", prod1Price.getText().toString());
                i.putExtra("uniqueId", prod1Name.getText().toString());
                i.putExtra("id", 1);
              startActivity(i);
              overridePendingTransition(0, 0);
            }
        });

        product2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,productActivity.class);
                i.putExtra("name",prod2Name.getText().toString());
                i.putExtra("price", prod2Price.getText().toString());
                i.putExtra("uniqueId", prod2Name.getText().toString());
                i.putExtra("id", 2);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        product3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,productActivity.class);
                i.putExtra("name",prod3Name.getText().toString());
                i.putExtra("price", prod3Price.getText().toString());
                i.putExtra("uniqueId", prod3Name.getText().toString());
                i.putExtra("id", 1);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        product4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,productActivity.class);
                i.putExtra("name",prod4Name.getText().toString());
                i.putExtra("price", prod4Price.getText().toString());
                i.putExtra("uniqueId", prod4Name.getText().toString());
                i.putExtra("id", 2);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });

        home_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


    }

    private void init_views(){
        dynamicContent = (LinearLayout) findViewById(R.id.dynamicContent);
        bottonNavBar = (LinearLayout) findViewById(R.id.bottomNavBar);

        rg = findViewById(R.id.radioGroup1);
        rb = findViewById(R.id.bottom_home);

        sliderView = findViewById(R.id.image_slider);
        toolbar = findViewById(R.id.toolbar);

        product1 = findViewById(R.id.product1);
        product2 = findViewById(R.id.product2);
        product3 = findViewById(R.id.product3);
        product4 = findViewById(R.id.product4);

        prod1Name = findViewById(R.id.prod1name);
        prod2Name = findViewById(R.id.prod2name);
        prod3Name = findViewById(R.id.prod3name);
        prod4Name = findViewById(R.id.prod4name);

        prod1Price = findViewById(R.id.prod1price);
        prod2Price = findViewById(R.id.prod2price);
        prod3Price = findViewById(R.id.prod3price);
        prod4Price = findViewById(R.id.prod4price);

        viewAll = findViewById(R.id.viewAllProducts);
        home_cart = findViewById(R.id.home_cart);

        lv = findViewById(R.id.productslist);
    }


}