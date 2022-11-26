package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    //declaring variables
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mRegisterText;
    ProgressBar mProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterText = findViewById(R.id.tv_registerNew);
        mProgressBar = findViewById(R.id.ProgressBar2);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required.");
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required.");
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Password must be more than 6 characters.");
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }

                // Authenticate the user
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in successfully, welcome" + mEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Home.class));
                        }
                        else{
                            Toast.makeText(Login.this, "Login Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update user data display accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            startActivity(new Intent(getApplicationContext(),Home.class));
            currentUser.reload();
        }
    }
}