package com.example.proyecto_2025.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Message;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final List<Message> mensajes;
    private final SimpleDateFormat timeFormat;

    public MessageAdapter(List<Message> mensajes) {
        this.mensajes = mensajes;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Message msg = mensajes.get(position);
        String hora = timeFormat.format(msg.getFecha());

        if (msg.getSender() == Message.Sender.GUIDE) {
            // Mensaje del guía (izquierda, fondo gris)
            h.layoutGuia.setVisibility(View.VISIBLE);
            h.layoutAdmin.setVisibility(View.GONE);
            h.tvMensajeGuia.setText(msg.getTexto());
            h.tvHoraGuia.setText(hora);
        } else {
            // Mensaje del admin (derecha, fondo azul)
            h.layoutAdmin.setVisibility(View.VISIBLE);
            h.layoutGuia.setVisibility(View.GONE);
            h.tvMensajeAdmin.setText(msg.getTexto());
            h.tvHoraAdmin.setText(hora);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutGuia, layoutAdmin;
        TextView tvMensajeGuia, tvHoraGuia;
        TextView tvMensajeAdmin, tvHoraAdmin;

        ViewHolder(View v) {
            super(v);
            layoutGuia = v.findViewById(R.id.layoutMensajeGuia);
            layoutAdmin = v.findViewById(R.id.layoutMensajeAdmin);
            tvMensajeGuia = v.findViewById(R.id.tvMensajeGuia);
            tvHoraGuia = v.findViewById(R.id.tvHoraGuia);
            tvMensajeAdmin = v.findViewById(R.id.tvMensajeAdmin);
            tvHoraAdmin = v.findViewById(R.id.tvHoraAdmin);
        }
    }
}