package com.mobdeve.kims.helpinghand;

public class User {

    public String username, email, description;
    public Boolean isOwner;

    public User(){

    }

    public User(String username, String email, String description, Boolean isOwner){
        this.username = username;
        this.email = email;
        this.description = description;
        this.isOwner = isOwner;
    }


}
