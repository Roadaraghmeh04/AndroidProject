package com.example.androidproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubmitAssignmentActivity extends AppCompatActivity {

    private Spinner spinnerAssignments;
    private Button btnChooseFile, btnSubmit;
    private TextView tvSelectedFile;

    private Uri selectedFileUri;
    private static final int PICK_FILE_REQUEST = 1;
    private static final String LOAD_ASSIGNMENTS_URL = "http://10.0.2.2/school_api/load_assignments.php";
    private static final String UPLOAD_ASSIGNMENT_URL = "http://10.0.2.2/school_api/submit_assignment.php";

    private int studentId = 1; // حطيه من الـ SharedPreferences لاحقًا إذا بدك
    private int assignmentId = 1;

    private final ArrayList<String> assignmentTitles = new ArrayList<>();
    private final Map<String, Integer> assignmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        spinnerAssignments = findViewById(R.id.spinnerAssignments);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, assignmentTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignments.setAdapter(adapter);

        loadAssignments(adapter);

        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        btnSubmit.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                String selectedTitle = spinnerAssignments.getSelectedItem().toString();
                assignmentId = assignmentMap.get(selectedTitle);
                uploadFile(selectedFileUri);
            } else {
                Toast.makeText(this, "Please select a file first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAssignments(ArrayAdapter<String> adapter) {
        StringRequest request = new StringRequest(Request.Method.GET, LOAD_ASSIGNMENTS_URL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        assignmentTitles.clear();
                        assignmentMap.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("assignment_id");
                            String title = obj.getString("title");
                            assignmentTitles.add(title);
                            assignmentMap.put(title, id);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing assignments", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load assignments", Toast.LENGTH_SHORT).show();
                    Log.e("LOAD_ASSIGN", error.toString());
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            tvSelectedFile.setText("Selected: " + fileName);
        }
    }

    private void uploadFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileData = getBytes(inputStream);
            String fileName = getFileName(fileUri);

            ProgressDialog progressDialog = ProgressDialog.show(this, "Uploading", "Please wait...", true);

            VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_ASSIGNMENT_URL,
                    response -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("UPLOAD_ERROR", error.toString());
                    });

            Map<String, String> stringParams = new HashMap<>();
            stringParams.put("assignment_id", String.valueOf(assignmentId));
            stringParams.put("student_id", String.valueOf(studentId));

            Map<String, VolleyMultipartRequest.DataPart> byteParams = new HashMap<>();
            byteParams.put("file", new VolleyMultipartRequest.DataPart(fileName, fileData));

            request.setParams(stringParams);
            request.setByteData(byteParams);

            Volley.newRequestQueue(this).add(request);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "File read error", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }
}
