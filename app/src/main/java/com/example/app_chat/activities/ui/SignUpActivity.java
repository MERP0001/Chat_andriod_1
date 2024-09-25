package com.example.app_chat.activities.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app_chat.R;
import com.example.app_chat.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListernes();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        binding.btnSignUp.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String password = binding.InputPassword.getText().toString();
            String confirmPassword = binding.InputConfirmPassword.getText().toString();
            if (password.equals(confirmPassword)) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListernes() {
        binding.txtSignIn.setOnClickListener(v -> onBackPressed());
    }
}