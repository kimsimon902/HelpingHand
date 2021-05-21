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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpRegular extends AppCompatActivity {

    private EditText signupRegNameEt, signupRegEmailEt, signupRegPassEt, signupRegConfirmEt, signupRegDescEt;
    private Button signupRegConfirmBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_regular);

        mAuth = FirebaseAuth.getInstance();

        signupRegNameEt = findViewById(R.id.signupRegNameEt);
        signupRegEmailEt = findViewById(R.id.signupRegEmailEt);
        signupRegPassEt = findViewById(R.id.signupRegPassEt);
        signupRegConfirmEt = findViewById(R.id.signupRegConfirmEt);
        signupRegDescEt = findViewById(R.id.signupRegDescEt);

        signupRegConfirmBtn = findViewById(R.id.signupRegConfirmBtn);

        progressBar = findViewById(R.id.progressBar);


        signupRegConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }




    private void registerUser(){
        String name = signupRegNameEt.getText().toString();
        String email = signupRegEmailEt.getText().toString().trim();
        String pass = signupRegPassEt.getText().toString().trim();
        String confirm = signupRegConfirmEt.getText().toString().trim();
        String desc = signupRegDescEt.getText().toString();

        if(name.isEmpty()){
            signupRegNameEt.setError("Name is required!");
            signupRegNameEt.requestFocus();
            return;
        }

        if(email.isEmpty()){
            signupRegEmailEt.setError("Email is required!");
            signupRegEmailEt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupRegEmailEt.setError("Invalid email format!");
            signupRegEmailEt.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            signupRegPassEt.setError("Password is required!");
            signupRegPassEt.requestFocus();
            return;
        }

        if(pass.length() < 6){
            signupRegPassEt.setError("Minimum password length should be 6 characters!");
            signupRegPassEt.requestFocus();
            return;
        }

        if(confirm.isEmpty()){
            signupRegConfirmEt.setError("Please confirm your password");
            signupRegConfirmEt.requestFocus();
            return;
        }

        if(!pass.equals(confirm)){
            signupRegPassEt.setError("Passwords do not match!");
            signupRegConfirmEt.setError("Passwords do not match!");
            signupRegConfirmEt.requestFocus();
            signupRegPassEt.requestFocus();
            return;
        }

        if(desc.isEmpty()){
            signupRegDescEt.setError("Description is required!");
            signupRegDescEt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email, desc, false);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpRegular.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(SignUpRegular.this, Login.class);
                                        startActivity(i);

                                        //redirect to login next
                                    }else{
                                        Toast.makeText(SignUpRegular.this, "Failed to register. Try again.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

    }

}
