package com.example.begin.bean;

import java.io.Serializable;

public class Information implements Serializable {
    private String informId;
    private String informTitle;
    private String informStatus;

    public String getInformId(){return informId;}

    public void setInformId(String informId){this.informId = informId;}

    public String getInformTitle(){return informTitle;}

    public void setInformTitle(String informTitle){this.informTitle = informTitle;}

    public String getInformStatus(){return informStatus;}

    public void setInformStatus(String informStatus){this.informStatus = informStatus;}
}
