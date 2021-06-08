package com.example.begin.constant;

public class NetConstant {
    public static final String baseService = "http://106.14.124.235:3000/mock/9";

    private static final String userURL = "/user";

    private static final String loginURL            = "/login";
    private static final String registerURL         = "/register";

    private static final String CourseListURL       = "/course/list";

    private static final String FileListURL         = "/course/material";

    private static final String DownloadURL         = "/download";

    private static final String TaskListURL         = "/task/list";

    private static final String InformListURL       = "/message";

    private static final String RecievedTaskURL     = "/task/myReceive";

    private static final String TaskDetailURL       = "/task/detail";

    private static final String PublishedTaskURL        = "/task/myPublish";

    public static String getLoginURL() {
        return baseService + userURL +loginURL;
    }

    public static String getRegisterURL() {
        return baseService + userURL + registerURL;
    }

    public static String getCourseListURL() {
        return baseService + CourseListURL;
    }

    public static String getFileListURL() {
        return baseService + FileListURL;
    }

    public static String getDownloadURL() {
        return baseService + DownloadURL;
    }

    public static String getTaskListURL(){return baseService + TaskListURL;}

    public static String getInformListURL(){return baseService + InformListURL;}

    public static String getRecievedTaskURL(){return baseService + RecievedTaskURL;}

    public static String getTaskDetailURL(){return baseService + TaskDetailURL;}

    public static String getPublishedTaskURL(){return baseService + PublishedTaskURL;}
}