package com.example.begin.bean;

import java.io.Serializable;

public class Filesource implements Serializable{
    private long id;
    private String courseMaterialName;
    private double score;
    private long downloads;

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getCourseMaterialName(){return courseMaterialName;}

    public void setCourseMaterialName(String courseMaterialName){this.courseMaterialName = courseMaterialName;}

    public double getScore(){return score;}

    public void setScore(double score){this.score = score;}

    public long getDownloads(){return downloads;}

    public void setDownloads(long downloads){this.downloads = downloads;}
}
