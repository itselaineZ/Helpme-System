package com.example.begin.bean;

import java.io.Serializable;

public class Information implements Serializable {
    private String taskId;
    private String title;
    private String content;

    public String getTaskId(){return taskId;}

    public void setTaskId(String taskId){this.taskId = taskId;}

    public String getTitle(){return title;}

    public void setTitle(String title){this.title = title;}

    public String getContent(){return content;}

    public void setContent(String informStatus){this.content = content;}
}
