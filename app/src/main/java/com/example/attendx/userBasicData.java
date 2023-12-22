package com.example.attendx;
//this file is the class for uploading basic user data to database that we get from gmail

public class userBasicData {
    public String name, email, user_id, phone, photoUrl,fcm_token;
    public userBasicData(String name, String email, String user_id, String phone, String photoUrl) {
        this.name= name;
        this.email = email;
        this.user_id = user_id;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.fcm_token = "0";
    }
}
