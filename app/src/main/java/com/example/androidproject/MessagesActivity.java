package com.example.androidproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    private ListView listMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        listMessages = findViewById(R.id.listMessages);

        int teacherId = getIntent().getIntExtra("teacher_id", -1);

        ArrayList<String> messages = new ArrayList<>();

        if (teacherId == 3) { // Sami Awad
            messages.add("📥 Ahmad Khalid submitted Math Homework.");
            messages.add("🕒 Sunday 08:00 AM - Math (Room 101)");
            messages.add("🕒 Tuesday 08:00 AM - Math (Room 101)");
        } else if (teacherId == 4) { // Sara Omar
            messages.add("📥 Lina Yasin submitted Science Worksheet.");
            messages.add("🕒 Sunday 09:00 AM - Science (Room 102)");
            messages.add("🕒 Tuesday 09:00 AM - Science (Room 102)");
        } else {
            messages.add("No messages available.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        listMessages.setAdapter(adapter);
    }
}
