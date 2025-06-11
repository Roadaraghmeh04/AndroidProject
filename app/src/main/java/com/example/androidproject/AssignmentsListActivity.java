package com.example.androidproject;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<AssignmentItem> assignmentList;
    AssignmentAdapter adapter;
    RequestQueue requestQueue;

    private int studentId;
    private String BASE_URL = "http://10.0.2.2/school_api/get_assignmentss.php?student_id=1&assignment_id=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_list);

        studentId = getIntent().getIntExtra("student_id", 0);

        recyclerView = findViewById(R.id.recyclerAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        loadAssignments();
    }

    private void loadAssignments() {
        String urlWithParam = BASE_URL + "?student_id=" + studentId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlWithParam,
                null,
                response -> {
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
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Volley Error: " + error.toString(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(jsonArrayRequest);
}
}