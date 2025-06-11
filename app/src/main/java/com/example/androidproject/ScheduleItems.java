package com.example.androidproject;

public class ScheduleItems {
    private String subject;
    private String day;
    private String time;

    public ScheduleItems(String subject, String day, String time) {
        this.subject = subject;
        this.day = day;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }
}


