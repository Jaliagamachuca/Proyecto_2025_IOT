package com.example.proyecto_2025.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Administrador;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Guia_Turismo;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.IrvEmployeeBinding;
import com.example.proyecto_2025.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> listaUsuarios;
    private Context context;

    private static final String TAG = "msg-test-UserAdapter";

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        IrvEmployeeBinding binding = IrvEmployeeBinding.inflate(inflater, parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = listaUsuarios.get(position);
        holder.user = user;

        // Nombre completo (displayName)
        holder.binding.textViewFullName.setText(user.getNombreCompleto());

        // Imagen
        String foto = user.getPhotoUrl();
        if (foto != null && !foto.isEmpty()) {
            Glide.with(context)
                    .load(foto)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.binding.imgUser);
        } else {
            holder.binding.imgUser.setImageResource(R.drawable.ic_person);
        }

        // Botón "Ver información"
        holder.binding.buttonInformacion.setOnClickListener(view -> {
            Intent intent;

            String rol = user.getRole() != null ? user.getRole().toLowerCase() : "";

            switch (rol) {
                case "admin":
                    intent = new Intent(context, Superadmin_Ver_Administrador.class);
                    break;
                case "guia":
                    intent = new Intent(context, Superadmin_Ver_Guia_Turismo.class);
                    break;
                case "cliente":
                default:
                    intent = new Intent(context, Superadmin_Ver_Cliente.class);
                    break;
            }

            intent.putExtra("user", user);
            context.startActivity(intent);
        });

        // Botón Activar / Desactivar según status
        if (user.isActivo()) {
            holder.binding.buttonActivar.setText("DESACTIVAR");
            holder.binding.buttonActivar.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            holder.binding.buttonActivar.setOnClickListener(v -> mostrarDialogDesactivar(user));
        } else {
            holder.binding.buttonActivar.setText("ACTIVAR");
            holder.binding.buttonActivar.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            holder.binding.buttonActivar.setOnClickListener(v -> mostrarDialogActivar(user));
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    public void setListaEmpleados(List<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        IrvEmployeeBinding binding;
        User user;

        public UserViewHolder(IrvEmployeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void mostrarDialogActivar(User usuario) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Activar usuario")
                .setMessage("¿Está seguro de activar a " + usuario.getNombreCompleto() + "?")
                .setNeutralButton(R.string.cancel, (d, i) -> Log.d(TAG, "cancelar activar"))
                .setPositiveButton(R.string.ok, (d, i) -> {
                    Log.d(TAG, "Usuario activado: " + usuario.getNombreCompleto());
                    // Aquí luego actualizaremos status en Firestore
                })
                .show();
    }

    private void mostrarDialogDesactivar(User usuario) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Desactivar usuario")
                .setMessage("¿Está seguro de desactivar a " + usuario.getNombreCompleto() + "?")
                .setNeutralButton(R.string.cancel, (d, i) -> Log.d(TAG, "cancelar desactivar"))
                .setPositiveButton(R.string.ok, (d, i) -> {
                    Log.d(TAG, "Usuario desactivado: " + usuario.getNombreCompleto());
                    // Aquí luego actualizaremos status en Firestore
                })
                .show();
    }
}
