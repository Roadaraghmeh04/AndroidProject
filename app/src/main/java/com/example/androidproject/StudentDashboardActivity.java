package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudentDashboardActivity extends AppCompatActivity {

    CardView cardSchedule, cardGrades, cardAssignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        cardSchedule = findViewById(R.id.cardSchedule);
        cardGrades = findViewById(R.id.cardGrades);
        cardAssignments = findViewById(R.id.cardAssignments);

        cardSchedule.setOnClickListener(v -> startActivity(new Intent(this, ViewScheduleActivity.class)));
        cardGrades.setOnClickListener(v -> startActivity(new Intent(this, ViewGradesActivity.class)));
        cardAssignments.setOnClickListener(v -> startActivity(new Intent(this, AssignmentsListActivity.class)));
    }
}
