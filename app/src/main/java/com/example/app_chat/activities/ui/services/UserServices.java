package com.example.app_chat.activities.ui.services;
import com.example.app_chat.activities.ui.modelo.user;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserServices {
    private FirebaseFirestore database;
    private static UserServices instance;

    private UserServices() {
        database = FirebaseFirestore.getInstance();
    }

    public static UserServices getInstance() {
        if (instance == null) {
            instance = new UserServices();
        }
        return instance;
    }

    public void createUser(String name, String email, String image, String token) {
        database.collection("users")
                .document(email)
                .set(new user(name, email, image, token));
    }

    public user findUserByEmail(String email) {
        return database.collection("users")
                .document(email)
                .get()
                .getResult()
                .toObject(user.class);
    }
}
