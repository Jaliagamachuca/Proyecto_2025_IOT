package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerGuiaTurismoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Guia_Turismo extends AppCompatActivity {

    private ActivitySuperadminVerGuiaTurismoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerGuiaTurismoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🟢 Recibir objeto User desde el intent
        Intent intent = getIntent();
        User guia = (User) intent.getSerializableExtra("user");

        if (guia != null) {
            // 🟢 Mostrar la información del guía en los campos
            binding.inputNombre.setText(guia.getNombre());
            binding.inputApellidos.setText(guia.getApellidos());
            binding.inputDni.setText(guia.getDni());
            binding.inputCorreo.setText(guia.getCorreo());
            binding.inputTelefono.setText(guia.getTelefono());
            binding.inputDomicilio.setText(guia.getDomicilio());
            binding.inputFechaNacimiento.setText(guia.getFechaNacimiento());
            if (guia.getIdiomas() != null && !guia.getIdiomas().isEmpty()) {
                binding.inputIdiomas.setText(String.join(", ", guia.getIdiomas()));
            } else {
                binding.inputIdiomas.setText("—");
            }
        }

        // 🟢 Mostrar la imagen del guía
        if (guia.getFotoUrl() != null && !guia.getFotoUrl().isEmpty()) {
            try {
                Glide.with(this)
                        .load(guia.getFotoUrl())
                        .placeholder(R.drawable.ic_person) // mientras carga
                        .error(R.drawable.ic_person) // si falla la carga
                        .circleCrop() // hace la imagen redonda
                        .into(binding.imgGuia);
            } catch (Exception e) {
                Log.e("Superadmin_Ver_Guia", "Error al cargar imagen: " + e.getMessage());
            }
        } else {
            binding.imgGuia.setImageResource(R.drawable.ic_person);
        }

        // 🟢 Configurar botón de activación/desactivación
        if (guia != null) {
            if (guia.isActivo()) {
                binding.btnActivarGuia.setText("DESACTIVAR");
                binding.btnActivarGuia.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.holo_red_dark)
                );
                binding.btnActivarGuia.setOnClickListener(v -> mostrarDialogDesactivar(guia));
            } else {
                binding.btnActivarGuia.setText("ACTIVAR");
                binding.btnActivarGuia.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.holo_green_dark)
                );
                binding.btnActivarGuia.setOnClickListener(v -> mostrarDialogActivar(guia));
            }
        }
    }

    // 🟢 Diálogo para activar guía
    private void mostrarDialogActivar(User guia) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Activar Guía de Turismo")
                .setMessage("¿Está seguro de activar al guía " + guia.getNombre() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) ->
                        Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) ->
                        Log.d("msg-test", "Guía activado: " + guia.getNombre()))
                .show();
    }

    // 🟢 Diálogo para desactivar guía
    private void mostrarDialogDesactivar(User guia) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Desactivar Guía de Turismo")
                .setMessage("¿Está seguro de desactivar al guía " + guia.getNombre() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) ->
                        Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) ->
                        Log.d("msg-test", "Guía desactivado: " + guia.getNombre()))
                .show();
    }
}
