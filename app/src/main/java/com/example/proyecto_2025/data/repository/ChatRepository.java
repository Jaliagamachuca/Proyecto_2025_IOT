package com.example.proyecto_2025.data.repository;

import androidx.annotation.Nullable;

import com.example.proyecto_2025.model.ChatMessage;
import com.example.proyecto_2025.model.Conversation;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ChatRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface ConversationsCb {
        void onData(java.util.List<Conversation> list);
        void onError(Exception e);
    }

    public interface MessagesCb {
        void onData(java.util.List<ChatMessage> list);
        void onError(Exception e);
    }

    public interface SimpleCb {
        void onOk();
        void onError(Exception e);
    }

    public ListenerRegistration listenMyConversations(String myUid, ConversationsCb cb) {
        return db.collection("conversations")
                .whereArrayContains("participants", myUid)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) { cb.onError(e); return; }
                    java.util.List<Conversation> out = new java.util.ArrayList<>();
                    if (snap != null) {
                        snap.getDocuments().forEach(d -> {
                            Conversation c = d.toObject(Conversation.class);
                            if (c != null) {
                                c.id = d.getId();
                                out.add(c);
                            }
                        });
                    }
                    cb.onData(out);
                });
    }

    public ListenerRegistration listenMessages(String conversationId, MessagesCb cb) {
        return db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) { cb.onError(e); return; }
                    java.util.List<ChatMessage> out = new java.util.ArrayList<>();
                    if (snap != null) {
                        snap.getDocuments().forEach(d -> {
                            ChatMessage m = d.toObject(ChatMessage.class);
                            if (m != null) {
                                m.id = d.getId();
                                out.add(m);
                            }
                        });
                    }
                    cb.onData(out);
                });
    }

    public void sendMessage(String conversationId,
                            String senderId,
                            String text,
                            @Nullable String otherUid,
                            SimpleCb cb) {

        String clean = (text == null) ? "" : text.trim();
        if (clean.isEmpty()) { cb.onOk(); return; }

        DocumentReference convRef = db.collection("conversations").document(conversationId);
        DocumentReference msgRef = convRef.collection("messages").document();

        Map<String, Object> msg = new HashMap<>();
        msg.put("senderId", senderId);
        msg.put("text", clean);
        msg.put("createdAt", FieldValue.serverTimestamp());

        Map<String, Object> convUpd = new HashMap<>();
        convUpd.put("lastMessage", clean);
        convUpd.put("lastSenderId", senderId);
        convUpd.put("updatedAt", FieldValue.serverTimestamp());

        convUpd.put("unread." + senderId, false);
        if (otherUid != null && !otherUid.isEmpty()) {
            convUpd.put("unread." + otherUid, true);
        }

        db.runBatch(b -> {
                    b.set(msgRef, msg);
                    b.update(convRef, convUpd);
                }).addOnSuccessListener(v -> cb.onOk())
                .addOnFailureListener(cb::onError);
    }

    public void markRead(String conversationId, String myUid) {
        db.collection("conversations")
                .document(conversationId)
                .update("unread." + myUid, false);
    }

    public String buildConversationId(String uidA, String uidB) {
        if (uidA.compareTo(uidB) < 0) return uidA + "_" + uidB;
        return uidB + "_" + uidA;
    }

    public void ensureConversation(String uidA, String roleA, String nameA, String photoA,
                                   String uidB, String roleB, String nameB, String photoB,
                                   SimpleCb cb) {

        String id = buildConversationId(uidA, uidB);
        DocumentReference convRef = db.collection("conversations").document(id);

        convRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) { cb.onOk(); return; }

            Map<String, Object> data = new HashMap<>();
            java.util.List<String> participants = new java.util.ArrayList<>();
            participants.add(uidA);
            participants.add(uidB);

            Map<String, String> roles = new HashMap<>();
            roles.put(uidA, roleA);
            roles.put(uidB, roleB);

            Map<String, String> displayNames = new HashMap<>();
            displayNames.put(uidA, nameA != null ? nameA : "");
            displayNames.put(uidB, nameB != null ? nameB : "");

            Map<String, String> photoUrls = new HashMap<>();
            photoUrls.put(uidA, photoA != null ? photoA : "");
            photoUrls.put(uidB, photoB != null ? photoB : "");

            Map<String, Boolean> unread = new HashMap<>();
            unread.put(uidA, false);
            unread.put(uidB, false);

            data.put("participants", participants);
            data.put("roles", roles);
            data.put("displayNames", displayNames);
            data.put("photoUrls", photoUrls);
            data.put("unread", unread);
            data.put("lastMessage", "");
            data.put("lastSenderId", "");
            data.put("updatedAt", FieldValue.serverTimestamp());

            convRef.set(data)
                    .addOnSuccessListener(v -> cb.onOk())
                    .addOnFailureListener(cb::onError);

        }).addOnFailureListener(cb::onError);
    }
}
