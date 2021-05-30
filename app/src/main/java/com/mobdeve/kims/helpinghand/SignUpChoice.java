package com.mobdeve.kims.helpinghand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//choose between regular or business user
public class SignUpChoice extends AppCompatActivity {

    private Button choiceRegBtn;
    private Button choiceBusinessBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_choice);

        choiceRegBtn = findViewById(R.id.choiceRegBtn);
        choiceBusinessBtn = findViewById(R.id.choiceBusinessBtn);

        choiceRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpChoice.this, SignUpRegular.class);
                startActivity(i);
            }
        });

        choiceBusinessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpChoice.this, SignUpBusiness.class);
                startActivity(i);
            }
        });

    }
}