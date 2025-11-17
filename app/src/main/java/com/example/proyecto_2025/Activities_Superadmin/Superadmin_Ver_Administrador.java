package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerAdministradorBinding;
import com.example.proyecto_2025.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

public class Superadmin_Ver_Administrador extends AppCompatActivity {

    private ActivitySuperadminVerAdministradorBinding binding;
    private User user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Superadmin_Ver_Admin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            // Nombre completo (displayName)
            binding.inputNombre.setText(user.getNombreCompleto());
            // En el esquema actual no hay apellidos separados → lo dejamos vacío
            binding.inputApellidos.setText("");
            binding.inputDni.setText(user.getDni());
            binding.inputFechaNacimiento.setText("-"); // no está en el esquema
            binding.inputCorreo.setText(user.getEmail());
            binding.inputTelefono.setText(user.getPhone());
            binding.inputDomicilio.setText("-");        // no está en el esquema

            // Foto
            if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                try {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(binding.imgAdministrador);
                } catch (Exception e) {
                    Log.e(TAG, "Error al cargar imagen: " + e.getMessage());
                    binding.imgAdministrador.setImageResource(R.drawable.ic_person);
                }
            } else {
                binding.imgAdministrador.setImageResource(R.drawable.ic_person);
            }

            actualizarBotonEstado();
        } else {
            Log.e(TAG, "User es null, revisa el putExtra(\"user\")");
        }
    }

    /** Cambia texto y color del botón según status */
    private void actualizarBotonEstado() {
        if (user.isActivo()) {
            binding.btnActivarAdministrador.setText("DESACTIVAR");
            binding.btnActivarAdministrador.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            binding.btnActivarAdministrador.setOnClickListener(v -> mostrarDialogCambiarEstado(false));
        } else {
            binding.btnActivarAdministrador.setText("ACTIVAR");
            binding.btnActivarAdministrador.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            binding.btnActivarAdministrador.setOnClickListener(v -> mostrarDialogCambiarEstado(true));
        }
    }

    private void mostrarDialogCambiarEstado(boolean activar) {
        String titulo = activar ? "Activar Administrador" : "Desactivar Administrador";
        String accion = activar ? "activar" : "desactivar";

        new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setMessage("¿Está seguro de " + accion + " al usuario " + user.getNombreCompleto() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) -> Log.d(TAG, "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) -> actualizarEstadoEnFirestore(activar))
                .show();
    }

    /** Actualiza el campo status en Firestore: active / inactive */
    private void actualizarEstadoEnFirestore(boolean activar) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            Toast.makeText(this, "UID inválido, no se puede actualizar.", Toast.LENGTH_LONG).show();
            return;
        }

        String nuevoStatus = activar ? "active" : "inactive";

        db.collection("users").document(user.getUid())
                .update("status", nuevoStatus)
                .addOnSuccessListener(aVoid -> {
                    user.setStatus(nuevoStatus);
                    actualizarBotonEstado();
                    Toast.makeText(this,
                            activar ? "Administrador activado" : "Administrador desactivado",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error actualizando estado", e);
                    Toast.makeText(this, "Error actualizando estado", Toast.LENGTH_LONG).show();
                });
    }
}
