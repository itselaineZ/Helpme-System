package com.example.begin.bean;

import java.io.Serializable;

public class Information implements Serializable {
    private long taskId;
    private String title;
    private String content;

    public long getTaskId(){return taskId;}

    public void setTaskId(long taskId){this.taskId = taskId;}

    public String getTitle(){return title;}

    public void setTitle(String title){this.title = title;}

    public String getContent(){return content;}

    public void setContent(String informStatus){this.content = content;}
}
