package com.mobdeve.kims.helpinghand;

import android.net.Uri;

public class Post {

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

    public String getUsername(){
        return username;
    }

    public String getCaption(){
        return caption;
    }

    public String getImage_name(){
        return image_name;
    }

    public String getUid(){
        return uid;
    }
}
