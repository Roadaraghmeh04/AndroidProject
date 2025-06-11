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

public class ViewGradesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<GradeItem> gradeList;
    GradeAdapter adapter;
    RequestQueue requestQueue;


    private final String URL = "http://10.0.2.2/school_api/get_grades.php?student_id=?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grades);

        recyclerView = findViewById(R.id.recyclerGrades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gradeList = new ArrayList<>();
        adapter = new GradeAdapter(gradeList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        int studentId = 1;

        loadGrades(studentId);
    }

    private void loadGrades(int studentId) {
        String fullUrl = URL + "?student_id=" + studentId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                fullUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        gradeList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject grade = response.getJSONObject(i);
                                String subject = grade.getString("subject_name");
                                String examType = grade.getString("exam_type");
                                int gradeValue = grade.getInt("grade");

                                gradeList.add(new GradeItem(subject, examType, gradeValue));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(ViewGradesActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewGradesActivity.this, "Volley Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }

                });

        requestQueue.add(jsonArrayRequest);
    }
}
