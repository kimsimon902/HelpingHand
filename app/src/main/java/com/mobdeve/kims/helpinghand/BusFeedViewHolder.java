package com.mobdeve.kims.helpinghand;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

public class BusFeedViewHolder extends RecyclerView.ViewHolder {

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private CardView postCV;
    private TextView tv_username, tv_caption, tv_comments, tv_addComment;
    private ImageView iv_avatar, iv_image, userdp;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private String imageName, mydp;
    private FirebaseAuth mAuth;
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();

    public BusFeedViewHolder(@NonNull View itemView){
        super(itemView);

        this.postCV = itemView.findViewById(R.id.postCV);
        postCV.setVisibility(View.GONE);
        this.tv_username = itemView.findViewById(R.id.tv_username);
        this.tv_caption = itemView.findViewById(R.id.tv_caption);
        this.tv_comments = itemView.findViewById(R.id.tv_comments);
        tv_comments.setVisibility(View.GONE);
        this.tv_addComment = itemView.findViewById(R.id.tv_addComment);
        tv_addComment.setVisibility(View.GONE);
        this.iv_image = itemView.findViewById(R.id.iv_image);
//        this.iv_avatar = itemView.findViewById(R.id.iv_avatar);
//        iv_avatar.setVisibility(View.GONE);
        this.userdp = itemView.findViewById(R.id.userdp_Iv);



    }

    public void setUsername(String username){
        this.tv_username.setText(username);
    }

    public void setCaption(String caption){
        this.tv_caption.setText(caption);
    }

    public void setImage(String imageName){
        StorageReference imageRef = this.storageRef.child(imageName);

        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Log.d("Debug", "onComplete: got image");
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(iv_image);
                } else {
                    Log.d("Debug", "onComplete: Did not get image");
                }
            }
        });
    }

    public void setdps(String uid){
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myRef = this.firebaseDatabase.getReference("Users").child(uid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                dpProcess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myRef.addValueEventListener(postListener);

    }

    public void dpProcess(User user){

        imageName = user.getImage_name();

        if(imageName != null){
            StorageReference imageRef = this.storageRef.child("images/" + imageName);

            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Log.d("Debug", "onComplete: got image");
                        Picasso.get()
                                .load(task.getResult())
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(iv_avatar);
                    }else {
                        Log.d("Debug", "onComplete: Did not get image");
                    }
                }
            });
        }
    }

}



