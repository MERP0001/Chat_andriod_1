package com.example.app_chat.activities.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.app_chat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferences preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());  // Usando binding.getRoot()

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());  // Usando SharedPreferences correctamente
        setListeners();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> {
            signOut();  // Corregido a signOut
        });
        binding.fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
        });
    }

    private void signOut() {
        // Lógica de cierre de sesión
    }
}
