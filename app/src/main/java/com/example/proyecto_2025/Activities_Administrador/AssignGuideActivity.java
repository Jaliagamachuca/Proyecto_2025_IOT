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

                    String guia = (tour.guiaId != null) ? tour.guiaId : "(sin guía)";
                    String estado = doc.getString("estado");
                    if (estado == null) estado = "-";

                    tv.setText(
                            "Tour: " + (tour.titulo != null ? tour.titulo : tour.id) +
                                    "\nEstado: " + estado +
                                    "\nGuía (uid): " + guia +
                                    "\nPago guía: S/ " + String.format("%.2f", tour.propuestaPagoGuia)
                    );
                });
    }

    private void aceptarGuia() {
        if (tour == null) return;

        Map<String, Object> updates = new HashMap<>();
        // Mantener estado SOLICITADO (como tú quieres)
        updates.put("estado", "SOLICITADO");

        // Marcar que ya fue aceptado por admin
        updates.put("solicitudEstado", "ACEPTADA"); // NUEVO CAMPO

        // opcional: timestamp
        updates.put("solicitudAceptadaUtc", System.currentTimeMillis());

        db.collection("tours").document(tourId)
                .update(updates)
                .addOnSuccessListener(x -> {
                    Toast.makeText(this, "Solicitud aceptada. Listo para publicar a clientes.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show()
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
                    finish();
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
