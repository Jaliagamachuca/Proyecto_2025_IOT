package com.example.proyecto_2025.Activities_Superadmin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarGuiaTurismoBinding;
import com.example.proyecto_2025.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Superadmin_Registrar_Guia_Turismo extends AppCompatActivity {

    private ActivitySuperadminRegistrarGuiaTurismoBinding binding;
    private User guia;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRegistrarGuiaTurismoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        guia = (User) getIntent().getSerializableExtra("user");
        if (guia == null) {
            Toast.makeText(this, "No se pudo cargar la solicitud.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Cargar datos en UI
        cargarDatosEnFormulario();

        // Botones
        binding.btnAprobarGuia.setOnClickListener(v -> confirmarCambioEstado("active"));
        binding.btnRechazarGuia.setOnClickListener(v -> confirmarCambioEstado("rejected"));
    }

    private void cargarDatosEnFormulario() {
        // displayName = nombre mostrado en la app
        String nombreMostrar = guia.getDisplayName() != null
                ? guia.getDisplayName()
                : guia.getNombreCompleto(); // por si mantuviste el helper

        binding.tvTituloGuia.setText("Revisar solicitud de " + nombreMostrar);

        if (guia.getPhotoUrl() != null && !guia.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(guia.getPhotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.imgGuia);
        } else {
            binding.imgGuia.setImageResource(R.drawable.ic_person);
        }

        binding.inputNombre.setText(nombreMostrar);
        binding.inputDni.setText(guia.getDni());
        binding.inputCorreo.setText(guia.getEmail());
        binding.inputTelefono.setText(guia.getPhone());
        binding.inputDomicilio.setText(guia.getDomicilio());

        // Idiomas
        if (guia.getIdiomas() != null && !guia.getIdiomas().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < guia.getIdiomas().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(guia.getIdiomas().get(i));
            }
            binding.inputIdiomas.setText(sb.toString());
        }

        // Zonas de operación
        if (guia.getZonaOperacion() != null && !guia.getZonaOperacion().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < guia.getZonaOperacion().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(guia.getZonaOperacion().get(i));
            }
            // ⚠️ ID correcto según el XML
            binding.inputZonasOperacion.setText(sb.toString());
        }
    }

    private void confirmarCambioEstado(String nuevoEstado) {
        String titulo = "Aprobar Guía";
        String msg = "¿Está seguro de aprobar a este guía?";
        if ("rejected".equals(nuevoEstado)) {
            titulo = "Rechazar Guía";
            msg = "¿Está seguro de rechazar esta solicitud?";
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setMessage(msg)
                .setNeutralButton(R.string.cancel, (d, i) -> Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (d, i) -> guardarCambios(nuevoEstado))
                .show();
    }

    private void guardarCambios(String nuevoEstado) {
        if (guia.getUid() == null || guia.getUid().isEmpty()) {
            Toast.makeText(this, "UID de guía inválido.", Toast.LENGTH_LONG).show();
            return;
        }

        // Recoger campos editados
        String nombre = binding.inputNombre.getText() != null
                ? binding.inputNombre.getText().toString().trim() : "";
        String dni = binding.inputDni.getText() != null
                ? binding.inputDni.getText().toString().trim() : "";
        String correo = binding.inputCorreo.getText() != null
                ? binding.inputCorreo.getText().toString().trim() : "";
        String telefono = binding.inputTelefono.getText() != null
                ? binding.inputTelefono.getText().toString().trim() : "";
        String domicilio = binding.inputDomicilio.getText() != null
                ? binding.inputDomicilio.getText().toString().trim() : "";
        String idiomasStr = binding.inputIdiomas.getText() != null
                ? binding.inputIdiomas.getText().toString().trim() : "";
        String zonasStr = binding.inputZonasOperacion.getText() != null
                ? binding.inputZonasOperacion.getText().toString().trim() : "";

        List<String> idiomas = new ArrayList<>();
        if (!idiomasStr.isEmpty()) {
            String[] parts = idiomasStr.split(",");
            for (String p : parts) {
                String s = p.trim();
                if (!s.isEmpty()) idiomas.add(s);
            }
        }

        List<String> zonas = new ArrayList<>();
        if (!zonasStr.isEmpty()) {
            String[] parts = zonasStr.split(",");
            for (String p : parts) {
                String s = p.trim();
                if (!s.isEmpty()) zonas.add(s);
            }
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", nombre);
        updates.put("dni", dni);
        updates.put("email", correo);
        updates.put("phone", telefono);
        updates.put("domicilio", domicilio);
        updates.put("idiomas", idiomas);
        updates.put("zonaOperacion", zonas);
        updates.put("status", nuevoEstado);
        updates.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(guia.getUid())
                .update(updates)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this,
                            "Solicitud actualizada correctamente.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
