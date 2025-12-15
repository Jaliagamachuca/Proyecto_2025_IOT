package com.example.proyecto_2025.model;

public class ChatMsgAtencion {
    public String id;
    public String roomId;
    public String senderId;
    public String text;
    public long createdAt;

    public ChatMsgAtencion() {}

    public ChatMsgAtencion(String id, String roomId, String senderId, String text, long createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.text = text;
        this.createdAt = createdAt;
    }
}
