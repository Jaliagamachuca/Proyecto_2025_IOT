package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerClientesBinding;
import com.example.proyecto_2025.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Cliente extends AppCompatActivity {

    private ActivitySuperadminVerClientesBinding binding;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerClientesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Recuperar el usuario enviado
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            // 🔹 Mostrar los datos del usuario
            binding.inputNombre.setText(user.getNombre());
            binding.inputApellidos.setText(user.getApellidos());
            binding.inputDni.setText(user.getDni());
            binding.inputCorreo.setText(user.getCorreo());
            binding.inputTelefono.setText(user.getTelefono());
            binding.inputDomicilio.setText(user.getDomicilio());
            binding.inputFechaNacimiento.setText(user.getFechaNacimiento());

            // 🟢 Mostrar la imagen del guía
            if (user.getFotoUrl() != null && !user.getFotoUrl().isEmpty()) {
                try {
                    Glide.with(this)
                            .load(user.getFotoUrl())
                            .placeholder(R.drawable.ic_person) // mientras carga
                            .error(R.drawable.ic_person) // si falla la carga
                            .circleCrop() // hace la imagen redonda
                            .into(binding.imgCliente);
                } catch (Exception e) {
                    Log.e("Superadmin_Ver_Guia", "Error al cargar imagen: " + e.getMessage());
                }
            } else {
                binding.imgCliente.setImageResource(R.drawable.ic_person);
            }

            // 🔹 Mostrar el estado actual
            actualizarBotonEstado();
        } else {
            Log.e("Superadmin_Ver_Cliente", "El objeto User es null — revisa el putExtra()");
        }
    }

    /** 🔹 Cambia el texto y color del botón según si el usuario está activo o no */
    private void actualizarBotonEstado() {
        if (user.isActivo()) {
            binding.btnActivarCliente.setText("DESACTIVAR");
            binding.btnActivarCliente.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            binding.btnActivarCliente.setOnClickListener(v -> mostrarDialogDesactivar(user));
        } else {
            binding.btnActivarCliente.setText("ACTIVAR");
            binding.btnActivarCliente.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            binding.btnActivarCliente.setOnClickListener(v -> mostrarDialogActivar(user));
        }
    }

    /** 🔹 Dialogo para activar */
    private void mostrarDialogActivar(User user) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Activar Cliente")
                .setMessage("¿Está seguro de activar al usuario " + user.getNombre() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) -> Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) -> {
                    user.setActivo(true);
                    actualizarBotonEstado();
                    Log.d("msg-test", "Usuario activado: " + user.getNombre());
                })
                .show();
    }

    /** 🔹 Dialogo para desactivar */
    private void mostrarDialogDesactivar(User user) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Desactivar Cliente")
                .setMessage("¿Está seguro de desactivar al usuario " + user.getNombre() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) -> Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) -> {
                    user.setActivo(false);
                    actualizarBotonEstado();
                    Log.d("msg-test", "Usuario desactivado: " + user.getNombre());
                })
                .show();
    }
}
