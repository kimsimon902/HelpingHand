package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private TextView usernameTv, addcmntTv;
    private String username, bio, isOwner,uid,dp, dp_Sp, username_Sp, bio_Sp, isOwner_Sp;
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
        addcmntTv = findViewById(R.id.tv_addComment);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        this.postsRv.setLayoutManager(linearLayoutManager);
        myAdapter = new FeedAdapter(posts, uid);
        this.postsRv.setAdapter(this.myAdapter);
        myDpProcess();
        myAdapter.notifyDataSetChanged();

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

                Intent intent = new Intent(Feed.this, regularProfile.class);

                if(username == null){
                    intent.putExtra("username", username_Sp);
                    intent.putExtra("bio", bio_Sp);
                    intent.putExtra("isowner", isOwner_Sp);
                    intent.putExtra("dp", dp_Sp);
                }else{
                    intent.putExtra("username", username);
                    intent.putExtra("bio", bio);
                    intent.putExtra("isowner", isOwner);
                    intent.putExtra("dp", dp);
                }


                startActivity(intent);

                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this,Login.class);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    public void myDpProcess(){
        StorageReference imageRef;

        if(dp != null || dp_Sp != null){
            // With the storageReference, get the image based on its name

            if(dp_Sp !=null)
                 imageRef = this.storageRef.child("images/" + dp_Sp);
            else
                 imageRef = this.storageRef.child("images/" + dp);


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

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("dp", dp);
        editor.putString("dp2", dp_Sp);
        editor.putString("username", username);
        editor.putString("bio", bio);
        System.out.println(bio);
        editor.putString("isowner", isOwner);

        editor.apply();
        editor.commit();


    }
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sp = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        username_Sp = sp.getString("username",null);
        bio_Sp = sp.getString("bio",null);
        isOwner_Sp = sp.getString("isowner",null);

        dp_Sp = sp.getString("dp",null);

        if(dp_Sp == null)
            dp_Sp = sp.getString("dp2",null);

        System.out.println(bio_Sp + "onstart");




        myDpProcess();

    }




}