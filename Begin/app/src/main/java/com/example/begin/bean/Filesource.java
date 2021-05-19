package com.example.begin.bean;

import java.io.Serializable;

public class Filesource implements Serializable{
    private long id;
    private String filename;
    private String path;
    private double score;
    private int evaluators;
    private int downloads;

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getFilename(){return filename;}

    public void setFilename(String filename){this.filename = filename;}

    public String getPath(){return path;}

    public void setPath(String path){this.path = path;}

    public double getScore(){return score;}

    public void setScore(double score){this.score = score;}

    public int getEvaluators(){return evaluators;}

    public void setEvaluators(int evaluators){this.evaluators = evaluators;}

    public int getDownloads(){return downloads;}

    public void setDownloads(int downloads){this.downloads = downloads;}
}
