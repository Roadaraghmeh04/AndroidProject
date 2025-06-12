package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.*;

public class AddStudentActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etFullName, etEmail;
    private DatePicker datePicker;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        datePicker = findViewById(R.id.datePicker);
        btnSubmit = findViewById(R.id.btnSubmitStudent);

        btnSubmit.setOnClickListener(v -> {
            new Thread(() -> {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);
                Calendar today = Calendar.getInstance();

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "⚠️ الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (selectedDate.after(today)) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "⚠️ لا يمكن اختيار تاريخ في المستقبل", Toast.LENGTH_SHORT).show());
                    return;
                }

                String dob = year + "-" + (month + 1) + "-" + day;

                runOnUiThread(() -> sendVolleyRequest(username, password, fullName, email, dob));
            }).start();
        });
    }

    private void sendVolleyRequest(String username, String password, String fullName, String email, String dob) {
        String url = "http://10.0.2.2/school_api/add_student.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(AddStudentActivity.this, "✅ تم إضافة الطالب بنجاح!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddStudentActivity.this, RegisterDashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "⚠️ خطأ في معالجة الاستجابة", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "⚠️ حدث خطأ في الاتصال: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("full_name", fullName);
                params.put("email", email);
                params.put("dob", dob);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
