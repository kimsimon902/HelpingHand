package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comments extends AppCompatActivity {


    private EditText addcomment;
    private TextView postcmnt;
    private String uid, postid;
    private Comment comment;
    private ArrayList<Comment> comments = new ArrayList<>();

    private RecyclerView commentsRc;
    //private CommentAdapter myAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
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
        commentsRc = findViewById(R.id.comments_Rc);

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


//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        this.commentsRc.setLayoutManager(linearLayoutManager);
//        myAdapter = new CommentAdapter(comments, uid);
//        this.commentsRc.setAdapter(this.myAdapter);
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        ref = this.firebaseDatabase.getReference("Posts").child(postid).child("comments");
//
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<String> keys = new ArrayList<>();
//                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
//                    keys.add(keyNode.getKey());
//                     comment = keyNode.getValue(Comment.class);
//                    comments.add(comment);
//
//                    // Once done loading data, notify the adapter that data has loaded in
//                    myAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }

    private void addComment(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);

        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("comment", addcomment.getText().toString());
        hashmap.put("publisher", uid);
        hashmap.put("post_id", postid);

        String key = ref.push().getKey();
        hashmap.put("comment_id", key);

        ref.child("comments").child(key).setValue(hashmap);

        comment = new Comment(addcomment.getText().toString(), uid, key, postid);
        comments.add(comment);

        addcomment.setText("");

    }



}