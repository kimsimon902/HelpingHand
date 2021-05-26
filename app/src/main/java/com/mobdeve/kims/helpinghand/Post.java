package com.mobdeve.kims.helpinghand;

import android.net.Uri;

public class Post {

    public String name, caption, image_name, username;


    public Post(){

    }

    public Post(String name, String description, String imageName, String username){

        this.username = username;
        this.caption = description;
        this.name = name;
        this.image_name = imageName;
    }

    public String getUsername(){
        return username;
    }

    public String getCaption(){
        return caption;
    }

    public String getImageName(){
        return image_name;
    }

}
