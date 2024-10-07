package com.example.app_chat.activities.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.app_chat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferences preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());  // Usando binding.getRoot()
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String UID = sharedPreferences.getString("user_id", null);  // Usando SharedPreferences correctamente

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
        System.out.println("Sign out");
        // Obtén la instancia de FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Cierra la sesión del usuario actual
        auth.signOut();

        // Elimina el email del usuario de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("user_id");
        editor.apply();

        // Redirige al usuario a la actividad de inicio de sesión
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }
}
