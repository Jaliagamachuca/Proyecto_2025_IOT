package com.example.proyecto_2025.Activities_Chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.adapter.ChatMessageAdapter;
import com.example.proyecto_2025.databinding.ActivityChatRoomBinding;
import com.example.proyecto_2025.model.ChatMsgAtencion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private ActivityChatRoomBinding binding;
    private FirebaseFirestore db;
    private ChatMessageAdapter adapter;

    private String roomId;
    private String otherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        roomId = getIntent().getStringExtra("roomId");
        otherName = getIntent().getStringExtra("otherName");

        binding.toolbarChat.setTitle(otherName != null ? otherName : "Chat");
        binding.toolbarChat.setNavigationOnClickListener(v -> finish());

        String myUid = FirebaseAuth.getInstance().getUid();
        adapter = new ChatMessageAdapter(myUid);

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(adapter);

        binding.btnSend.setOnClickListener(v -> sendMessage());

        listenMessages();
    }

    private void listenMessages() {
        if (TextUtils.isEmpty(roomId)) {
            Toast.makeText(this, "roomId vacío", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("chatRooms")
                .document(roomId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snaps, err) -> {
                    if (err != null || snaps == null) return;

                    List<ChatMsgAtencion> list = new ArrayList<>();
                    for (var d : snaps.getDocuments()) {
                        ChatMsgAtencion m = d.toObject(ChatMsgAtencion.class);
                        if (m != null) list.add(m);
                    }
                    adapter.replace(list);
                    if (!list.isEmpty()) binding.rvMessages.scrollToPosition(list.size() - 1);
                });
    }

    private void sendMessage() {
        String myUid = FirebaseAuth.getInstance().getUid();
        if (TextUtils.isEmpty(myUid)) return;

        String txt = binding.etMsg.getText() != null ? binding.etMsg.getText().toString().trim() : "";
        if (txt.isEmpty()) return;

        String msgId = db.collection("chatRooms").document(roomId)
                .collection("messages").document().getId();

        ChatMsgAtencion msg = new ChatMsgAtencion(
                msgId,
                roomId,
                myUid,
                txt,
                System.currentTimeMillis()
        );

        db.collection("chatRooms").document(roomId)
                .collection("messages").document(msgId)
                .set(msg)
                .addOnSuccessListener(ok -> binding.etMsg.setText(""))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
