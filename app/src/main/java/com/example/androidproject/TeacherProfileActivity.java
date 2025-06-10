package com.example.androidproject;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class TeacherProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvSubject, tvGender, tvDOB, tvIDNumber,
            tvAddress, tvBio;

    int teacherId = 4; // غيّر حسب الـ ID المُسجّل

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        // الربط بين TextView والـ XML
        tvName = findViewById(R.id.tvTeacherName);
        tvEmail = findViewById(R.id.tvTeacherEmail);
        tvPhone = findViewById(R.id.tvTeacherPhone);
        tvSubject = findViewById(R.id.tvTeacherSubject);
        tvGender = findViewById(R.id.tvTeacherGender);
        tvDOB = findViewById(R.id.tvTeacherDOB);
        tvIDNumber = findViewById(R.id.tvTeacherID);
        tvAddress = findViewById(R.id.tvTeacherAddress);
        tvBio = findViewById(R.id.tvTeacherBio);

        loadTeacherProfile();
    }

    private void loadTeacherProfile() {
        String url = "http://10.0.2.2/school_api/get_teacher_profile.php?teacher_id=" + teacherId;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JSONObject data = json.getJSONObject("data");

                    tvName.setText(data.getString("full_name"));
                    tvEmail.setText(data.getString("email"));
                    tvPhone.setText("Phone number: " + data.getString("phone"));
                    tvSubject.setText("Major " + data.getString("subject"));
                    tvGender.setText("Gender " + data.getString("gender"));
                    tvDOB.setText("Date Of Birth " + data.getString("dob"));
                    tvIDNumber.setText("ID " + data.getString("id_number"));
                    tvAddress.setText("Address " + data.getString("address"));
                    //tvClasses.setText("#Classes " + data.getInt("classes"));
                    // tvSubjectsCount.setText("عدد المواد: " + data.getInt("subjects"));
                    //tvHireDate.setText("تاريخ التوظيف: " + data.getString("hire_date"));
                    tvBio.setText(data.getString("bio"));

                } else {
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(this, "خطأ أثناء قراءة البيانات", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "فشل الاتصال بالخادم", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
}

