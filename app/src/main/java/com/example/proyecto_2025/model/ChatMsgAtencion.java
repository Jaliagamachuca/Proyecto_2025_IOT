package com.example.proyecto_2025.model;

import com.google.firebase.Timestamp;

public class ChatMsgAtencion {
    public String id;
    public String roomId;
    public String senderId;
    public String text;
    public Timestamp createdAt;

    public ChatMsgAtencion() {}

    public ChatMsgAtencion(String id, String roomId, String senderId, String text, Timestamp createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.text = text;
        this.createdAt = createdAt;
    }

}
