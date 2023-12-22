package com.example.attendx;

public class FeaturedHelper {

    int image;
    String title, room_id,admin,sub_name,cls_code;

    public FeaturedHelper(int image, String title, String room_id, String admin, String sub_name, String cls_code) {
        this.image = image;
        this.room_id = room_id;
        this.title = title;
        this.admin = admin;
        this.sub_name = sub_name;
        this.cls_code = cls_code;

    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getRoom_id(){ return room_id;}

    public String getAdmin() {
        return admin;
    }

    public String getSub_name(){ return sub_name;}

    public String getCls_code(){ return cls_code;}


}
