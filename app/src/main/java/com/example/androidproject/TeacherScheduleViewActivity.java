package com.example.androidproject;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TeacherScheduleViewActivity extends AppCompatActivity {

    RecyclerView recyclerViewSchedule;
    ScheduleAdapter scheduleAdapter;
    ArrayList<ScheduleItem> scheduleList = new ArrayList<>();
    int teacherId = -1;

    Spinner spinnerDay, spinnerSubject, spinnerTeacher;
    EditText editStartTime, editEndTime;
    Button btnSubmitSchedule, btnShowSchedule;
    TableLayout tableSchedule;
    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<Integer> subjectIds = new ArrayList<>();
    ArrayList<String> teacherList = new ArrayList<>();
    ArrayList<Integer> teacherIds = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean fromRegistrar = getIntent().getBooleanExtra("from_registrar", false);

        if (fromRegistrar) {
            setContentView(R.layout.activity_add_teacher_schedule);

            spinnerDay = findViewById(R.id.spinnerDay);
            spinnerSubject = findViewById(R.id.spinnerSubject);
            spinnerTeacher = findViewById(R.id.spinnerTeacher);
            editStartTime = findViewById(R.id.editStartTime);
            editEndTime = findViewById(R.id.editEndTime);
            btnSubmitSchedule = findViewById(R.id.btnSubmitSchedule);
            btnShowSchedule = findViewById(R.id.btnShowSchedule);
            tableSchedule = findViewById(R.id.tableSchedule);

            setupDaySpinner();
            loadSubjects();
            loadTeachers();

            editStartTime.setOnClickListener(v -> showTimePicker(editStartTime));
            editEndTime.setOnClickListener(v -> showTimePicker(editEndTime));

            btnSubmitSchedule.setOnClickListener(v -> saveSchedule());
            btnShowSchedule.setOnClickListener(v -> showTeacherSchedule());
            return;
        }

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

    private void setupDaySpinner() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
    }

    private void loadSubjects() {
        String url = "http://10.0.2.2/school_api/get_subject.php";
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(adapter);
            } catch (JSONException e) {
                Toast.makeText(this, "Error loading subjects", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Network error while loading subjects", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(request);
    }

    private void loadTeachers() {
        String url = "http://10.0.2.2/school_api/get_teacher.php";
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTeacher.setAdapter(adapter);
            } catch (JSONException e) {
                Toast.makeText(this, "Error loading teachers", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Network error while loading teachers", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(request);
    }

    private void showTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            editText.setText(time);
        }, hour, minute, true);
        dialog.show();
    }

    private void saveSchedule() {
        int subjectIndex = spinnerSubject.getSelectedItemPosition();
        int teacherIndex = spinnerTeacher.getSelectedItemPosition();

        if (subjectIndex < 0 || teacherIndex < 0) {
            Toast.makeText(this, "Please select subject and teacher", Toast.LENGTH_SHORT).show();
            return;
        }

        int subjectId = subjectIds.get(subjectIndex);
        int selectedTeacherId = teacherIds.get(teacherIndex);
        String day = spinnerDay.getSelectedItem().toString();
        String start = editStartTime.getText().toString();
        String end = editEndTime.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please choose start and end time", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/school_api/add_teacher_schedule.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(this, "Schedule saved", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("subject_id", String.valueOf(subjectId));
                map.put("teacher_id", String.valueOf(selectedTeacherId));
                map.put("day_of_week", day);
                map.put("start_time", start);
                map.put("end_time", end);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void showTeacherSchedule() {
        int index = spinnerTeacher.getSelectedItemPosition();
        if (index < 0) {
            Toast.makeText(this, "Select a teacher", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = teacherIds.get(index);
        String url = "http://10.0.2.2/school_api/get_schedule_by_teacher.php?teacher_id=" + id;
        tableSchedule.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                TableRow header = new TableRow(this);
                String[] titles = {"Day", "Start", "End", "Subject"};
                for (String title : titles) {
                    TextView tv = new TextView(this);
                    tv.setText(title);
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
                    for (String val : values) {
                        TextView tv = new TextView(this);
                        tv.setText(val);
                        tv.setPadding(12, 8, 12, 8);
                        row.addView(tv);
                    }
                    tableSchedule.addView(row);
                }

            } catch (JSONException e) {
                Toast.makeText(this, "Failed to parse schedule", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error loading schedule", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void loadSchedule() {
        if (teacherId == -1) {
            Toast.makeText(this, "Teacher ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/school_api/get_schedule_by_teacher.php?teacher_id=6" + teacherId;
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