package com.example.app_chat.activities.ui.modelo;

import java.io.Serializable;

public class user implements Serializable {
    private String name;
    private String email;
    private String image;
    private String token;
    private String id;

    public user() {
    }

    public user(String name, String email, String image, String token) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.token = token;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getImage() {
        return image;
    }
    public String getToken() {
        return token;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setId(String id) {
        this.id = id;
    }
}
