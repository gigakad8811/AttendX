package com.example.attendx;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class CategoriesHelper {

    GradientDrawable Gradient;
    int image;
    String title,sub_name,sub_code,ins_name,admin,room_id;

    public String getRoom_id() {
        return room_id;
    }

    public CategoriesHelper(GradientDrawable Gradient, int image, String title, String sub_name, String sub_code, String ins_name, String admin, String room_id) {
        this.image = image;
        this.title = title;
        this.Gradient = Gradient;
        this.sub_name = sub_name;
        this.sub_code = sub_code;
        this.ins_name = ins_name;
        this.room_id = room_id;
        this.admin = admin;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getGradient(){
        return Gradient;
    }

    public String getSub_name() {
        return sub_name;
    }

    public String getSub_code() {
        return sub_code;
    }

    public String getIns_name() {
        return ins_name;
    }

    public String getAdmin() {
        return admin;
    }
}
