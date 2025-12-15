package com.example.proyecto_2025.model;

public class ConversationUI {
    public String id;           // id de conversación (docId)
    public String title;        // nombre mostrado (otro usuario)
    public String lastMessage;  // último mensaje
    public String time;         // hora/fecha display (string ya formateado)
    public boolean unread;      // no leído (para el usuario actual)
    public String otherUserId;  // uid del otro usuario
    public String otherRole;    // guia/cliente/admin/etc
    public String photoUrl;     // foto del otro usuario (opcional)

    public ConversationUI() {}

    public ConversationUI(String id, String title, String lastMessage, String time,
                          boolean unread, String otherUserId, String otherRole, String photoUrl) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unread = unread;
        this.otherUserId = otherUserId;
        this.otherRole = otherRole;
        this.photoUrl = photoUrl;
    }
}
