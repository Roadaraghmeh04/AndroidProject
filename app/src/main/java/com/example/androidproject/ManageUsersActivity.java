package com.example.androidproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log; // استيراد Log
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private ArrayList<String> userList;
    private ArrayList<Integer> userIdList;
    private UsersAdapter adapter;
    private RequestQueue requestQueue;

    private final String URL_GET_USERS = "http://10.0.2.2/school_api/get_users.php";
    private final String URL_UPDATE_USER = "http://10.0.2.2/school_api/update_user.php";
    private final String URL_DELETE_USER = "http://10.0.2.2/school_api/delete_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        rvUsers = findViewById(R.id.rvUsers);
        userList = new ArrayList<>();
        userIdList = new ArrayList<>();

        adapter = new UsersAdapter(userList, new UsersAdapter.OnUserActionListener() {
            @Override
            public void onEditClicked(int position) {
                Log.d("ManageUsers", "Edit clicked for position: " + position);
                showEditDialog(position);
            }

            @Override
            public void onDeleteClicked(int position) {
                Log.d("ManageUsers", "Delete clicked for position: " + position);
                deleteUser(position);
            }
        });

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        loadUsers();
    }

    private void loadUsers() {
        Log.d("ManageUsers", "Loading users...");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_USERS, null,
                response -> {
                    userList.clear();
                    userIdList.clear();
                    Log.d("ManageUsers", "Users loaded: " + response.length());

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject user = response.getJSONObject(i);
                            int id = user.getInt("id");
                            String username = user.getString("username");
                            String role = user.getString("role");

                            userIdList.add(id);
                            userList.add(username + " (" + role + ")");

                            Log.d("ManageUsers", "User: ID=" + id + ", Username=" + username + ", Role=" + role);
                        } catch (JSONException e) {
                            Log.e("ManageUsers", "Error parsing user JSON", e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("ManageUsers", "Error loading users", error);
                    Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void showEditDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputUsername = new EditText(this);
        inputUsername.setHint("Username");
        String currentUsername = userList.get(position).split(" ")[0];
        inputUsername.setText(currentUsername);
        layout.addView(inputUsername);

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Password");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPassword);

        final EditText inputRole = new EditText(this);
        inputRole.setHint("Role (student, teacher, registrar)");
        String currentRole = userList.get(position).substring(
                userList.get(position).indexOf("(") + 1,
                userList.get(position).indexOf(")")
        );
        inputRole.setText(currentRole);
        layout.addView(inputRole);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newUsername = inputUsername.getText().toString().trim();
            String newPassword = inputPassword.getText().toString().trim();
            String newRole = inputRole.getText().toString().trim();

            Log.d("ManageUsers", "Updating user ID=" + userIdList.get(position) + " to: " + newUsername + ", " + newRole);

            if (newUsername.isEmpty() || newPassword.isEmpty() || newRole.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUser(userIdList.get(position), newUsername, newPassword, newRole);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateUser(int userId, String newUsername, String newPassword, String newRole) {
        Log.d("ManageUsers", "Sending update request for ID=" + userId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_USER,
                response -> {
                    Log.d("ManageUsers", "Update response: " + response);
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    loadUsers();
                },
                error -> {
                    Log.e("ManageUsers", "Error updating user", error);
                    Toast.makeText(this, "Error updating user", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("id", String.valueOf(userId));
                params.put("username", newUsername);
                params.put("password", newPassword);
                params.put("role", newRole);
                Log.d("ManageUsers", "Update Params: " + params.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void deleteUser(int position) {
        int userId = userIdList.get(position);
        Log.d("ManageUsers", "Sending delete request for ID=" + userId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_USER,
                response -> {
                    Log.d("ManageUsers", "Delete response: " + response);
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    loadUsers();
                },
                error -> {
                    Log.e("ManageUsers", "Error deleting user", error);
                    Toast.makeText(this, "Error deleting user", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("id", String.valueOf(userId));
                Log.d("ManageUsers", "Delete Params: " + params.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
