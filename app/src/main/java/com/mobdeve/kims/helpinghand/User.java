package com.mobdeve.kims.helpinghand;

import android.net.Uri;

public class User {

    public String username, email, description, image_name;
    public Boolean isOwner;


    public User(){

    }

    public User(String username, String email, String description, Boolean isOwner, String imageName){
        this.username = username;
        this.email = email;
        this.description = description;
        this.isOwner = isOwner;
        this.image_name = imageName;
    }


}
