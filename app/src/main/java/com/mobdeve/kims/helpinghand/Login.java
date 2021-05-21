package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class Login extends AppCompatActivity {

    private EditText loginEmailEt, loginPassEt;
    private Button loginLogBtn;
    private TextView loginSignUpTv;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailEt = findViewById(R.id.loginEmailEt);
        loginPassEt = findViewById(R.id.loginPassEt);

        loginLogBtn = findViewById(R.id.loginLogBtn);

        loginSignUpTv = findViewById(R.id.loginSignUpTv);

        progressBar = findViewById(R.id.progressBar);


        loginLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        loginSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignUpChoice.class);
                startActivity(i);
            }
        });

    }

    private void userLogin(){
        String email = loginEmailEt.getText().toString().trim();
        String pass = loginPassEt.getText().toString().trim();

        if(email.isEmpty()){
            loginEmailEt.setError("Email is required!");
            loginEmailEt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmailEt.setError("Please enter a valid email!");
            loginEmailEt.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            loginPassEt.setError("Password is required!");
            loginPassEt.requestFocus();
            return;
        }

        if(pass.length() < 6){
            loginPassEt.setError("Min password length is 6 characters!");
            loginPassEt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Intent i = new Intent(Login.this, Feed.class);
                    startActivity(i);
                }else{
                    Toast.makeText(Login.this, "Failed to login! Please try again", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


}