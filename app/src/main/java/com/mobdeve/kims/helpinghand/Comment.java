package com.mobdeve.kims.helpinghand;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

//Comment class
public class Comment implements Serializable {

    public String publisher, comment,comment_id,post_id;



    public Comment(){

    }

    public Comment(String author, String comment, String commentid, String postid){

        this.publisher = author;
        this.comment = comment;
        this.comment_id = commentid;
        this.post_id = postid;

    }

    public void setPublisher(String author){
        this.publisher= author;

    }

    public void setComment(String comment){
        this.comment= comment;
    }

    public String getPublisher(){
        return publisher;
    }

    public String getComment(){
        return comment;
    }

    public String getCommentid(){
        return comment_id;
    }

    public String getpostid(){
        return post_id;
    }

}
