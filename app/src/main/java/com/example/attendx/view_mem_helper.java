package com.example.attendx;

public class view_mem_helper {
    public String mem_name,mem_phone,mem_email,mem_image,mem_roomid;

    public view_mem_helper(String mem_name, String mem_phone, String mem_email, String mem_image, String mem_roomid) {
        this.mem_name = mem_name;
        this.mem_phone = mem_phone;
        this.mem_email = mem_email;
        this.mem_image = mem_image;
        this.mem_roomid = mem_roomid;
    }

    public String getMem_name() {
        return mem_name;
    }

    public String getMem_phone() {
        return mem_phone;
    }

    public String getMem_email() {
        return mem_email;
    }

    public String getMem_image() {
        return mem_image;
    }

    public String getMem_roomid() {
        return mem_roomid;
    }
}
