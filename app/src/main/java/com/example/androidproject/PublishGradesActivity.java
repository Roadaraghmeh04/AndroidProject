package com.example.androidproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublishGradesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PublishGradesAdapter adapter;
    private ArrayList<StudentItem> studentList;
    private Button btnSubmitGrades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_grades);

        recyclerView = findViewById(R.id.recyclerViewGrades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSubmitGrades = findViewById(R.id.btnSubmitGrades);

        studentList = new ArrayList<>();
        adapter = new PublishGradesAdapter(studentList);
        recyclerView.setAdapter(adapter);

        int teacherId = getIntent().getIntExtra("teacher_id", -1);
        if (teacherId == -1) {
            Toast.makeText(this, "Teacher ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchStudents(teacherId);

        btnSubmitGrades.setOnClickListener(v -> sendGradesToServer());
    }

    private void fetchStudents(int teacherId) {
        String url = "http://10.0.2.2/school_api/get_students.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        studentList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("student_id");
                            String name = obj.getString("full_name");
                            String email = obj.getString("email");
                            String subjectName = obj.getString("subject_name");

                            studentList.add(new StudentItem(id, name, email, subjectName));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing students", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show();
                    Log.e("STUDENT_FETCH_ERROR", error.toString());
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void sendGradesToServer() {
        String url = "http://10.0.2.2/school_api/submit_grades.php";

        for (StudentItem student : studentList) {
            String grade = student.getGrade();
            if (grade == null || grade.isEmpty()) continue;

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> Log.d("GRADE_RESPONSE", response),
                    error -> Log.e("GRADE_SEND_ERROR", error.toString())
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("student_id", String.valueOf(student.getId()));
                    params.put("score", grade);
                    params.put("subject_id", String.valueOf(student.getSubjectId()));
                    params.put("exam_name", "Final");
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }

        Toast.makeText(this, "Grades submitted", Toast.LENGTH_SHORT).show();
    }
}
