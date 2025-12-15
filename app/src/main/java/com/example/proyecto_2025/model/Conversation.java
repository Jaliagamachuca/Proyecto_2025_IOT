package com.example.proyecto_2025.model;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Map;

public class Conversation {
    public String id;

    public List<String> participants;              // uids
    public Map<String, String> roles;              // uid -> role
    public Map<String, String> displayNames;       // uid -> name
    public Map<String, String> photoUrls;          // uid -> photoUrl
    public String companyId;

    public String lastMessage;
    public String lastSenderId;
    public Timestamp updatedAt;

    public Map<String, Boolean> unread;            // uid -> boolean

    public Conversation() {}
}
