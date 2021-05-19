package com.example.begin.bean;

import java.io.Serializable;

public class Course implements Serializable{
    private long id;
    private String coursename;

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getCoursename(){return coursename;}

    public void setCoursename(String coursename){this.coursename = coursename;}

}
