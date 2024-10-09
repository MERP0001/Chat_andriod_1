package com.example.app_chat.activities.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.app_chat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    private static final int REQUEST_STORAGE_PERMISSION_CODE = 103; // Código de solicitud para permisos de almacenamiento
    private ActivityMainBinding binding;
    private Uri imageUri; // Para almacenar la URI seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestNotificationPermission();
        setListeners();

        // Verifica y solicita permisos al inicio
        if (!hasPermissions()) {
            requestPermissions();
        }
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserActivity.class)));
        binding.imageProfile.setOnClickListener(v -> openImageChooser()); // Listener para elegir imagen
    }

    private void openImageChooser() {
        // Verificar permisos antes de abrir el selector de imágenes
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Permiso temporal para URI
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                // Aquí puedes procesar el archivo
                try {
                    String authority = getPackageName() + ".fileprovider"; // Reemplaza con tu autoridad
                    Uri contentUri = Uri.parse(selectedFileUri.toString()); // Para obtener el URI directamente
                    InputStream inputStream = getContentResolver().openInputStream(contentUri);
                    // Aquí puedes procesar el InputStream (por ejemplo, subir la imagen a Firebase)
                    Toast.makeText(this, "Imagen seleccionada: " + contentUri.toString(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    // Manejo de la excepción
                    e.printStackTrace();
                    Toast.makeText(this, "Archivo no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser(); // Vuelve a intentar abrir el selector de imágenes si el permiso fue concedido
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Notification", "Permiso de notificación concedido");
            } else {
                Log.d("Notification", "Permiso de notificación denegado");
            }
        }
    }

    private void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        String userId = getSharedPreferences("user_info", MODE_PRIVATE).getString("user_id", null);
        Log.d("ID_USUARIO", userId);
        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId)
                    .update("token", "0")
                    .addOnSuccessListener(aVoid -> Log.d("SignOut", "Token eliminado exitosamente"))
                    .addOnFailureListener(e -> Log.d("SignOut", "Error al eliminar el token: ", e));
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .update("token", null)
                .addOnSuccessListener(aVoid -> Log.d("SignOut", "Token eliminado exitosamente"))
                .addOnFailureListener(e -> Log.d("SignOut", "Error al eliminar el token: ", e));


        auth.signOut();
        fm.deleteToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignOut", "Token eliminado exitosamente");
                    } else {
                        Log.d("SignOut", "Error al eliminar el token: ", task.getException());
                    }
                });
        getSharedPreferences("user_info", MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_STORAGE_PERMISSION_CODE);
        }
    }
}
