package com.example.attendx;

import android.widget.TextView;

public class JoinReqHelper {


    String image_url, stu_name,phn_no,email,user_id,room_id,key;

    public JoinReqHelper(String image_url, String stu_name, String phn_no, String email,String user_id,String room_id,String key) {
        this.image_url = image_url;
        this.stu_name = stu_name;
        this.phn_no = phn_no;
        this.email = email;
        this.user_id = user_id;
        this.room_id = room_id;
        this.key= key;
    }

    public String getKey() { return key; }

    public String getImage_url() {
        return image_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getStu_name() {
        return stu_name;
    }

    public String getPhn_no() {
        return phn_no;
    }

    public String getEmail() {
        return email;
    }

    public String getRoom_id() { return room_id; }
}
