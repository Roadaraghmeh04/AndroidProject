package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Spinner spinnerRole;
    MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnLogin = findViewById(R.id.btnLogin);

        String[] roles = {"Student", "Teacher", "Registrar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnLogin.setOnClickListener(view -> {
            final String email = etEmail.getText().toString().trim();
            final String password = etPassword.getText().toString().trim();
            final String role = spinnerRole.getSelectedItem().toString().toLowerCase();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2/school_api/login.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.e("LOGIN_RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            if (status.equals("success")) {
                                String fullName = jsonObject.getString("full_name");
                                int teacherId = jsonObject.optInt("user_id", -1); // 👈 احصل على ID المعلم

                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                switch (role) {
                                    case "teacher":
                                        Intent intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
                                        intent.putExtra("full_name", fullName);
                                        intent.putExtra("teacher_id", teacherId); // ✅ تمرير teacher_id
                                        startActivity(intent);
                                        break;
                                    case "student":
                                        // startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                                        break;
                                    case "registrar":
                                        // startActivity(new Intent(LoginActivity.this, RegistrarDashboardActivity.class));
                                        break;
                                }
                            } else {
                                String message = jsonObject.optString("message", "Login failed");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("role", role);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(stringRequest);
        });
    }
}
