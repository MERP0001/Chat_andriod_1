package com.example.app_chat.activities.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app_chat.R;
import com.example.app_chat.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperar el email del usuario de SharedPreferences
        SharedPreferences cache = getSharedPreferences("user_info", MODE_PRIVATE);
        String emailCheck = cache.getString("email", null);

        // Si el email existe, iniciar MainActivity directamente
        if (emailCheck != null) {
            System.out.println("Email in cache");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }

        setListeners();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String password = binding.InputPassword.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", email);
                            editor.apply();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    });
        });

    }

    private void setListeners() {
        binding.txtCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });
        binding.btnSignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }
}