package com.example.androidproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewScheduleActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ScheduleItem> scheduleList;
    ScheduleAdapter adapter;
    RequestQueue requestQueue;

    private final String URL = "http://10.0.2.2/school_sys/get_schedule.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        recyclerView = findViewById(R.id.recyclerSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter((ArrayList<ScheduleItem>) scheduleList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        int studentId = 1;

        loadSchedule(studentId);
    }

    private void loadSchedule(int studentId) {
        String fullUrl = URL + "?student_id=" + studentId;

        System.out.println("Request URL: " + fullUrl);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, fullUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        scheduleList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String subject = obj.getString("subject_name");
                                String day = obj.getString("day_of_week");
                                String time = obj.getString("start_time") + " - " + obj.getString("end_time");
                                String room = obj.getString("room");

                                scheduleList.add(new ScheduleItem(subject, day, time + " | " + room));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(ViewScheduleActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewScheduleActivity.this, "Volley Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }
}