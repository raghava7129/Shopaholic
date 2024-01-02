package com.raghava.shopaholic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raghava.shopaholic.model.Users;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    EditText regName, regEmail, regPass, regConfirmPass;
    String emailPattern= "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    AppCompatButton signUpButton;
    LinearLayout signInText;
    String imgUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        init_views();

        askForFullScreen();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                signUpCheck();
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void signUpCheck() {
        String name= regName.getEditableText().toString();
        String email=regEmail.getEditableText().toString();
        String password= regPass.getEditableText().toString();
        String cPassword= regConfirmPass.getEditableText().toString();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, "Enter valid data", Toast.LENGTH_SHORT).show();
        }
        else if(!email.matches(emailPattern)){
            progressDialog.dismiss();
            regEmail.setError("Invalid Email");
            Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }
        else if(password.length()<=6){
            progressDialog.dismiss();
            regPass.setError("Invalid password");
            Toast.makeText(RegisterActivity.this, "Please enter more than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(cPassword)){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        DatabaseReference reference = database.getReference().child("users").child(auth.getCurrentUser().getUid());
                        StorageReference storageReference = storage.getReference().child("upload").child(auth.getCurrentUser().getUid());

                        imgUri = "https://firebasestorage.googleapis.com/v0/b/shopaholic-5aa80.appspot.com/o/profile_pic_logo.jpg?alt=media&token=dbb12738-22e5-4fb5-bd51-5665c9d5696e";

                        Users users = new Users(auth.getUid(), name, email, imgUri);
                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Error occurred while creating a new user ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void askForFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void init_views() {
        regName=findViewById(R.id.regUsername);
        regEmail=findViewById(R.id.regEmail);
        regPass=findViewById(R.id.regPass);
        regConfirmPass=findViewById(R.id.regConfirmPass);

        signUpButton=findViewById(R.id.signUpButton);
        signUpButton.setBackgroundResource(R.drawable.intro_signin);

        signInText=findViewById(R.id.signInText);
    }

}