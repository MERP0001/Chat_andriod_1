package com.example.app_chat.activities.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_chat.R;
import com.example.app_chat.activities.ui.adapter.UsersAdapter;
import com.example.app_chat.activities.ui.listerner.UserListerner;
import com.example.app_chat.activities.ui.modelo.user;  // Cambiado a mayúscula
import com.example.app_chat.databinding.ActivityUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements UserListerner {

    private ActivityUserBinding binding;
    private SharedPreferences preferenceManager;  // Cambiado a SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Se inicializa la variable de SharedPreferences
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Obtiene el userId del preferenceManager
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<user> users = new ArrayList<>();  // Cambiado a List<User>
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {  // Comprobación correcta de id diferente
                                continue;
                            }
                            user user = new user();  // Cambiado a User
                            user.setEmail(queryDocumentSnapshot.getString("email"));
                            user.setName(queryDocumentSnapshot.getString("name"));
                            user.setId(queryDocumentSnapshot.getString("id"));
                            user.setToken(queryDocumentSnapshot.getString("token"));
                            // Se pueden añadir otros atributos como image y token si son necesarios
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UsersAdapter userAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(userAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserCkicked(user user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}
