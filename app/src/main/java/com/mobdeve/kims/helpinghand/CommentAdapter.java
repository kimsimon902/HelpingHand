package com.mobdeve.kims.helpinghand;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private ArrayList<Comment> data;
    private String uid;

    public CommentAdapter(ArrayList<Comment> data, String uid) {
        this.data = data;
        this.uid = uid;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.comments, parent, false);

        // Create an instance of the ViewHolder with the created ViewGroups
        CommentViewHolder myViewHolder = new CommentViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {


        holder.setComment(this.data.get(position).getpostid(), this.data.get(position).getCommentid());
        holder.setAuthor(this.data.get(position).getpostid(), this.data.get(position).getCommentid());



    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


}
