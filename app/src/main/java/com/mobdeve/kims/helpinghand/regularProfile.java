package com.mobdeve.kims.helpinghand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class regularProfile extends AppCompatActivity {

    private String username, bio, isOwner;
    private TextView usernameTv, bioTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_profile);

        Intent i = getIntent();

        username = i.getStringExtra("username");
        bio = i.getStringExtra("bio");
        isOwner = i.getStringExtra("isowner");

        usernameTv = findViewById(R.id.profRegUserTv);
        bioTv = findViewById(R.id.profRegBioTv);

        usernameTv.setText(username);
        bioTv.setText(bio);


    }
}