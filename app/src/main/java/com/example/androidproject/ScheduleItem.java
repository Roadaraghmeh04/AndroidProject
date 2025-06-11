package com.example.androidproject;
public class ScheduleItem {
    private String day;
    private String time;
    private String subject;
    private String room;

    public ScheduleItem(String day, String time, String subject, String room) {
        this.day = day;
        this.time = time;
        this.subject = subject;
        this.room = room;
    }

    public ScheduleItem(String subject, String day, String subject1) {
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getSubject() {
        return subject;
    }

    public String getRoom() {
        return room;
    }
}