package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.IrvEmployeeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Adaptador que muestra información de los usuarios (Admins, Guías y Clientes)
 */
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.UserViewHolder> {

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

        // 🔹 Mostrar nombre completo
        String fullName = user.getNombre() + " " + user.getApellidos();
        holder.binding.textViewFullName.setText(fullName);

        // 🔹 Cargar imagen del usuario
        if (user.getFotoUrl() != null && !user.getFotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getFotoUrl())
                    .placeholder(R.drawable.ic_person) // imagen temporal mientras carga
                    .error(R.drawable.ic_person)       // imagen por defecto si falla
                    .into(holder.binding.imgUser);
        } else {
            holder.binding.imgUser.setImageResource(R.drawable.ic_person);
        }

        // 🔹 Botón "Ver información"
        holder.binding.buttonInformacion.setOnClickListener(view -> {
            Intent intent;

            // Dependiendo del rol, abre diferentes pantallas
            switch (user.getRol()) {
                case "Administrador":
                    intent = new Intent(context, Superadmin_Ver_Administrador.class);
                    break;
                case "Guía":
                    intent = new Intent(context, Superadmin_Ver_Guia_Turismo.class);
                    break;
                case "Cliente":
                default:
                    intent = new Intent(context, Superadmin_Ver_Cliente.class);
                    break;
            }

            intent.putExtra("user", user);
            context.startActivity(intent);
        });

        // 🔹 Lógica del botón según si el usuario está activo o no
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

    // Getters y Setters
    public List<User> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaEmpleados(List<User> listaUsuarios) { // mantiene el mismo nombre del método
        this.listaUsuarios = listaUsuarios;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // 🔹 ViewHolder adaptado
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        IrvEmployeeBinding binding;
        User user;

        public UserViewHolder(IrvEmployeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // 🔹 Diálogo de activación
    private void mostrarDialogActivar(User usuario) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Activar usuario");
        dialogBuilder.setMessage("¿Está seguro de activar al usuario " + usuario.getNombre() + "?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) ->
                Log.d(TAG, "btn neutral")
        );
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                Log.d(TAG, "Usuario activado: " + usuario.getNombre())
        );
        dialogBuilder.show();
    }

    // 🔹 Diálogo de desactivación
    private void mostrarDialogDesactivar(User usuario) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Desactivar usuario");
        dialogBuilder.setMessage("¿Está seguro de desactivar al usuario " + usuario.getNombre() + "?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) ->
                Log.d(TAG, "btn neutral")
        );
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                Log.d(TAG, "Usuario desactivado: " + usuario.getNombre())
        );
        dialogBuilder.show();
    }
}
