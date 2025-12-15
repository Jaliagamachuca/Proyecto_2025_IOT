package com.example.proyecto_2025.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    public String id;
    public String senderId;
    public String text;
    public Timestamp createdAt;

    public ChatMessage() {}
}
