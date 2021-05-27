package com.mobdeve.kims.helpinghand;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private ArrayList<Post> data;

    public FeedAdapter(ArrayList<Post> data) {
        this.data = data;
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
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
