package com.example.begin.constant;

public class NetConstant {
    public static final String baseService = "http://101.132.124.114:8080";

    private static final String userURL = "/user";

    private static final String loginURL            = "/login";
    private static final String registerURL         = "/register";

    private static final String CourseListURL       = "/course/list";

    private static final String CourseAddURL        ="/course/add";

    private static final String FileListURL         ="/course/material";

    private static final String FileAddURL          ="/course/material/add";

    private static final String DownloadURL         = "/course/material/download";

    private static final String ScoreURL            = "/course/material/score";

    private static final String TaskListURL         = "/task/list";

    private static final String ReleaseURL          ="/task/publish";

    private static final String InformListURL       = "/message";

    private static final String RecievedTaskURL     = "/task/list/receive";

    private static final String TaskDetailURL       = "/task/receive";

    private static final String PublishedTaskURL    = "/task/list/publish";

    private static final String FinishByReceiverURL = "/task/finish/receiver";

    private static final String FinishByPublisherURL= "/task/finish/publisher";

    private static final String PublisherGiveUpURL  = "/task/halt/publisher";

    private static final String ReceiverGiveUpURL   = "/task/halt/receiver";

    public static String getLoginURL() {
        return baseService + userURL +loginURL;
    }

    public static String getRegisterURL() {
        return baseService + userURL + registerURL;
    }

    public static String getCourseListURL() {
        return baseService + CourseListURL;
    }

    public static String getCourseAddURL(){return baseService + CourseAddURL;}

    public static String getFileListURL() {
        return baseService + FileListURL;
    }

    public static String getFileAddURL(){return baseService + FileAddURL;}

    public static String getDownloadURL() {
        return baseService + DownloadURL;
    }

    public static String getScoreURL(){return baseService + ScoreURL;}

    public static String getTaskListURL(){return baseService + TaskListURL;}

    public static String getReleaseURL(){return baseService + ReleaseURL;}

    public static String getInformListURL(){return baseService + InformListURL;}

    public static String getRecievedTaskURL(){return baseService + RecievedTaskURL;}

    public static String getTaskDetailURL(){return baseService + TaskDetailURL;}

    public static String getPublishedTaskURL(){return baseService + PublishedTaskURL;}

    public static String getFinishByPublisherURL(){return baseService + FinishByPublisherURL;}

    public static String getFinishByReceiverURL(){return baseService + FinishByReceiverURL;}

    public static String getPublisherGiveUpURL(){return baseService + PublisherGiveUpURL;}

    public static String getReceiverGiveUpURL(){return baseService + ReceiverGiveUpURL;}
}