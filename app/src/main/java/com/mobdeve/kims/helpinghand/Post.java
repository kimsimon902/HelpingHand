package com.mobdeve.kims.helpinghand;

import android.net.Uri;

public class Post {

    public String name, description, imageName, username;


    public Post(){

    }

    public Post(String name, String description, String imageName, String username){

        this.username = username;
        this.description = description;
        this.name = name;
        this.imageName = imageName;
    }

    public String getUsername(){
        return username;
    }

    public String getCaption(){
        return description;
    }

    public String getImageName(){
        return imageName;
    }

}
