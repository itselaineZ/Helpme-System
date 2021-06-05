package com.example.begin.bean;

import java.io.Serializable;

public class Tasksource implements Serializable {
    private String id;
    private String title;
    private String publisherId;
    private String recieverId;
    private String description;

    public String getId(){return id;}

    public void setId(String id){this.id = id;}

    public String getTitle(){return title;}

    public void setTitle(String title){this.title = title;}

    public String getPublisherId(){return publisherId;}

    public void setPublisherId(String publisherId){this.publisherId = publisherId;}

    public String getRecieverId(){return recieverId;}

    public void setRecieverId(String recieverId){this.recieverId = recieverId;}

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

}
