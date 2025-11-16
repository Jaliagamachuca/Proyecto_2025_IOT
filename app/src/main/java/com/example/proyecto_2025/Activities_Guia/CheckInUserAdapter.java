package com.example.proyecto_2025.Activities_Guia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;

import java.util.List;

public class CheckInUserAdapter extends RecyclerView.Adapter<CheckInUserAdapter.ViewHolder> {

    private final List<String> usuarios;
    private final Context context;

    public CheckInUserAdapter(List<String> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_usuario_checkin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String usuario = usuarios.get(position);

        holder.textViewFullName.setText(usuario);

        // Imagen random para cada usuario
        Glide.with(context)
                .load("https://picsum.photos/seed/user" + position + "/200")
                .placeholder(R.drawable.ic_person)
                .into(holder.imgUser);

        holder.buttonRegistrarCheckin.setOnClickListener(v -> {
            Toast.makeText(context,
                    "Check-in registrado para " + usuario,
                    Toast.LENGTH_SHORT).show();

            // Cambiar el botón a "Registrado"
            holder.buttonRegistrarCheckin.setText("Registrado ✔");
            holder.buttonRegistrarCheckin.setEnabled(false);
            holder.buttonRegistrarCheckin.setBackgroundTintList(
                    context.getColorStateList(R.color.accent_color)
            );
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView textViewFullName;
        Button buttonRegistrarCheckin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            textViewFullName = itemView.findViewById(R.id.textViewFullName);
            buttonRegistrarCheckin = itemView.findViewById(R.id.buttonRegistrarCheckin);
        }
    }
}
