package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private User userProfile;
    private TextView usernameTv, addcmntTv;
    private String username, bio, isOwner,uid,dp, uid_Sp, dp_Sp, username_Sp, bio_Sp, isOwner_Sp;
    private ImageView userdp,logo;
    private Button feedAddBtn;

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
        isOwner = i.getStringExtra("isowner");
        dp = i.getStringExtra("dp");


        postsRv = findViewById(R.id.rv_posts);
        userdp = findViewById(R.id.userdp_Iv);
        logo = findViewById(R.id.logo);
        addcmntTv = findViewById(R.id.tv_addComment);
        feedAddBtn = findViewById(R.id.feedAddBtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        this.postsRv.setLayoutManager(linearLayoutManager);
        myAdapter = new FeedAdapter(posts, uid, isOwner);
        this.postsRv.setAdapter(this.myAdapter);
        myDpProcess();
        myAdapter.notifyDataSetChanged();



        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myRef = this.firebaseDatabase.getReference("Posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        //get post from database and populate local arraylist
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
                    intent.putExtra("uid", uid);

                startActivity(intent);
                finish();


            }

        });

        feedAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Feed.this, AddPost.class);

                i.putExtra("username", username);
                i.putExtra("uid", user.getUid());

                startActivity(i);

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

                Intent i;
                Log.d("before if", "isOwner: " + isOwner);
                Log.d("sp", "isOwner-SP: " + isOwner_Sp);
                Log.d("ddd", "uid: " +  uid);

                if(isOwner == null){
                    if(isOwner_Sp.equals("true")){
                        i = new Intent(Feed.this, businessProfile.class);
                        i.putExtra("businessuid", uid_Sp);
                        i.putExtra("isOwner", isOwner_Sp);
                    }else {
                        i = new Intent(Feed.this, regularProfile.class);
                        i.putExtra("uid", uid_Sp);
                        i.putExtra("isOwner", isOwner_Sp);
                    }
                }else {
                    if(isOwner.equals("true")) {
                        i = new Intent(Feed.this, businessProfile.class);
                        i.putExtra("businessuid", uid);
                        i.putExtra("isOwner", isOwner);

                    }else
                    {
                        i = new Intent(Feed.this, regularProfile.class);
                        i.putExtra("uid", uid);
                    }
                }

                startActivity(i);


                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences settings = this.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

                settings.edit().clear().commit();

                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    //set dp image
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
                        Picasso.get()
                                .load(task.getResult())
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(userdp);
                    } else {
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
        editor.putString("uid", uid);
        editor.putString("isowner", isOwner);

        editor.apply();
        editor.commit();


    }
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sp = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        Intent intent = getIntent();
        isOwner = intent.getStringExtra("isowner");


        username_Sp = sp.getString("username", null);
        bio_Sp = sp.getString("bio", null);
        isOwner_Sp = sp.getString("isowner", null);
        isOwner = isOwner_Sp;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tempUid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(tempUid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userProfile = snapshot.getValue(User.class);
                isOwner = userProfile.getIsOwner().toString();
                dp = userProfile.getImage_name();

                if (isOwner.equals("true")) {
                    feedAddBtn.setVisibility(View.VISIBLE);
                }

                myDpProcess();
                username = userProfile.getusername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myDpProcess();

        myAdapter.notifyDataSetChanged();
    }


}