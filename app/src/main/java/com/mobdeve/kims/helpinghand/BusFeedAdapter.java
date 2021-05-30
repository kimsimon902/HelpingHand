package com.mobdeve.kims.helpinghand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusFeedAdapter extends RecyclerView.Adapter<BusFeedViewHolder> {

    private ArrayList<Post> data;
    private ArrayList<Post> businessPosts = new ArrayList<Post>();
    private String uid;


    public BusFeedAdapter(ArrayList<Post> data, String uid){
        this.data = data;
        this.uid = uid;

    }

    @NonNull
    @Override
    public BusFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.post, parent, false);

        BusFeedViewHolder myViewHolder = new BusFeedViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BusFeedViewHolder holder, int position) {
        holder.setUsername(this.data.get(position).getName());
        holder.setCaption(this.data.get(position).getCaption());
        holder.setImage("images/" + this.data.get(position).getImage_name());




    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
