package com.example.app_chat.activities.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager; // Este import puede necesitar cambio si usas AndroidX
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app_chat.R;
import com.example.app_chat.activities.ui.adapter.CharAdapter;
import com.example.app_chat.activities.ui.modelo.ChatMessage;
import com.example.app_chat.activities.ui.modelo.user;
import com.example.app_chat.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private user receiverUser; // Cambiado a User
    private List<ChatMessage> chatMessages;
    private CharAdapter chatAdapter;
    private android.content.SharedPreferences preferenceManager; // Cambiar tipo a SharedPreferences
    private FirebaseFirestore database;
    private String id_sender_activo = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadDetailsReceiver();
        init(); // Llamar a init() después de cargar detalles del receptor
        setListeners();// Llamar a setListeners para inicializar los listeners
        listenMessages();// Llamar a listenMessages para escuchar mensajes

        findViewById(R.id.imageBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.shareImage ).setOnClickListener(v -> openImageChooser());
    }
    //Subir imagen
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToDatabase(imageUri);
        }
    }

    private void uploadImageToDatabase(Uri imageUri) {
        // Aquí puedes agregar el código para subir la imagen a tu base de datos

    }




    //=============================
    private void init() {
        // Initialize preferenceManager using the correct method
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        chatMessages = new ArrayList<>();

        // Asegúrate de que receiverUser no sea nulo antes de acceder a su imagen
        if (receiverUser != null) {
            chatAdapter = new CharAdapter(
                    chatMessages,
//                    getBitmapFromEncodedString(receiverUser.getImage()), // Usar método getter para el campo privado
                    id_sender_activo // Proporcionar un valor por defecto para getString
            );

            binding.chatRecyclerView.setAdapter(chatAdapter);
        }
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put("id_sender", id_sender_activo);
        message.put("id_receiver", receiverUser.getId());
        message.put("id_message", binding.inputMessage.getText().toString());
        message.put("time_stamp", new Date());
        database.collection("id_chat").add(message);
        binding.inputMessage.setText(null);
    }

//    private void sendNotification(String message) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("users").document(id_sender_activo).get()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
//                        String nameSender = task.getResult().getString("name");
//                        db.collection("users").document(receiverUser.getId())
//                                .get()
//                                .addOnCompleteListener(doccumentSnapshot -> {
//                                    String token = doccumentSnapshot.getResult().getString("token");
//
//                                    Map<String, String> notification = new HashMap<>();
//                                    notification.put("title", nameSender);
//                                    notification.put("body", message);
//
//                                    Map<String, String> data = new HashMap<>();
//                                    data.put("id_sender", id_sender_activo);
//                                    data.put("id_receiver", receiverUser.getId());
//
//                                    FirebaseMessaging fm = FirebaseMessaging.getInstance();
//                                    String messageId = UUID.randomUUID().toString();
//                                    fm.send(new RemoteMessage.Builder(token)
//                                            .setMessageId(messageId)
//                                            .addData("notification", new Gson().toJson(notification))
//                                            .addData("data", new Gson().toJson(data))
//                                            .build());
//                                })
//                                .addOnFailureListener(e -> {
//                                    Toast.makeText(this, "Failed to send notification", Toast.LENGTH_LONG).show();
//                                });
//                    } else{
//                        Log.d("ChatActivity", "Error getting sender name", task.getException());
//                    }
//                });
//    }

    private void listenMessages() {
        database.collection("id_chat")
                .whereEqualTo("id_sender", id_sender_activo)
                .whereEqualTo("id_receiver", receiverUser.getId())
                .addSnapshotListener(eventListener);

        database.collection("id_chat")
                .whereEqualTo("id_sender", receiverUser.getId())
                .whereEqualTo("id_receiver", id_sender_activo)
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
                    Log.d("ChatActivity", "Mensaje recibido: " +
                            "\nID Emisor: " + chatMessage.senderId +
                            "\nID Receptor: " + chatMessage.receiverId +
                            "\nMensaje: " + chatMessage.message +
                            "\nTimestamp: " + chatMessage.dateTime);
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
//        Log.d("ChatActivity", "Mensajes recibidos: " + value.getDocumentChanges().size());
//        Log.d("ChatActivity", "Mensajes recibidos: " + value.getDocumentChanges().size());


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
