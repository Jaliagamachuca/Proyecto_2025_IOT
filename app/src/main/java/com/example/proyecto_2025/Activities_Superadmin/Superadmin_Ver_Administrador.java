package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerAdministradorBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Administrador extends AppCompatActivity {

    private ActivitySuperadminVerAdministradorBinding binding;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Recuperar el usuario enviado
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            // 🔹 Mostrar los datos del usuario
            binding.inputNombre.setText(user.getNombre());
            binding.inputApellidos.setText(user.getApellidos());
            binding.inputDni.setText(user.getDni());
            binding.inputFechaNacimiento.setText(user.getFechaNacimiento());
            binding.inputCorreo.setText(user.getCorreo());
            binding.inputTelefono.setText(user.getTelefono());
            binding.inputDomicilio.setText(user.getDomicilio());

            // 🔹 Mostrar el estado actual
            actualizarBotonEstado();
        }
    }

    /** 🔹 Cambia el texto y color del botón según si el usuario está activo o no */
    private void actualizarBotonEstado() {
        if (user.isActivo()) {
            binding.btnActivarAdministrador.setText("DESACTIVAR");
            binding.btnActivarAdministrador.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            binding.btnActivarAdministrador.setOnClickListener(v -> mostrarDialogDesactivar(user));
        } else {
            binding.btnActivarAdministrador.setText("ACTIVAR");
            binding.btnActivarAdministrador.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            binding.btnActivarAdministrador.setOnClickListener(v -> mostrarDialogActivar(user));
        }
    }

    /** 🔹 Dialogo para activar */
    private void mostrarDialogActivar(User user) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Activar Administrador")
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
                .setTitle("Desactivar Administrador")
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
