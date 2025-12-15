package com.example.proyecto_2025.model;

public class ConversationUI {

    public String id;           // roomId
    public String title;        // nombre visible
    public String lastMessage;
    public String time;
    public boolean unread;
    public String otherUserId;
    public String otherRole;
    public String otherPhotoUrl; // opcional (si no lo usas, pasa null)

    // 🔹 Constructor vacío (Firestore / JavaBeans)
    public ConversationUI() {}

    // 🔹 Constructor completo (para UI)
    public ConversationUI(
            String id,
            String title,
            String lastMessage,
            String time,
            boolean unread,
            String otherUserId,
            String otherRole,
            String otherPhotoUrl
    ) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unread = unread;
        this.otherUserId = otherUserId;
        this.otherRole = otherRole;
        this.otherPhotoUrl = otherPhotoUrl;
    }
}
