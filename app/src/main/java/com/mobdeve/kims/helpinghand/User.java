package com.mobdeve.kims.helpinghand;

import android.net.Uri;

//user model
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

    public String getEmail(){return email;}

    public String getImage_name()
    {
        return image_name;
    }

    public String getusername(){
        return username;
    }

    public String getDescription(){
        return description;
    }

    public Boolean getIsOwner(){
        return isOwner;
    }

}