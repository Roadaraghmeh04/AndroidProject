package com.example.androidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private List<GradeItem> gradeList;

    public GradeAdapter(List<GradeItem> gradeList) {
        this.gradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeItem item = gradeList.get(position);
        holder.tvSubject.setText(item.getSubject());
        holder.tvExamType.setText(item.getExamType());
        holder.tvGrade.setText(String.valueOf(item.getGrade()));
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvExamType, tvGrade;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvExamType = itemView.findViewById(R.id.tvExamType);
            tvGrade = itemView.findViewById(R.id.tvGrade);
        }
    }
}
