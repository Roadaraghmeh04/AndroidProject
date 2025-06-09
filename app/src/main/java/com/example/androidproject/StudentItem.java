package com.example.androidproject;

public class StudentItem {
    private int id;
    private String name;
    private String email;
    private String subjectName;
    private String grade = "";
    private int subjectId;

    public StudentItem(int id, String name, String email, String subjectName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subjectName = subjectName;
        // ملاحظة: ممكن تحتاج تجيب الـ subject_id من السيرفر مستقبلاً
        this.subjectId = 1; // مؤقتاً ثابت
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSubjectName() { return subjectName; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public int getSubjectId() { return subjectId; }
}
