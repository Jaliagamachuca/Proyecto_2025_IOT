package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AssignGuideActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String tourId;
    private Tour tour;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.assign_guide);

        db = FirebaseFirestore.getInstance();
        tourId = getIntent().getStringExtra("tourId");
        if (tourId == null || tourId.isEmpty()) {
            Toast.makeText(this, "tourId faltante", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        escucharTour();

        findViewById(R.id.btnConfirm).setOnClickListener(v -> aceptarGuia());

        // Si en tu layout existe btnReject, habilítalo:
        findViewById(R.id.btnReject).setOnClickListener(v -> rechazarSolicitud());
    }

    private void escucharTour() {
        db.collection("tours").document(tourId)
                .addSnapshotListener((doc, e) -> {
                    if (e != null || doc == null || !doc.exists()) return;

                    tour = doc.toObject(Tour.class);
                    if (tour == null) return;
                    tour.id = doc.getId();

                    TextView tv = findViewById(R.id.tvSummary);

                    String estado = (tour.estado != null) ? tour.estado.name() : "-";

                    if (estado == null) estado = "-";

                    String base =
                            "Tour: " + (tour.titulo != null ? tour.titulo : tour.id) +
                                    "\nEstado: " + estado +
                                    "\nPago guía: S/ " + String.format("%.2f", tour.propuestaPagoGuia);

                    String guiaId = (tour.guiaId != null) ? tour.guiaId.trim() : "";

                    if (guiaId.isEmpty()) {
                        tv.setText(base + "\nGuía: (pendiente)");
                        return;
                    }

                    db.collection("users").document(guiaId).get()
                            .addOnSuccessListener(uDoc -> {
                                String nombre = uDoc.getString("displayName");
                                String guiaNombre = (nombre != null && !nombre.trim().isEmpty())
                                        ? nombre
                                        : "(pendiente)";
                                tv.setText(base + "\nGuía: " + guiaNombre);
                            })
                            .addOnFailureListener(err -> tv.setText(base + "\nGuía: (pendiente)"));
                });
    }


    private void aceptarGuia() {
        if (tour == null) return;

        if (tour.guiaId == null || tour.guiaId.isEmpty()) {
            Toast.makeText(this, "No hay guía asignado.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("solicitudEstado", "ACEPTADA");
        updates.put("solicitudAceptadaUtc", System.currentTimeMillis());
        // ❌ NO CAMBIAR estado aquí

        db.collection("tours").document(tourId)
                .update(updates)
                .addOnSuccessListener(x -> {
                    Toast.makeText(this, "Guía aceptado.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show()
                );
    }



    private void rechazarSolicitud() {
        if (tour == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", "PENDIENTE_GUIA");
        updates.put("guiaId", null);
        updates.put("solicitudEstado", "");

        db.collection("tours").document(tourId)
                .update(updates)
                .addOnSuccessListener(x -> {
                    Toast.makeText(this, "Solicitud rechazada (vuelve a bolsa).", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
