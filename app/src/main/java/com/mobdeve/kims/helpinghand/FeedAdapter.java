package com.mobdeve.kims.helpinghand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private ArrayList<Post> data;
    private String uid, isOwner;
    private Context context;

    public FeedAdapter(ArrayList<Post> data, String uid, String isOwner) {
        this.data = data;
        this.uid = uid;
        this.isOwner = isOwner;

    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.post, parent, false);

        // Create an instance of the ViewHolder with the created ViewGroup
        FeedViewHolder myViewHolder = new FeedViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {


        holder.setUsername(this.data.get(position).getUsername());
        holder.setCaption(this.data.get(position).getCaption());
        holder.setImage("images/" + this.data.get(position).getImage_name());
        holder.setdps(this.data.get(position).getUid());
        holder.getComments(this.data.get(position).getKey());

        holder.setCommentListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Comments.class);
                intent.putExtra("key", data.get(position).getKey());
                view.getContext().startActivity(intent);
            }
        });

        holder.viewCommentListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Comments.class);
                intent.putExtra("key", data.get(position).getKey());
                view.getContext().startActivity(intent);
            }
        });

        holder.listen(new View.OnClickListener(){
            @Override
            public void onClick(View v) {



                Handler h = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        Intent i = new Intent(v.getContext(), businessProfile.class);

                        i.putExtra("businessusernmae", data.get(position).getUsername());
                        i.putExtra("businessuid", data.get(position).getUid());
                        i.putExtra("username", data.get(position).getUsername());
                        i.putExtra("isowner", isOwner);

                        v.getContext().startActivity(i);


                    }
                };
                h.sendEmptyMessageDelayed(0, 2000);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


}
