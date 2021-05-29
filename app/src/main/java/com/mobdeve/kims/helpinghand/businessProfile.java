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
import android.widget.TextView;

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
import com.mobdeve.kims.helpinghand.BusFeedAdapter;
import com.mobdeve.kims.helpinghand.Post;
import com.mobdeve.kims.helpinghand.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class businessProfile extends AppCompatActivity {

    private String username, bio, isOwner, email, uid, dp;
    private String businessusername, businessuid;
    private TextView profBusUserTv, busEmail_Tv, profBusBioTv;
    private ImageView dp1, busHeaderdp_Iv, logo;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private ArrayList<Post> posts = new ArrayList<Post>();
    private ArrayList<Post> businessPosts = new ArrayList<Post>();

    private RecyclerView busPosts_Rv;
    private BusFeedAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);

        Intent i = getIntent();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        businessusername = i.getStringExtra("businessusername");
        businessuid = i.getStringExtra("businessuid");

        username = i.getStringExtra("username");
        bio = i.getStringExtra("bio");
        isOwner = i.getStringExtra("isowner");
        dp = i.getStringExtra("dp");
        email = i.getStringExtra("email");

        logo = findViewById(R.id.busLogo);
        busHeaderdp_Iv = findViewById(R.id.busHeaderdp_Iv);
        dp1 = findViewById(R.id.profBusImg);

        profBusUserTv = findViewById(R.id.profBusUserTv);
        busEmail_Tv = findViewById(R.id.busEmail_Tv);
        profBusBioTv = findViewById(R.id.profBusBioTv);
        busPosts_Rv = findViewById(R.id.busPosts_Rv);

        profBusUserTv.setText(username);
        busEmail_Tv.setText(user.getEmail());
        profBusBioTv.setText(bio);
        myDpProcess();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        this.busPosts_Rv.setLayoutManager(linearLayoutManager);
        myAdapter = new BusFeedAdapter(businessPosts, businessuid);
        this.busPosts_Rv.setAdapter(this.myAdapter);
        myAdapter.notifyDataSetChanged();

        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myRef = this.firebaseDatabase.getReference("Posts");
        storageRef = FirebaseStorage.getInstance().getReference();

        this.myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Post post = keyNode.getValue(Post.class);
                    posts.add(post);


                    myAdapter.notifyDataSetChanged();
                }

                for(int i = 0; i < posts.size(); i++){
                    if(posts.get(i).getUid().equals(businessuid)){
                        businessPosts.add(posts.get(i));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Debug", "onDataChange: cancelled");
            }
        });



        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(businessProfile.this, Feed.class);
                intent.putExtra("username", username);
                intent.putExtra("bio", bio);
                intent.putExtra("isowner", isOwner);
                intent.putExtra("dp", dp);
                intent.putExtra("uid", uid);
                startActivity(intent);
                finish();
            }
        });

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
                                .into(dp1);
                        Picasso.get()
                                .load(task.getResult())
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(busHeaderdp_Iv);
                    } else {
                        Log.d("Debug", "onComplete: did not get image");
                    }
                }
            });

        }
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

                Intent i = new Intent(businessProfile.this, businessProfile.class);

                i.putExtra("username", username);
                i.putExtra("bio", bio);
                i.putExtra("isowner", isOwner);
                i.putExtra("dp", dp);
                i.putExtra("uid", uid);
                i.putExtra("businessuid", uid);

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

}