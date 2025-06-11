package com.example.androidproject;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PublishGradesAdapter extends RecyclerView.Adapter<PublishGradesAdapter.GradeViewHolder> {

    private final ArrayList<StudentItem> students;

    public PublishGradesAdapter(ArrayList<StudentItem> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_grade_row, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        StudentItem student = students.get(position);
        holder.tvName.setText(student.getName());
        holder.tvEmail.setText(student.getEmail());
        holder.tvSubject.setText("Subject: " + student.getSubjectName());

        holder.etGrade.setText(student.getGrade());

        holder.etGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                student.setGrade(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSubject;
        EditText etGrade;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvSubject = itemView.findViewById(R.id.tvSubjectName);
            etGrade = itemView.findViewById(R.id.etGrade);
        }
    }
}
