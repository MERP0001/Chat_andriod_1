package com.example.app_chat.activities.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager; // Este import puede necesitar cambio si usas AndroidX
import android.util.Base64;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app_chat.R;
import com.example.app_chat.activities.ui.adapter.CharAdapter;
import com.example.app_chat.activities.ui.modelo.ChatMessage;
import com.example.app_chat.activities.ui.modelo.user;
import com.example.app_chat.databinding.ActivityChatBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private user receiverUser; // Cambiado a User
    private List<ChatMessage> chatMessages;
    private CharAdapter chatAdapter;
    private android.content.SharedPreferences preferenceManager; // Cambiar tipo a SharedPreferences
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadDetailsReceiver();
        init(); // Llamar a init() después de cargar detalles del receptor
        setListeners(); // Llamar a setListeners para inicializar los listeners
    }

    private void init() {
        // Initialize preferenceManager using the correct method
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        chatMessages = new ArrayList<>();

        // Asegúrate de que receiverUser no sea nulo antes de acceder a su imagen
        if (receiverUser != null) {
            chatAdapter = new CharAdapter(
                    chatMessages,
//                    getBitmapFromEncodedString(receiverUser.getImage()), // Usar método getter para el campo privado
                    preferenceManager.getString("user_id", null) // Proporcionar un valor por defecto para getString
            );

            binding.chatRecyclerView.setAdapter(chatAdapter);
        }
        database = FirebaseFirestore.getInstance();
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null; // Retornar null si la cadena está vacía
    }

    private void loadDetailsReceiver() {
        receiverUser = (user) getIntent().getSerializableExtra("user"); // Asegúrate de que User implemente Serializable
        if (receiverUser != null) {
            binding.textName.setText(receiverUser.getName());
        }
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
