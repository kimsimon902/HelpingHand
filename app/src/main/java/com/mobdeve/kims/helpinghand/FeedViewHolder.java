package com.mobdeve.kims.helpinghand;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

public class FeedViewHolder extends RecyclerView.ViewHolder {

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private TextView usernameTv, captionTv, commentsTv, addcmntTv;
    private ImageView imageIv, dp;


    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);

        this.usernameTv = itemView.findViewById(R.id.tv_username);
        this.captionTv = itemView.findViewById(R.id.tv_caption);
        this.commentsTv = itemView.findViewById(R.id.tv_comments);
        this.addcmntTv = itemView.findViewById(R.id.tv_addComment);
        this.imageIv = itemView.findViewById(R.id.iv_image);
        this.dp = itemView.findViewById(R.id.iv_avatar);

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
                    Log.d("Debug", "onComplete: got image");
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(imageIv);
                } else {
                    Log.d("Debug", "onComplete: did not get image");
                }
            }
        });
    }


}
