package com.example.begin.bean;

import java.io.Serializable;

public class Tasksource implements Serializable {
    private long id;
    private String title;
    private String publisherName;
    private String receiverName;
    private String description;

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getTitle(){return title;}

    public void setTitle(String title){this.title = title;}

    public String getPublisherName(){return publisherName;}

    public void setPublisherName(String publisherName){this.publisherName = publisherName;}

    public String getReceiverName(){return receiverName;}

    public void setReceiverName(String receiverName){this.receiverName = receiverName;}

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

}
