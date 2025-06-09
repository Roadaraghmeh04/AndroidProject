package com.example.androidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onEditClicked(int position);
        void onDeleteClicked(int position);
    }

    private List<String> userList;
    private OnUserActionListener listener;

    public UsersAdapter(List<String> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userText = userList.get(position);
        holder.tvUsername.setText(userText);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        ImageButton btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
           btnEdit = itemView.findViewById(R.id.btnEdit);
           btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
