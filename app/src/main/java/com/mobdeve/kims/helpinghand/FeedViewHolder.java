package com.mobdeve.kims.helpinghand;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FeedViewHolder extends RecyclerView.ViewHolder {

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private TextView usernameTv, captionTv, commentsTv, addcmntTv;
    private ImageView imageIv, postdp, userdp;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private String imageName, mydp;
    private FirebaseAuth mAuth;
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();



    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);

        this.usernameTv = itemView.findViewById(R.id.tv_username);
        this.captionTv = itemView.findViewById(R.id.tv_caption);
        this.commentsTv = itemView.findViewById(R.id.tv_comments);
        commentsTv.setVisibility(View.GONE);
        this.addcmntTv = itemView.findViewById(R.id.tv_addComment);
        this.imageIv = itemView.findViewById(R.id.iv_image);
        this.postdp = itemView.findViewById(R.id.iv_avatar);
        this.userdp = itemView.findViewById(R.id.userdp_Iv);



    }

    public void listen(View.OnClickListener onClickListener){
        this.postdp.setOnClickListener(onClickListener);
    }

    public void setUsername(String username) {
        this.usernameTv.setText(username);
    }

    public void setCaption(String caption) {
        this.captionTv.setText(caption);
    }

    public void setImage(String imageName) {
        // With the storageReference, get the image based on its name
        StorageReference imageRef = this.storageRef.child(imageName);
        // Download the image and display via Picasso accordingly
        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(imageIv);
                } else {
                }
            }
        });

    }

    public void getComments(String postid){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).child("comments");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0) {
                    commentsTv.setText("View all " + snapshot.getChildrenCount() + " Comments");
                    commentsTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void setdps(String uid) {


        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myRef = this.firebaseDatabase.getReference("Users").child(uid);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);

                dpProcess(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(postListener);




    }


    public void dpProcess(User user){

        imageName = user.getImage_name();

        if(imageName != null ){
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
                                .into(postdp);
                    } else {
                    }
                }
            });

        }

    }

    public void setCommentListener(View.OnClickListener ocl) {
        this.addcmntTv.setOnClickListener(ocl);
    }

    public void viewCommentListener(View.OnClickListener ocl) {
        this.commentsTv.setOnClickListener(ocl);
    }


}
