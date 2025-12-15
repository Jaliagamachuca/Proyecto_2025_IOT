package com.example.proyecto_2025.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.ChatMsgAtencion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.VH> {

    private final List<ChatMsgAtencion> data = new ArrayList<>();
    private final String myUid;

    public ChatMessageAdapter(String myUid) {
        this.myUid = myUid;
    }

    public void replace(List<ChatMsgAtencion> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ChatMsgAtencion m = data.get(pos);

        h.tvBubble.setText(m.text != null ? m.text : "");

        long timeMs = 0L;
        if (m.createdAt != null) {
            timeMs = m.createdAt.toDate().getTime();
        }

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(timeMs));

        h.tvMeta.setText(time);

        // “Alineación” simple (izq/der) según sender
        boolean mine = myUid != null && myUid.equals(m.senderId);
        android.view.ViewGroup.MarginLayoutParams lp =
                (android.view.ViewGroup.MarginLayoutParams) h.bubbleContainer.getLayoutParams();

        if (lp instanceof android.widget.FrameLayout.LayoutParams) {
            android.widget.FrameLayout.LayoutParams flp = (android.widget.FrameLayout.LayoutParams) lp;
            flp.gravity = mine ? android.view.Gravity.END : android.view.Gravity.START;
            h.bubbleContainer.setLayoutParams(flp);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvBubble, tvMeta;
        android.view.View bubbleContainer;

        VH(@NonNull View itemView) {
            super(itemView);
            tvBubble = itemView.findViewById(R.id.tvBubble);
            tvMeta   = itemView.findViewById(R.id.tvMeta);
            bubbleContainer = itemView.findViewById(R.id.bubbleContainer);
        }

    }
}
