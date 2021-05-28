package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DatabaseMetaData;
import java.util.HashMap;

public class Comments extends AppCompatActivity {


    private EditText addcomment;
    private TextView postcmnt;
    private String uid, postid;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Intent intent = getIntent();

        postid = intent.getStringExtra("key");

        uid = user.getUid();

        addcomment = findViewById(R.id.addcomment_Et);
        postcmnt = findViewById(R.id.postcomment_Tv);


        postcmnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(addcomment.getText().toString().equals("")){
                    Toast.makeText(Comments.this, "You cant send an empty comment", Toast.LENGTH_SHORT).show();
                } else{
                    addComment();
                }

            }

        });


    }

    private void addComment(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);

        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("comment", addcomment.getText().toString());
        hashmap.put("publisher", uid);

        ref.child("comments").push().setValue(hashmap);
        addcomment.setText("");

    }



}