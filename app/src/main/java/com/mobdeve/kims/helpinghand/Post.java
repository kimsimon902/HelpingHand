package com.mobdeve.kims.helpinghand;

import android.net.Uri;

public class Post {

    public String name, description, imageName;


    public Post(){

    }

    public Post(String name, String description, String imageName){

        this.description = description;
        this.name = name;
        this.imageName = imageName;
    }


}
