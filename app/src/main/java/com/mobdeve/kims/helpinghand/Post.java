package com.mobdeve.kims.helpinghand;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

//post class
public class Post implements Serializable {

    public String name, caption, image_name, username, uid, key;



    public Post(){

    }

    public Post(String name, String description, String imageName, String username, String uid, String key){

        this.username = username;
        this.caption = description;
        this.name = name;
        this.image_name = imageName;
        this.uid = uid;
        this.key = key;

    }

    public String getName(){return name;}

    public String getUsername(){
        return username;
    }

    public String getCaption(){
        return caption;
    }

    public String getImage_name(){
        return image_name;
    }

    //user id of person who posted
    public String getUid(){
        return uid;
    }

    public String getKey(){
        return key;
    }



}
