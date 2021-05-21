package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class SignUpBusiness extends AppCompatActivity {


    private EditText signupBusNameEt, signupBusEmailEt, signupBusPassEt, signupBusConfirmEt, signupBusDescEt;
    private Button signupBusConfirmBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_business);

        mAuth = FirebaseAuth.getInstance();

        signupBusNameEt = findViewById(R.id.signupBusNameEt);
        signupBusEmailEt = findViewById(R.id.signupBusEmailEt);
        signupBusPassEt = findViewById(R.id.signupBusPassEt);
        signupBusConfirmEt = findViewById(R.id.signupBusConfirmEt);
        signupBusDescEt = findViewById(R.id.signupBusDescEt);

        signupBusConfirmBtn = findViewById(R.id.signupBusConfirmBtn);

        progressBar = findViewById(R.id.progressBar);

        signupBusConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    private void registerUser(){
        String name = signupBusNameEt.getText().toString();
        String email = signupBusEmailEt.getText().toString().trim();
        String pass = signupBusPassEt.getText().toString().trim();
        String confirm = signupBusConfirmEt.getText().toString().trim();
        String desc = signupBusDescEt.getText().toString();

        if(name.isEmpty()){
            signupBusNameEt.setError("Business Name is required!");
            signupBusNameEt.requestFocus();
            return;
        }

        if(email.isEmpty()){
            signupBusEmailEt.setError("Email is required!");
            signupBusEmailEt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupBusEmailEt.setError("Invalid email format!");
            signupBusEmailEt.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            signupBusPassEt.setError("Password is required!");
            signupBusPassEt.requestFocus();
            return;
        }

        if(pass.length() < 6){
            signupBusPassEt.setError("Minimum password length should be 6 characters!");
            signupBusPassEt.requestFocus();
            return;
        }

        if(confirm.isEmpty()){
            signupBusConfirmEt.setError("Please confirm your password");
            signupBusConfirmEt.requestFocus();
            return;
        }

        if(!pass.equals(confirm)){
            signupBusPassEt.setError("Passwords do not match!");
            signupBusConfirmEt.setError("Passwords do not match!");
            signupBusConfirmEt.requestFocus();
            signupBusPassEt.requestFocus();
            return;
        }

        if(desc.isEmpty()){
            signupBusDescEt.setError("Business Description is required!");
            signupBusDescEt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email, desc, true);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUpBusiness.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        //redirect to login next
                                    }else{
                                        Toast.makeText(SignUpBusiness.this, "Failed to register. Try again.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

    }

}