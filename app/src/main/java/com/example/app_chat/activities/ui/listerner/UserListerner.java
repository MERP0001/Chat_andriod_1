package com.example.app_chat.activities.ui.listerner;

import com.example.app_chat.activities.ui.modelo.user;
import com.google.firebase.firestore.auth.User;

public interface UserListerner {

    void onUserCkicked(user user);

}
