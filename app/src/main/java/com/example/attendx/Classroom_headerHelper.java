package com.example.attendx;

public class Classroom_headerHelper {

    //Subcode, insname, R optional, so we pass null if not given by user & total class is initialize with 0

    public String TeacherName, ClassName, SubName, SubCode, InstituteName, TotalClassTaken,last_class_taken_on;
    public Classroom_headerHelper(){}

    public Classroom_headerHelper(String TeacherName, String ClassName, String SubName, String SubCode, String InstituteName, String TotalClassTaken) {
        this.TeacherName = TeacherName;
        this.ClassName = ClassName;
        this.SubName = SubName;
        this.SubCode = SubCode;
        this.InstituteName = InstituteName;
        this.TotalClassTaken = TotalClassTaken;
        this.last_class_taken_on = "0";
    }
}
