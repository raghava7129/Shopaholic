package com.raghava.shopaholic.menuFiles;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.raghava.shopaholic.HomeActivity;
import com.raghava.shopaholic.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProduct extends baseActivity {

    LinearLayout dynamicContent,bottonNavBar;
    RadioGroup rg;
    RadioButton rb;
    ImageView addProdImg, addProductBack;
    EditText addProdName, addProdPrice, addProdDesc, addProdCategory;
    TextView confirmAdd;

    FirebaseStorage storage;
    StorageReference storageReference;

    Uri setImageUri;
    String finalImageUri;

    ProgressDialog progressDialog;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_add_product);

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        dynamicContent = (LinearLayout) findViewById(R.id.dynamicContent);
        bottonNavBar = (LinearLayout) findViewById(R.id.bottomNavBar);

        View wizard = getLayoutInflater().inflate(R.layout.activity_add_product, null);
        dynamicContent.addView(wizard);

        rg = findViewById(R.id.radioGroup1);
        rb = findViewById(R.id.bottom_addprod);
        rb.setBackgroundColor(R.color.item_selected);
        rb.setTextColor(Color.parseColor("#3F51B5"));

        init_views();

        storage = FirebaseStorage.getInstance();

        addProdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);

            }
        });

        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= addProdName.getEditableText().toString();
                String price=addProdPrice.getEditableText().toString();
                String desc=addProdDesc.getEditableText().toString();
                String category=addProdCategory.getEditableText().toString();

                if(TextUtils.isEmpty(name)){
                    addProdName.setError("product name can`t be empty");
                    Toast.makeText(AddProduct.this, "Please fill all the details correctly before confirming", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(price)){
                    addProdPrice.setError("Please enter price of the product");
                    Toast.makeText(AddProduct.this, "Please fill all the details correctly before confirming", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(desc)){
                    addProdDesc.setError("Please enter description of the product");
                    Toast.makeText(AddProduct.this, "Please fill all the details correctly before confirming", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(category)){
                    addProdCategory.setError("Please mention category of the product");
                    Toast.makeText(AddProduct.this, "Please fill all the details correctly before confirming", Toast.LENGTH_SHORT).show();
                }
                else if (addProdImg == null ||
                        (addProdImg.getDrawable() != null &&
                                ((BitmapDrawable) addProdImg.getDrawable().getCurrent()).getBitmap().equals(
                                        ((BitmapDrawable) ContextCompat.getDrawable(AddProduct.this,
                                                R.drawable.add_product_icon)).getBitmap()))){
                    Toast.makeText(AddProduct.this, "Please upload a pic of the product", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.show();
                    storageReference = storage.getReference().child("products").child(name);
                    addingToProdList();
                }

            }
        });

        addProductBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddProduct.this, HomeActivity.class));

            }
        });

    }

    private void addingToProdList() {
        String currDate,currTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        currDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM:ss a");
        currTime = timeFormat.format(calendar.getTime());

        DatabaseReference prodListRef = FirebaseDatabase.getInstance().getReference().child("View All");
        String name = addProdName.getText().toString();

        HashMap<String,Object> prodMap = new HashMap<>();
        prodMap.put("pid",addProdName.getText().toString());
        prodMap.put("name",name);
        prodMap.put("date",currDate);
        prodMap.put("time",currTime);
        prodMap.put("description",addProdDesc.getText().toString());
        prodMap.put("price","₹"+addProdPrice.getText().toString());
        prodMap.put("category",addProdCategory.getText().toString());
        prodMap.put("overAllRating",0);

        if(setImageUri != null){
            storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            finalImageUri = uri.toString();
                            Log.i("final URL : ",finalImageUri);
                            prodMap.put("img",finalImageUri);

                            prodListRef.child("user View").child("products").child(addProdName.getText().toString())
                                    .updateChildren(prodMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProduct.this, "Thank you for helping the platform",Toast.LENGTH_SHORT ).show();
                                                startActivity(new Intent(AddProduct.this,HomeActivity.class));
                                                finish();
                                            }
                                        }
                                    });

                        }
                    });



                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        setImageUri=data.getData();

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),setImageUri);
                addProdImg.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(AddProduct.this,"Error: "+e,Toast.LENGTH_SHORT).show();
                Log.e("Imgage upload error : ",e.toString());
                e.printStackTrace();
            }
        }

    }

    private  void init_views(){
        addProdImg=findViewById(R.id.addProductImg);
        addProdName=findViewById(R.id.addProductName);
        addProdPrice=findViewById(R.id.addProductPrice);
        addProdDesc=findViewById(R.id.addProductDesc);
        addProdCategory=findViewById(R.id.addProductCategory);
        confirmAdd=findViewById(R.id.confirmAddProd);
        addProductBack=findViewById(R.id.addProductBack);
    }
}