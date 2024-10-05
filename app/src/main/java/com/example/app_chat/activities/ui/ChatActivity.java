package com.example.app_chat.activities.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager; // Este import puede necesitar cambio si usas AndroidX
import android.util.Base64;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app_chat.R;
import com.example.app_chat.activities.ui.adapter.CharAdapter;
import com.example.app_chat.activities.ui.modelo.ChatMessage;
import com.example.app_chat.activities.ui.modelo.user;
import com.example.app_chat.databinding.ActivityChatBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

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
        setListeners();// Llamar a setListeners para inicializar los listeners
        listenMessages(); // Llamar a listenMessages para escuchar mensajes
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
                    preferenceManager.getString("id", null) // Proporcionar un valor por defecto para getString
            );

            binding.chatRecyclerView.setAdapter(chatAdapter);
        }
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put("id_sender", preferenceManager.getString("id", null));
        message.put("id_receiver", receiverUser.getId());
        message.put("id_message", binding.inputMessage.getText().toString());
        message.put("time_stamp", new Date());
        database.collection("id_chat").add(message);
        binding.inputMessage.setText(null);
    }

    private void listenMessages() {
        database.collection("id_chat")
                .whereEqualTo("id_sender", preferenceManager.getString("id", null))
                .whereEqualTo("id_receiver", receiverUser.getId())
                .addSnapshotListener(eventListener);

        database.collection("id_chat")
                .whereEqualTo("id_sender", receiverUser.getId())
                .whereEqualTo("id_receiver", preferenceManager.getString("id", null))
                .addSnapshotListener(eventListener);
    }



    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        int count = 0;
        if (value != null) {
            count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("id_sender");
                    chatMessage.receiverId = documentChange.getDocument().getString("id_receiver");
                    chatMessage.message = documentChange.getDocument().getString("id_message");
                    chatMessage.dateTime = getReadableDateTimestamp(documentChange.getDocument().getDate("time_stamp"));
                    chatMessage.dateObject = documentChange.getDocument().getDate("time_stamp");
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);

    };




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
        binding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });
    }

    private String getReadableDateTimestamp(Date date) {
        return android.text.format.DateFormat.format("dd/MM/yyyy hh:mm a", date).toString();
    }
}
