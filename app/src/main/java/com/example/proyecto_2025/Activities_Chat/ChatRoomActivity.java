package com.example.proyecto_2025.Activities_Chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.adapter.ChatMessageAdapter;
import com.example.proyecto_2025.data.repository.ChatRepository;
import com.example.proyecto_2025.databinding.ActivityChatRoomBinding;
import com.example.proyecto_2025.model.ChatMsgAtencion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_ROOM";

    private ActivityChatRoomBinding binding;
    private final ChatRepository chatRepo = new ChatRepository();

    private ChatMessageAdapter adapter;
    private ListenerRegistration reg;

    private String conversationId;
    private String otherUserId;
    private String otherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversationId = getIntent().getStringExtra("conversationId");
        otherUserId    = getIntent().getStringExtra("otherUserId");
        otherName      = getIntent().getStringExtra("otherName");

        Log.d(TAG, "extras conversationId=" + conversationId + " otherUserId=" + otherUserId + " otherName=" + otherName);

        binding.toolbarChat.setTitle(!TextUtils.isEmpty(otherName) ? otherName : "Atención al cliente");
        binding.toolbarChat.setNavigationOnClickListener(v -> finish());

        String myUid = FirebaseAuth.getInstance().getUid();
        if (TextUtils.isEmpty(myUid)) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new ChatMessageAdapter(myUid);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(adapter);

        binding.btnSend.setOnClickListener(v -> sendMessage());

        if (TextUtils.isEmpty(conversationId)) {
            Toast.makeText(this, "conversationId vacío", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Marcar leído al entrar
        chatRepo.markRead(conversationId, myUid);

        listenMessages();
    }

    private void listenMessages() {
        if (reg != null) reg.remove();

        reg = chatRepo.listenMessages(conversationId, new ChatRepository.MessagesCb() {
            @Override
            public void onData(List<ChatMsgAtencion> list) {
                if (list == null) list = new ArrayList<>();
                adapter.replace(list);
                if (!list.isEmpty()) binding.rvMessages.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "listenMessages error", e);
                Toast.makeText(ChatRoomActivity.this, "Error leyendo chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String myUid = FirebaseAuth.getInstance().getUid();
        if (TextUtils.isEmpty(myUid)) return;

        String txt = (binding.etMsg.getText() != null) ? binding.etMsg.getText().toString().trim() : "";
        if (txt.isEmpty()) return;

        chatRepo.sendMessage(conversationId, myUid, txt, otherUserId, new ChatRepository.SimpleCb() {
            @Override public void onOk() { binding.etMsg.setText(""); }
            @Override public void onError(Exception e) {
                Log.e(TAG, "sendMessage error", e);
                Toast.makeText(ChatRoomActivity.this, "Error enviando: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reg != null) reg.remove();
    }
}
