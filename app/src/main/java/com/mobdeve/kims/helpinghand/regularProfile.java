package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import org.w3c.dom.Text;

public class regularProfile extends AppCompatActivity {

    private String username, bio, isOwner,dp,email,uid;

    private TextView usernameTv, bioTv,emailTv;
    private ImageView dp1, dp2,logo;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    //for option profile
    String description, imageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_profile);

        Intent i = getIntent();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid= user.getUid();

        username = i.getStringExtra("username");
        bio = i.getStringExtra("bio");
        isOwner = i.getStringExtra("isowner");
        dp = i.getStringExtra("dp");
        email = i.getStringExtra("email");


        usernameTv = findViewById(R.id.profRegUserTv);
        emailTv = findViewById(R.id.email_Tv);
        bioTv = findViewById(R.id.profRegBioTv);
        dp1 = findViewById(R.id.profRegImg);
        dp2 = findViewById(R.id.headerdp_Iv);
        logo = findViewById(R.id.logo);

        usernameTv.setText(username);
        emailTv.setText("email: " + user.getEmail());
        bioTv.setText(bio);

        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(regularProfile.this, Feed.class);
                intent.putExtra("username", username);
                intent.putExtra("bio", bio);
                intent.putExtra("isowner", isOwner);
                intent.putExtra("dp", dp);
                startActivity(intent);
                finish();
            }

        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);
                description = userProfile.getDescription();
                imageName = userProfile.getImage_name();
                usernameTv.setText(userProfile.getusername());
                bioTv.setText(description);
                isOwner = userProfile.getIsOwner().toString();
                emailTv.setText(userProfile.getEmail());
                setUserImg(imageName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //set dp image
    public void myDpProcess(){

        if(dp != null ){
            // With the storageReference, get the image based on its name
            StorageReference imageRef = this.storageRef.child("images/" + dp);

            // Download the image and display via Picasso accordingly
            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Picasso.get()
                                .load(task.getResult())
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(dp1);
                        Picasso.get()
                                .load(task.getResult())
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(dp2);
                    } else {
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

                Intent i = new Intent(regularProfile.this, regularProfile.class);

                i.putExtra("username", username);
                i.putExtra("bio", bio);
                i.putExtra("isowner", isOwner);
                i.putExtra("dp", dp);
                i.putExtra("uid", uid);

                startActivity(i);
                finish();
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    //set user image
    public void setUserImg(String imageName) {
        // With the storageReference, get the image based on its name
        StorageReference imageRef = this.storageRef.child("images/" + imageName);
        // Download the image and display via Picasso accordingly
        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(dp1);
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(dp2);
                } else {
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        username = i.getStringExtra("username");
        bio = i.getStringExtra("bio");
        isOwner = i.getStringExtra("isowner");
        dp = i.getStringExtra("dp");
        email = i.getStringExtra("email");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        username = i.getStringExtra("username");
        bio = i.getStringExtra("bio");
        isOwner = i.getStringExtra("isowner");
        dp = i.getStringExtra("dp");
        email = i.getStringExtra("email");
    }

}