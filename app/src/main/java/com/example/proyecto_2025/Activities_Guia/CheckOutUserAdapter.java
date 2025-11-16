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

public class CheckOutUserAdapter extends RecyclerView.Adapter<CheckOutUserAdapter.ViewHolder> {

    private final List<String> usuarios;
    private final Context context;

    public CheckOutUserAdapter(List<String> usuarios, Context context) {
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

        // Foto generada automáticamente
        Glide.with(context)
                .load("https://picsum.photos/seed/user_checkout" + position + "/200")
                .placeholder(R.drawable.ic_person)
                .into(holder.imgUser);

        // BOTÓN DE CHECK-OUT
        holder.buttonRegistrarCheckin.setText("Registrar Check-out");

        holder.buttonRegistrarCheckin.setOnClickListener(v -> {
            Toast.makeText(context,
                    "Check-out registrado para " + usuario,
                    Toast.LENGTH_SHORT).show();

            holder.buttonRegistrarCheckin.setText("Listo ✔");
            holder.buttonRegistrarCheckin.setEnabled(false);
            holder.buttonRegistrarCheckin.setBackgroundTintList(
                    context.getColorStateList(R.color.error_color)
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
