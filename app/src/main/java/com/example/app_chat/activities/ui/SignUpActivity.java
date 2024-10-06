package com.example.app_chat.activities.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app_chat.R;
import com.example.app_chat.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class  SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private SharedPreferences sharedPreferences;
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
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();// Aquí se obtiene el UID del usuario
                                addDataToFirestore(uid);  // Pasamos el UID al método addDataToFirestore
                                sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("user_id", uid);  // Aquí se guarda el UID del usuario en las preferencias compartidas
                                editor.apply();
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

    private void addDataToFirestore(String uid) {
        String email = binding.inputEmail.getText().toString();
        String name = binding.inputName.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("id", uid);  // Aquí se guarda el UID del usuario en Firestore
        db.collection("users").document(uid).set(data).addOnSuccessListener(aVoid -> {
            Toast.makeText(SignUpActivity.this, "Data added successfully.", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(SignUpActivity.this, "Failed to add data.", Toast.LENGTH_LONG).show();
        });
    }
}