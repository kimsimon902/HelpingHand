package com.mobdeve.kims.helpinghand;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private ArrayList<Post> data;
    private String uid;

    public FeedAdapter(ArrayList<Post> data, String uid) {
        this.data = data;
        this.uid = uid;
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


        holder.listen(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), businessProfile.class);
                i.putExtra("businessusername", data.get(position).getUsername());
                i.putExtra("businessuid", data.get(position).getUid());
                i.putExtra("username", data.get(position).getUsername());

                v.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
