package com.raghava.shopaholic.menuFiles;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
import com.raghava.shopaholic.HomeActivity;
import com.raghava.shopaholic.LoginActivity;
import com.raghava.shopaholic.R;
import com.raghava.shopaholic.ShowHistory;
import com.raghava.shopaholic.model.Users;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends baseActivity {

    LinearLayout dynamicContent,bottonNavBar;
    RadioGroup rg;
    RadioButton rb;

    EditText profileEmail,profileUsername;

    ImageView profile_bg,done;
    Uri setImageUri;

    CircleImageView profileImg;
    ImageView profileBack;
    TextView profileLogout,displayUsername,profileHistory;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog progressDialog;
    Dialog dialog;
    String emailid;

    RelativeLayout myPage;
    StorageReference storageReference;
    DatabaseReference reference;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        dynamicContent = (LinearLayout) findViewById(R.id.dynamicContent);
        bottonNavBar = (LinearLayout) findViewById(R.id.bottomNavBar);

        View wizard = getLayoutInflater().inflate(R.layout.activity_profile, null);
        dynamicContent.addView(wizard);

        rg = findViewById(R.id.radioGroup1);
        rb = findViewById(R.id.bottom_profile);
        rb.setBackgroundColor(R.color.item_selected);
        rb.setTextColor(Color.parseColor("#3F51B5"));

        profile_bg = findViewById(R.id.profileImage_bg);
        profileImg = findViewById(R.id.profileImage);
        profileUsername = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileEmail);
        displayUsername = findViewById(R.id.displayUsername);
        profileHistory = findViewById(R.id.profileHistory);
        done = findViewById(R.id.done);

        myPage = findViewById(R.id.myPage);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        if(auth.getCurrentUser() != null) {
            reference = database.getReference().child("users").
                    child(Objects.requireNonNull(auth.getCurrentUser()).getUid());
            storageReference = storage.getReference().child("upload").child(auth.getCurrentUser().getUid());
        }
        else{
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }


        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name,email;
                name = profileUsername.getText().toString();
                email = profileEmail.getText().toString();

                displayUsername.setText(name);

                if(setImageUri != null ){
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(auth.getUid(),name,email,finalImageUri);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileActivity.this,
                                                        "Changes Saved to database!!", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileActivity.this,
                                                        "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                            }
                                            startActivity(new Intent(ProfileActivity.this,
                                                    ProfileActivity.class));
                                            overridePendingTransition(0, 0);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else{
                    String defaultImgUrl = "https://firebasestorage.googleapis.com/v0/b/shopaholic-5aa80.appspot.com/o/profile_pic_logo.jpg?alt=media&token=dbb12738-22e5-4fb5-bd51-5665c9d5696e";
                    Users users = new Users(auth.getUid(),name,email,defaultImgUrl);
                    
                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this,
                                        "changes saved to database", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this,
                                        "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                            startActivity(new Intent(ProfileActivity.this,
                                    ProfileActivity.class));
                            overridePendingTransition(0, 0);
                        }
                    });
                }



            }
        });


        profile_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                // alpha = 0 ( Fade out )
                profile_bg.animate().alpha(0).setDuration(1000)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // alpha = 1 ( fade in )
                                profile_bg.animate().alpha(1).setDuration(1000).start();
                            }
                        })
                        .start();
            }
        });

        profileBack = findViewById(R.id.profileBack);
        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            }
        });

        profileLogout = findViewById(R.id.profileLogout);

        profileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(ProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_view);

                TextView yes_btn,no_btn;
                yes_btn = dialog.findViewById(R.id.yes_btn);
                no_btn = dialog.findViewById(R.id.no_btn);

                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        finish();
                    }
                });

                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        progressDialog.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                emailid = snapshot.child("email").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();

                profileUsername.setText(name);
                displayUsername.setText(name);
                profileEmail.setText(emailid);
                Picasso.get().load(image).into(profileImg);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        profileHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ShowHistory.class));
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        setImageUri=data.getData();

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),setImageUri);
                profileImg.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(ProfileActivity.this,"Error: "+e,Toast.LENGTH_SHORT).show();
                Log.e("Imgage upload error : ",e.toString());
                e.printStackTrace();
            }
        }

    }
}