package com.example.androidproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeacherScheduleActivity extends AppCompatActivity {

    RecyclerView recyclerViewSchedule;
    ScheduleAdapter scheduleAdapter;
    ArrayList<ScheduleItem> scheduleList = new ArrayList<>();
    int teacherId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_schedule);

        teacherId = getIntent().getIntExtra("teacher_id", -1);
        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadSchedule();
    }

    private void loadSchedule() {
        if (teacherId == -1) {
            Toast.makeText(this, "Teacher ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/school_api/get_schedule_by_teacher.php?teacher_id=" + teacherId;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                scheduleList.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    scheduleList.add(new ScheduleItem(
                            obj.getString("day_of_week"),
                            obj.getString("start_time"),
                            obj.getString("end_time"),
                            obj.optString("subject_name", "-")
                    ));
                }
                scheduleAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                Log.e("ScheduleParse", e.getMessage());
            }
        }, error -> {
            Toast.makeText(this, "Error loading schedule", Toast.LENGTH_SHORT).show();
            Log.e("ScheduleLoadError", error.getMessage());
        });

        queue.add(request);
    }
}
