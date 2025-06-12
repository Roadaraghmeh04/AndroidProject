package com.example.androidproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.ArrayList;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText etSubjectName, etDescription;
    private Spinner spinnerTeachers;
    private Button btnAddSubject;

    private static final String URL_ADD_SUBJECT = "http://10.0.2.2/school_api/add_subject.php";
    private static final String URL_GET_TEACHERS = "http://10.0.2.2/school_api/get_teacher.php";
    private static final String PREF_NAME = "subject_pref";

    private ArrayList<Teacher> teacherList = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject);

        etSubjectName = findViewById(R.id.etSubjectName);
        etDescription = findViewById(R.id.etDescription);
        spinnerTeachers = findViewById(R.id.spinnerTeachers);
        btnAddSubject = findViewById(R.id.btnAddSubject);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        fetchTeachers();

        btnAddSubject.setOnClickListener(v -> {
            String name = etSubjectName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher selectedTeacher = (Teacher) spinnerTeachers.getSelectedItem();
            if (selectedTeacher == null) {
                Toast.makeText(this, "Please select a teacher", Toast.LENGTH_SHORT).show();
                return;
            }

            int teacherId = selectedTeacher.getId();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("subject_name", name);
            editor.putString("description", description);
            editor.putInt("teacher_id", teacherId);
            editor.apply();

            sendSubjectFromPrefs();
        });

        restoreSavedData();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddSubjectActivity.this, RegisterDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private static class Teacher {
        private int id;
        private String name;

        public Teacher(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return name;
        }
    }

    private void fetchTeachers() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                URL_GET_TEACHERS,
                response -> {
                    teacherList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("teacher_id");
                            String name = obj.getString("full_name");
                            teacherList.add(new Teacher(id, name));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<Teacher> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            teacherList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTeachers.setAdapter(adapter);

                    int savedTeacherId = prefs.getInt("teacher_id", -1);
                    if (savedTeacherId != -1) {
                        for (int i = 0; i < teacherList.size(); i++) {
                            if (teacherList.get(i).getId() == savedTeacherId) {
                                spinnerTeachers.setSelection(i);
                                break;
                            }
                        }
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load teachers", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        queue.add(request);
    }

    private void sendSubjectFromPrefs() {
        String name = prefs.getString("subject_name", "");
        String description = prefs.getString("description", "");
        int teacherId = prefs.getInt("teacher_id", -1);

        if (name.isEmpty() || description.isEmpty() || teacherId == -1) {
            Toast.makeText(this, "No valid data to send", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("subject_name", name);
            postData.put("description", description);
            postData.put("teacher_id", teacherId);
        } catch (JSONException e) {
            Toast.makeText(this, "Error in JSON data", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_ADD_SUBJECT,
                postData,
                response -> {
                    Toast.makeText(this, "Subject added successfully", Toast.LENGTH_SHORT).show();


                    etSubjectName.setText("");
                    etDescription.setText("");
                    spinnerTeachers.setSelection(0);
                },
                error -> {
                    String errorMsg = "Failed to add subject";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg += ": " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        queue.add(request);
    }

    private void restoreSavedData() {
        String savedName = prefs.getString("subject_name", "");
        String savedDescription = prefs.getString("description", "");

        if (!savedName.isEmpty()) {
            etSubjectName.setText(savedName);
        }

        if (!savedDescription.isEmpty()) {
            etDescription.setText(savedDescription);
        }
    }
}
