package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private TextView usernameTv;
    private String username, bio, isOwner,uid,dp;
    private ImageView userdp,logo;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference storageReference;

    private ArrayList<Post> posts = new ArrayList();

    private RecyclerView postsRv;
    private FeedAdapter myAdapter;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid= user.getUid();

        Intent i = getIntent();

        username = i.getStringExtra("username");
        bio = i.getStringExtra("bio");
        isOwner = i.getStringExtra("owner");
        dp = i.getStringExtra("dp");
        uid = i.getStringExtra("uid");

        //usernameTv = findViewById(R.id.usernameTv);
        postsRv = findViewById(R.id.rv_posts);
        userdp = findViewById(R.id.userdp_Iv);
        logo = findViewById(R.id.logo);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        this.postsRv.setLayoutManager(linearLayoutManager);
        myAdapter = new FeedAdapter(posts, uid);
        this.postsRv.setAdapter(this.myAdapter);
        myDpProcess();

       // usernameTv.setText(i.getStringExtra("username"));

        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myRef = this.firebaseDatabase.getReference("Posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        this.myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Post post = keyNode.getValue(Post.class);
                    posts.add(post);

                    // Once done loading data, notify the adapter that data has loaded in
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Debug", "onDataChange: canceled");
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Feed.this, Feed.class);
                intent.putExtra("username", username);
                intent.putExtra("bio", bio);
                intent.putExtra("isowner", isOwner);
                intent.putExtra("dp", dp);
                startActivity(intent);
                finish();
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:

                Intent i = new Intent(Feed.this, regularProfile.class);

                i.putExtra("username", username);
                i.putExtra("bio", bio);
                i.putExtra("isowner", isOwner);
                i.putExtra("dp", dp);

                startActivity(i);
                finish();
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this,Login.class);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    public void myDpProcess(){



        if(dp != null ){
            // With the storageReference, get the image based on its name
            StorageReference imageRef = this.storageRef.child("images/" + dp);

            // Download the image and display via Picasso accordingly
            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Log.d("Debug", "onComplete: got image");
                        Picasso.get()
                                .load(task.getResult())
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(userdp);
                    } else {
                        Log.d("Debug", "onComplete: did not get image");
                    }
                }
            });

        }
    }

}