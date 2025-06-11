package com.example.androidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<ScheduleItem> scheduleList;

    public ScheduleAdapter(ArrayList<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime, tvSubject, tvRoom;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvRoom = itemView.findViewById(R.id.tvRoom);
        }
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);
        holder.tvDay.setText(item.getDay());
        holder.tvTime.setText(item.getTime());
        holder.tvSubject.setText("Subject: " + item.getSubject());
        holder.tvRoom.setText("Room: " + item.getRoom());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
}