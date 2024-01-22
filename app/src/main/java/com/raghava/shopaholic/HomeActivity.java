package com.raghava.shopaholic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raghava.shopaholic.constant.Constant;
import com.raghava.shopaholic.menuFiles.CartActivity;
import com.raghava.shopaholic.menuFiles.SearchActivity;
import com.raghava.shopaholic.menuFiles.baseActivity;
import com.raghava.shopaholic.model.Product;
import com.raghava.shopaholic.viewholder.ProductAdapter;
import com.raghava.shopaholic.viewholder.SliderAdapter;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

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

        storage = FirebaseStorage.getInstance();

        dynamicContent = (LinearLayout) findViewById(R.id.dynamicContent);
        bottonNavBar = (LinearLayout) findViewById(R.id.bottomNavBar);

        View wizard = getLayoutInflater().inflate(R.layout.activity_home, null);
        dynamicContent.addView(wizard);

        rg = findViewById(R.id.radioGroup1);
        rb = findViewById(R.id.bottom_home);
        rb.setBackgroundColor(R.color.item_selected);
        rb.setTextColor(Color.parseColor("#3F51B5"));

        // Slider View
        sliderView = findViewById(R.id.image_slider);

        SliderAdapter sliderAdapter = new SliderAdapter(images);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        // Deals of the Day
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

        product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(HomeActivity.this,ProductDetails.class);
              i.putExtra("name",prod1Name.getText().toString());
                i.putExtra("price", prod1Price.getText().toString());
                i.putExtra("uniqueId", prod1Name.getText().toString());
                i.putExtra("description","Display: 6.1 Inches with a resolution of 1792 x 828 Pixels and pixel density, Memory: 4 GB RAM and 128 GB internal memory, Color options: White, Battery Capacity:: 3110 mAh battery");
                i.putExtra("id", 1);
                i.putExtra("category", "smartPhone");
              startActivity(i);
              overridePendingTransition(0, 0);
            }
        });

        product2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ProductDetails.class);
                i.putExtra("name",prod2Name.getText().toString());
                i.putExtra("price", prod2Price.getText().toString());
                i.putExtra("uniqueId", prod2Name.getText().toString());
                i.putExtra("description","16-core Neural Engine, 38.91 cm (15.3-inch) Liquid Retina display with True Tone, 1080p FaceTime HD camera, MagSafe 3 charging port, Two Thunderbolt / USB 4 ports, Magic Keyboard with Touch ID, Force Touch trackpad, 35W Dual USB-C Port Power Adapter");
                i.putExtra("id", 2);
                i.putExtra("category", "laptops");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        product3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ProductDetails.class);
                i.putExtra("name",prod3Name.getText().toString());
                i.putExtra("price", prod3Price.getText().toString());
                i.putExtra("uniqueId", prod3Name.getText().toString());
                i.putExtra("description","DCI-P3 93% Colour Gamut 64-bit Powerful Processor Android TV 9.0 OxygenPlay Bezel-Less Design");
                i.putExtra("id", 1);
                i.putExtra("category", "TV");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        product4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ProductDetails.class);
                i.putExtra("name",prod4Name.getText().toString());
                i.putExtra("price", prod4Price.getText().toString());
                i.putExtra("uniqueId", prod4Name.getText().toString());
                i.putExtra("description","Black/University Red - white , Original release : August 2016 , Designed by Tate kuerbis");
                i.putExtra("id", 2);
                i.putExtra("category", "Shoes");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        viewAll = findViewById(R.id.viewAllProducts);
        home_cart = findViewById(R.id.home_cart);

        lv = findViewById(R.id.productslist);

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


        // Top products !!
        ProductAdapter productAdapter = new ProductAdapter(this);
        productAdapter.updateProducts(Constant.PRODUCT_LIST);

        lv.setAdapter(productAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Product product = Constant.PRODUCT_LIST.get(position);
                Intent intent = new Intent(HomeActivity.this, ProductDetails.class);
                intent.putExtra("id", 3);
                intent.putExtra("uniqueId", product.getpName());
                intent.putExtra("name", product.getpName());
                intent.putExtra("description", product.getpDescription());
                intent.putExtra("category", "Smartphone");
                intent.putExtra("pprice", Constant.CURRENCY +
                        String.valueOf(product.getpPrice().setScale(0, BigDecimal.ROUND_HALF_UP)));
                intent.putExtra("imageName", product.getpImageName());
                Log.d("TAG", "View product: " + product.getpName());
                startActivity(intent);
            }
        });

//        addingToProdList();

    }

    private void addingToProdList(){
        String currDate,currTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        currDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM:ss a");
        currTime = timeFormat.format(calendar.getTime());

        DatabaseReference prodListRef = FirebaseDatabase.getInstance().getReference().child("View All");
        String name = prod1Name.getText().toString().replaceAll("\n"," ").replaceAll(".",",");

        final HashMap<String,Object> prodMap = new HashMap<>();
        prodMap.put("date", currDate);
        prodMap.put("time", currTime);
        prodMap.put("pid",name);
        prodMap.put("name",name);
        prodMap.put("price","49,000");
        prodMap.put("category","smartPhone");
        prodMap.put("description","Display: 6.1 Inches with a resolution of 1792 x 828 Pixels and pixel density\n" +
                "Memory: 4 GB RAM and 128 GB internal memory\n" +
                "Color options: White\n" +
                "Battery Capacity: 3110 mAh battery");

        prodListRef.child("user View").child("products").
                child(name).updateChildren(prodMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        Log.i("added: ",name);
                        Toast.makeText(HomeActivity.this, "added: "+name, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}