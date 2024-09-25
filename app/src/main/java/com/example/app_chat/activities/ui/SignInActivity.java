package com.example.app_chat.activities.ui;

import android.app.Activity;
import android.content.Intent;
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

        setListeners();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String password = binding.InputPassword.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
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
            addDataToFirestore();
        });
        binding.btnSignIn.setOnClickListener(v -> {
//            addDataToFirestore();
        });
    }

    private void addDataToFirestore() {
        // Add data to Firestore here.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("email", "mmg27Gmail.com");
        db.collection("users").add(data).addOnSuccessListener(documentReference -> {
            Toast.makeText(SignInActivity.this, "Data added successfully.", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(SignInActivity.this, "Failed to add data.", Toast.LENGTH_LONG).show();
        });
    }
}