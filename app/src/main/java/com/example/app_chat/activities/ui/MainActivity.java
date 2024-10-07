package com.example.app_chat.activities.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.app_chat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

        requestNotificationPermission();

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

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar el permiso de notificaciones
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario otorgó el permiso
                Log.d("Notification", "Permiso de notificación concedido");
            } else {
                // El usuario denegó el permiso
                Log.d("Notification", "Permiso de notificación denegado");
            }
        }
    }

    private void signOut() {
        System.out.println("Sign out");
        // Obtén la instancia de FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Obtén el user_id del SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);

        if (userId != null) {
            // Elimina el token de la base de datos (Firestore)
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId)
                    .update("token", FieldValue.delete())
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Token eliminado exitosamente");
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Error al eliminar el token: ");
                    });
        }

        // Cierra la sesión del usuario actual
        auth.signOut();

        // Elimina el email y user_id de SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("user_id");
        editor.apply();

        // Redirige al usuario a la actividad de inicio de sesión
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }
}
