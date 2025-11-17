package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerGuiaTurismoBinding;
import com.example.proyecto_2025.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Superadmin_Ver_Guia_Turismo extends AppCompatActivity {

    private ActivitySuperadminVerGuiaTurismoBinding binding;
    private User guia;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Superadmin_Ver_Guia";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerGuiaTurismoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        guia = (User) intent.getSerializableExtra("user");

        if (guia != null) {
            binding.inputNombre.setText(guia.getNombreCompleto());
            binding.inputApellidos.setText("");
            binding.inputDni.setText(guia.getDni());
            binding.inputCorreo.setText(guia.getEmail());
            binding.inputTelefono.setText(guia.getPhone());
            binding.inputDomicilio.setText("-");
            binding.inputFechaNacimiento.setText("-");

            List<String> idiomas = guia.getIdiomas();
            if (idiomas != null && !idiomas.isEmpty()) {
                binding.inputIdiomas.setText(android.text.TextUtils.join(", ", idiomas));
            } else {
                binding.inputIdiomas.setText("—");
            }

            if (guia.getPhotoUrl() != null && !guia.getPhotoUrl().isEmpty()) {
                try {
                    Glide.with(this)
                            .load(guia.getPhotoUrl())
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(binding.imgGuia);
                } catch (Exception e) {
                    Log.e(TAG, "Error al cargar imagen: " + e.getMessage());
                    binding.imgGuia.setImageResource(R.drawable.ic_person);
                }
            } else {
                binding.imgGuia.setImageResource(R.drawable.ic_person);
            }

            actualizarBotonEstado();
        } else {
            Log.e(TAG, "Guía es null — revisa el putExtra(\"user\")");
        }
    }

    private void actualizarBotonEstado() {
        if (guia.isActivo()) {
            binding.btnActivarGuia.setText("DESACTIVAR");
            binding.btnActivarGuia.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            binding.btnActivarGuia.setOnClickListener(v -> mostrarDialogCambiarEstado(false));
        } else {
            binding.btnActivarGuia.setText("ACTIVAR");
            binding.btnActivarGuia.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            binding.btnActivarGuia.setOnClickListener(v -> mostrarDialogCambiarEstado(true));
        }
    }

    private void mostrarDialogCambiarEstado(boolean activar) {
        String titulo = activar ? "Activar Guía de Turismo" : "Desactivar Guía de Turismo";
        String accion = activar ? "activar" : "desactivar";

        new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setMessage("¿Está seguro de " + accion + " al guía " + guia.getNombreCompleto() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) -> Log.d(TAG, "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) -> actualizarEstadoEnFirestore(activar))
                .show();
    }

    private void actualizarEstadoEnFirestore(boolean activar) {
        if (guia.getUid() == null || guia.getUid().isEmpty()) {
            Toast.makeText(this, "UID inválido, no se puede actualizar.", Toast.LENGTH_LONG).show();
            return;
        }

        String nuevoStatus = activar ? "active" : "inactive";

        db.collection("users").document(guia.getUid())
                .update("status", nuevoStatus)
                .addOnSuccessListener(aVoid -> {
                    guia.setStatus(nuevoStatus);
                    actualizarBotonEstado();
                    Toast.makeText(this,
                            activar ? "Guía activado" : "Guía desactivado",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error actualizando estado", e);
                    Toast.makeText(this, "Error actualizando estado", Toast.LENGTH_LONG).show();
                });
    }
}
