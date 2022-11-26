package com.example.androidexpense;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidexpense.helperclass.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText  mEmail, mPassword, mConfirmPassword;
    Button mRegisterBtn;
    TextView mLoginText;
    ProgressBar mProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.et_email) ;
        mPassword = findViewById(R.id.et_password);
        mConfirmPassword = findViewById(R.id.et_confirmPassword);
        mRegisterBtn = findViewById(R.id.btn_register);
        mLoginText = findViewById(R.id.tv_login);
        mProgressBar = findViewById(R.id.ProgressBar);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        //if user logged in
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                String email = mEmail.getEditableText().toString().trim();
                String password = mPassword.getEditableText().toString().trim();
                String confirmPassword = mPassword.getText().toString().trim();

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

                if(TextUtils.isEmpty(confirmPassword)){
                    mConfirmPassword.setError("Confirm password is required.");
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }

                if(!confirmPassword.equals(password)){
                    mConfirmPassword.setError("Confirm password is not the same as password.");
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }


                // Register the user in firebase
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Get all the values
                            String email = mEmail.getText().toString();

                            //call helper class for data passing
                            UserHelperClass helperClass = new UserHelperClass(email);

                            //store the registered user data into db
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(Register.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(),Login.class));
                                            }
                                            else{
                                                Toast.makeText(Register.this, "Register Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(Register.this, "Register Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
    }
}