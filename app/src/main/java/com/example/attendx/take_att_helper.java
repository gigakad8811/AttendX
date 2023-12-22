package com.example.attendx;

public class take_att_helper {
    public String uid,name,phn,email,img;

    public take_att_helper(String uid, String name, String phn, String email, String img) {
        this.uid = uid;
        this.name = name;
        this.phn = phn;
        this.email = email;
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhn() {
        return phn;
    }

    public String getEmail() {
        return email;
    }

    public String getImg() {
        return img;
    }
}
