package com.example.proyecto_2025.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.ConversationUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.VH> {

    public interface OnClick {
        void onOpen(ConversationUI c);
    }

    private final Context ctx;
    private final OnClick onClick;

    private final List<ConversationUI> original = new ArrayList<>();
    private final List<ConversationUI> filtered = new ArrayList<>();

    private String query = "";
    private String roleFilter = "ALL"; // ALL, guia, cliente
    private boolean unreadOnly = false;

    public ConversationAdapter(Context ctx, OnClick onClick) {
        this.ctx = ctx;
        this.onClick = onClick;
    }

    public void submit(List<ConversationUI> data) {
        original.clear();
        if (data != null) original.addAll(data);
        applyFilters();
    }

    public void setQuery(String q) {
        query = (q == null) ? "" : q.trim();
        applyFilters();
    }

    public void setRoleFilter(String role) {
        roleFilter = (role == null) ? "ALL" : role;
        applyFilters();
    }

    public void setUnreadOnly(boolean v) {
        unreadOnly = v;
        applyFilters();
    }

    private void applyFilters() {
        filtered.clear();

        String q = query.toLowerCase(Locale.ROOT);

        for (ConversationUI c : original) {
            if (c == null) continue;

            if (unreadOnly && !c.unread) continue;

            if (!"ALL".equals(roleFilter)) {
                if (c.otherRole == null) continue;
                if (!roleFilter.equalsIgnoreCase(c.otherRole)) continue;
            }

            if (!q.isEmpty()) {
                String t = (c.title == null) ? "" : c.title.toLowerCase(Locale.ROOT);
                String lm = (c.lastMessage == null) ? "" : c.lastMessage.toLowerCase(Locale.ROOT);
                if (!t.contains(q) && !lm.contains(q)) continue;
            }

            filtered.add(c);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ConversationUI c = filtered.get(position);

        h.tvTitle.setText(c.title != null ? c.title : "Conversación");
        h.tvLast.setText(c.lastMessage != null ? c.lastMessage : "Sin mensajes");
        h.tvTime.setText(c.time != null ? c.time : "");
        h.unreadDot.setVisibility(c.unread ? View.VISIBLE : View.GONE);

        if (c.photoUrl != null && !c.photoUrl.isEmpty()) {
            Glide.with(ctx)
                    .load(c.photoUrl)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_placeholder)
                    .into(h.imgAvatar);
        } else {
            h.imgAvatar.setImageResource(R.drawable.ic_user_placeholder);
        }

        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onOpen(c);
        });
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvTitle, tvLast, tvTime;
        View unreadDot;

        VH(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLast = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            unreadDot = itemView.findViewById(R.id.unreadDot);
        }
    }
}
