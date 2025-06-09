package com.example.androidproject;

import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TeacherScheduleActivity extends AppCompatActivity {

    Spinner daySpinner, subjectSpinner, teacherSpinner;
    EditText startTimeEditText, endTimeEditText;
    Button saveButton, showScheduleButton;
    TableLayout tableSchedule;

    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<Integer> subjectIds = new ArrayList<>();
    ArrayList<String> teacherList = new ArrayList<>();
    ArrayList<Integer> teacherIds = new ArrayList<>();

    int selectedSubjectId = -1;
    int selectedTeacherId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher_schedule);

        daySpinner = findViewById(R.id.spinnerDay);
        subjectSpinner = findViewById(R.id.spinnerSubject);
        teacherSpinner = findViewById(R.id.spinnerTeacher);
        startTimeEditText = findViewById(R.id.editStartTime);
        endTimeEditText = findViewById(R.id.editEndTime);
        saveButton = findViewById(R.id.btnSubmitSchedule);
        showScheduleButton = findViewById(R.id.btnShowSchedule);
        tableSchedule = findViewById(R.id.tableSchedule);

        setupDaySpinner();
        loadSubjects();
        loadTeachers();

        startTimeEditText.setOnClickListener(v -> showTimePicker(startTimeEditText));
        endTimeEditText.setOnClickListener(v -> showTimePicker(endTimeEditText));

        saveButton.setOnClickListener(v -> saveSchedule());
        showScheduleButton.setOnClickListener(v -> showSchedule());
    }

    private void setupDaySpinner() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
    }

    private void loadSubjects() {
        String url = "http://10.0.2.2/school_api/get_subject.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                subjectList.clear();
                subjectIds.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    subjectList.add(obj.getString("subject_name"));
                    subjectIds.add(obj.getInt("subject_id"));
                }
                subjectSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList));
            } catch (JSONException e) {
                Toast.makeText(this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void loadTeachers() {
        String url = "http://10.0.2.2/school_api/get_teacher.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                teacherList.clear();
                teacherIds.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    teacherList.add(obj.getString("full_name"));
                    teacherIds.add(obj.getInt("teacher_id"));
                }
                teacherSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList));
            } catch (JSONException e) {
                Toast.makeText(this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void showTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format("%02d:%02d", hour, minute);
            editText.setText(time);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void saveSchedule() {
        int subjectPos = subjectSpinner.getSelectedItemPosition();
        int teacherPos = teacherSpinner.getSelectedItemPosition();
        if (subjectPos < 0 || teacherPos < 0) {
            Toast.makeText(this, "Please select all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedSubjectId = subjectIds.get(subjectPos);
        selectedTeacherId = teacherIds.get(teacherPos);
        String day = daySpinner.getSelectedItem().toString();
        String start = startTimeEditText.getText().toString();
        String end = endTimeEditText.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please enter time", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/school_api/add_teacher_schedule.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                Log.d("SERVER_RESPONSE", response);

                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                    // ✅ الحيلة: إضافة الصف يدويًا في الجدول على الشاشة
                    // Add the new row to the table manually
                    TableRow row = new TableRow(TeacherScheduleActivity.this);
                    String[] values = {day, start, end, subjectList.get(subjectPos)};
                    for (String v : values) {
                        TextView tv = new TextView(TeacherScheduleActivity.this);
                        tv.setText(v);
                        tv.setPadding(12, 8, 12, 8);
                        row.addView(tv);
                    }
                    tableSchedule.addView(row);
                    ;

                } else {
                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("teacher_id", String.valueOf(selectedTeacherId));
                params.put("subject_id", String.valueOf(selectedSubjectId));
                params.put("day_of_week", day);
                params.put("start_time", start);
                params.put("end_time", end);
                return params;
            }
        };
        queue.add(request);
    }


    private void showSchedule() {
        int teacherPos = teacherSpinner.getSelectedItemPosition();
        if (teacherPos < 0) {
            Toast.makeText(this, "Select a teacher", Toast.LENGTH_SHORT).show();
            return;
        }
        int teacherId = teacherIds.get(teacherPos);
        String url = "http://10.0.2.2/school_api/get_schedule_by_teacher.php?teacher_id=" + teacherId;

       // tableSchedule.removeAllViews();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                TableRow header = new TableRow(this);
                String[] heads = {"Day", "Start", "End", "Subject"};
                for (String h : heads) {
                    TextView tv = new TextView(this);
                    tv.setText(h);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setPadding(12, 8, 12, 8);
                    header.addView(tv);
                }
                tableSchedule.addView(header);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    TableRow row = new TableRow(this);
                    String[] values = {
                            obj.getString("day_of_week"),
                            obj.getString("start_time"),
                            obj.getString("end_time"),
                            obj.optString("subject_name", "-")
                    };
                    for (String v : values) {
                        TextView tv = new TextView(this);
                        tv.setText(v);
                        tv.setPadding(12, 8, 12, 8);
                        row.addView(tv);
                    }
                    tableSchedule.addView(row);
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Request error", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
}
