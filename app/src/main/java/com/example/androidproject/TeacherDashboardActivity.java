package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    private View cardSchedule, cardGrades, cardAssignments, cardViewSubmissions;
    private TextView tvWelcome;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // استقبال البيانات
        String fullName = getIntent().getStringExtra("full_name");
        int teacherId = getIntent().getIntExtra("teacher_id", -1);

        // ربط العناصر
        tvWelcome = findViewById(R.id.tvTeacherWelcome);
        cardSchedule = findViewById(R.id.Schedule);
        cardGrades = findViewById(R.id.Grades);
        cardAssignments = findViewById(R.id.Assignments);
        cardViewSubmissions = findViewById(R.id.ViewSubmissions);
        btnLogout = findViewById(R.id.btnLogout);

        // الترحيب
        tvWelcome.setText("Welcome, " + (fullName != null ? fullName : "Teacher"));

        // تأثير دخول
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        cardSchedule.startAnimation(fadeIn);
        cardGrades.startAnimation(fadeIn);
        cardAssignments.startAnimation(fadeIn);
        cardViewSubmissions.startAnimation(fadeIn);

        // التنقل إلى الصفحات
        cardSchedule.setOnClickListener(v -> animateAndStartActivity(cardSchedule, TeacherScheduleActivity.class, teacherId));
        cardGrades.setOnClickListener(v -> animateAndStartActivity(cardGrades, PublishGradesActivity.class, teacherId));
      // cardAssignments.setOnClickListener(v -> animateAndStartActivity(cardAssignments, SubmitAssignmentActivity.class, teacherId));
       // cardViewSubmissions.setOnClickListener(v -> animateAndStartActivity(cardViewSubmissions, ViewSubmittedAssignmentsActivity.class, teacherId));

        // ✅ زر تسجيل الخروج
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // حذف كل ما قبله من Activities
            startActivity(intent);
        });
    }

    private void animateAndStartActivity(View view, Class<?> destination, int teacherId) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(TeacherDashboardActivity.this, destination);
                intent.putExtra("teacher_id", teacherId);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 150);
        }).start();
    }
}
