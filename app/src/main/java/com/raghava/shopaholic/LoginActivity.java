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

import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailEditText, passwordEditText;
    AppCompatButton signInButton;
    LinearLayout signUpText;
    String emailPattern= "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        init_views();

        askForFullScreen();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInCheck();
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private void signInCheck(){
        progressDialog.show();
        String email = emailEditText.getEditableText().toString();
        String password = passwordEditText.getEditableText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Enter valid data", Toast.LENGTH_SHORT).show();
        }
        else if(!email.matches(emailPattern)){
            emailEditText.setError("Invalid Email");
            Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }
        else if(password.length()<=6){
            progressDialog.dismiss();
            passwordEditText.setError("Invalid password");
            Toast.makeText(LoginActivity.this, "Please enter more than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error in login. Try again", Toast.LENGTH_SHORT).show();
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
        emailEditText=findViewById(R.id.loginEmail);
        passwordEditText=findViewById(R.id.loginPassword);
        signInButton=findViewById(R.id.signInButton);
        signUpText=findViewById(R.id.signUpText);

        signInButton.setBackgroundResource(R.drawable.intro_signin);
    }
}