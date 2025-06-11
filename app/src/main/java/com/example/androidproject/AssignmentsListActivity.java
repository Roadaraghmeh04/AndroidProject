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

public class AssignmentsListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<AssignmentItem> assignmentList;
    AssignmentAdapter adapter;
    RequestQueue requestQueue;

    private final String URL = "http://10.0.2.2/school_api/get_assignments.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_list);

        recyclerView = findViewById(R.id.recyclerAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        loadAssignments();
    }

    private void loadAssignments() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        assignmentList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject assignment = response.getJSONObject(i);
                                String title = assignment.getString("title");
                                String subject = assignment.getString("subject_name");
                                String dueDate = assignment.getString("due_date");

                                assignmentList.add(new AssignmentItem(title, subject, dueDate));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AssignmentsListActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AssignmentsListActivity.this, "Volley Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }

                });

        requestQueue.add(jsonArrayRequest);
    }
}
