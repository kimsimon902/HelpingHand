package com.mobdeve.kims.helpinghand;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Comment implements Serializable {

    public String author, comment,commentid,postid;



    public Comment(){

    }

    public Comment(String author, String comment, String commentid, String postid){

        this.author = author;
        this.comment = comment;
        this.commentid = commentid;
        this.postid = postid;

    }

    public void setAuthor(String author){
        this.author= author;

    }

    public void setComment(String comment){
        this.comment= comment;
    }

    public String getAuthor(){
        return author;
    }

    public String getComment(){
        return comment;
    }

    public String getCommentid(){
        return commentid;
    }

    public String getpostid(){
        return postid;
    }

}
