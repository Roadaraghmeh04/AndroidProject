package com.example.androidproject;

public class GradeItem {
    private String subject;
    private String examType;
    private double grade;

    public GradeItem(String subject, String examType, double grade) {
        this.subject = subject;
        this.examType = examType;
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public String getExamType() {
        return examType;
    }

    public double getGrade() {
        return grade;
    }
}
