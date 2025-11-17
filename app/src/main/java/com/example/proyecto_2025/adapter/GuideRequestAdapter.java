package com.example.proyecto_2025.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.User;

import java.util.ArrayList;
import java.util.List;

public class GuideRequestAdapter extends RecyclerView.Adapter<GuideRequestAdapter.VH> {

    public interface OnGuideClickListener {
        void onClick(User guia);
    }

    private final Context context;
    private final OnGuideClickListener listener;
    private final List<User> data = new ArrayList<>();

    public GuideRequestAdapter(Context context, OnGuideClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<User> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_solicitud_guia, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        User u = data.get(position);

        String nombre = u.getNombreCompleto() != null
                ? u.getNombreCompleto()
                : "(Sin nombre)";

        String correo = u.getEmail() != null ? u.getEmail() : "";
        String dni = u.getDni() != null ? u.getDni() : "";

        h.tvNombre.setText(nombre);
        h.tvCorreo.setText(correo);
        h.tvDni.setText(dni.isEmpty() ? "DNI no registrado" : "DNI: " + dni);

        h.tvEstado.setText("Pendiente de revisión");

        // Foto
        if (u.getPhotoUrl() != null && !u.getPhotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(u.getPhotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(h.imgAvatar);
        } else {
            h.imgAvatar.setImageResource(R.drawable.ic_person);
        }

        h.btnRevisar.setOnClickListener(v -> {
            if (listener != null) listener.onClick(u);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgAvatar;
        TextView tvNombre, tvCorreo, tvDni, tvEstado;
        Button btnRevisar;

        VH(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatarGuia);
            tvNombre = itemView.findViewById(R.id.tvNombreGuia);
            tvCorreo = itemView.findViewById(R.id.tvCorreoGuia);
            tvDni = itemView.findViewById(R.id.tvDniGuia);
            tvEstado = itemView.findViewById(R.id.tvEstadoSolicitud);
            btnRevisar = itemView.findViewById(R.id.btnRevisarGuia);
        }
    }
}
