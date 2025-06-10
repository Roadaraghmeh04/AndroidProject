package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    private View cardSchedule, cardGrades, cardAssignments, cardViewSubmissions, cardProfile, cardLogout;
    private TextView tvWelcome;

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
        cardProfile = findViewById(R.id.Profile);
        cardLogout = findViewById(R.id.Logout);
        ImageView notificationBell = findViewById(R.id.notificationBell); // ⬅️ هنا الإضافة المهمة

        // رسالة الترحيب
        tvWelcome.setText("Welcome, " + (fullName != null ? fullName : "Teacher"));

        // حركة دخول
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        cardSchedule.startAnimation(fadeIn);
        cardGrades.startAnimation(fadeIn);
        cardAssignments.startAnimation(fadeIn);
        cardViewSubmissions.startAnimation(fadeIn);
        cardProfile.startAnimation(fadeIn);
        cardLogout.startAnimation(fadeIn);

        // فتح كل واجهة حسب الزر
        cardSchedule.setOnClickListener(v -> animateAndStartActivity(cardSchedule, TeacherScheduleActivity.class, teacherId));
        cardGrades.setOnClickListener(v -> animateAndStartActivity(cardGrades, PublishGradesActivity.class, teacherId));
        cardAssignments.setOnClickListener(v -> animateAndStartActivity(cardAssignments, SendAssignmentActivity.class, teacherId));
        cardViewSubmissions.setOnClickListener(v -> animateAndStartActivity(cardViewSubmissions, ViewSubmittedAssignmentsActivity.class, teacherId));
        cardProfile.setOnClickListener(v -> animateAndStartActivity(cardProfile, TeacherProfileActivity.class, teacherId));

        // Logout (زر دائري)
        cardLogout.setOnClickListener(v -> {
            cardLogout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                cardLogout.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }, 150);
            }).start();
        });

        // زر الجرس لفتح رسائل
        notificationBell.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, MessagesActivity.class);
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
