package com.mobdeve.kims.helpinghand;

import android.content.Intent;
import android.net.Uri;
import android.renderscript.Sampler;
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

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private TextView authorTv, commentTv;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private String imageName, mydp, publisherId;
    private FirebaseAuth mAuth;
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        this.authorTv = itemView.findViewById(R.id.author_Tv);
        this.commentTv = itemView.findViewById(R.id.comment_Tv);


    }


    public void setComment(String postid, String commentid) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).child("comments").child(commentid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comment comment = snapshot.getValue(Comment.class);
                commentTv.setText(comment.getComment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void setAuthor(String postid, String commentid) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).child("comments").child(commentid);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comment comment = snapshot.getValue(Comment.class);
                publisherId = comment.getPublisher();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        authorTv.setText(user.getusername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }


}
