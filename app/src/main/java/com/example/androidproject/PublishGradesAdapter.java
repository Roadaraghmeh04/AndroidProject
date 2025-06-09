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

public class PublishGradesAdapter extends RecyclerView.Adapter<PublishGradesAdapter.ViewHolder> {

    private ArrayList<StudentItem> studentList;

    public PublishGradesAdapter(ArrayList<StudentItem> studentList) {
        this.studentList = studentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSubject;
        EditText etGrade;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvSubject = itemView.findViewById(R.id.tvSubjectName);
            etGrade = itemView.findViewById(R.id.etGrade);
        }

        public void bind(StudentItem student) {
            tvName.setText(student.getName());
            tvEmail.setText(student.getEmail());
            tvSubject.setText("Subject: " + student.getSubjectName());

            etGrade.setText(student.getGrade());
            etGrade.addTextChangedListener(new TextWatcher() {
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
    }

    @NonNull
    @Override
    public PublishGradesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_grade_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublishGradesAdapter.ViewHolder holder, int position) {
        holder.bind(studentList.get(position));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
