package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeacherScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSchedule;
    private ScheduleAdapter adapter;
    private ArrayList<ScheduleItem> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_schedule);

        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));

        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(scheduleList);
        recyclerViewSchedule.setAdapter(adapter);

        int teacherId = getIntent().getIntExtra("teacher_id", -1);
        if (teacherId == -1) {
            Toast.makeText(this, "Teacher ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = "http://10.0.2.2/school_api/get_teacher_schedule.php?teacher_id=" + teacherId;
        loadScheduleFromDatabase(url);

        // ✅ زر الرجوع إلى لوحة المعلم، مع تمرير teacher_id مرة ثانية
        ImageButton backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherScheduleActivity.this, TeacherDashboardActivity.class);
            intent.putExtra("teacher_id", teacherId);  // ✅ تمرير رقم المعلم
            startActivity(intent);
            finish();
        });
    }

    private void loadScheduleFromDatabase(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("RESPONSE_JSON", response.toString());
                        scheduleList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            String day = obj.getString("day");
                            String time = obj.getString("time");
                            String subject = obj.getString("subject");
                            String room = obj.getString("room");

                            scheduleList.add(new ScheduleItem(day, time, subject, room));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing schedule", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load schedule", Toast.LENGTH_SHORT).show();
                    Log.e("SCHEDULE_ERROR", error.toString());
                });

        queue.add(jsonArrayRequest);
    }
}
