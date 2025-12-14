package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityVistaDetallesTourBinding;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Vista_Detalles_Tour extends AppCompatActivity {

    private ActivityVistaDetallesTourBinding binding;
    private FirebaseFirestore db;
    private String tourId;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVistaDetallesTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        tourId = getIntent().getStringExtra("id");
        if (tourId == null || tourId.isEmpty()) {
            Toast.makeText(this, "Tour sin ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        escucharTour();
    }

    private void escucharTour() {
        db.collection("tours").document(tourId)
                .addSnapshotListener((doc, e) -> {
                    if (e != null || doc == null || !doc.exists()) return;

                    tour = doc.toObject(Tour.class);
                    if (tour == null) return;

                    tour.id = doc.getId(); // importante

                    pintarDatos();
                    if (tour.empresaId != null) {
                        cargarEmpresa(tour.empresaId);
                    }

                    configurarBotonSegunEstado();
                });
    }

    private void pintarDatos() {
        binding.inputEmpresa.setText(tour.empresaId != null ? tour.empresaId : "—");
        binding.inputNombreTour.setText(tour.titulo != null ? tour.titulo : "—");
        binding.inputDescripcion.setText(tour.descripcionCorta != null ? tour.descripcionCorta : "—");

        binding.inputEstado.setText(tour.estado != null ? tour.estado.name() : "SIN_ESTADO");
        binding.inputPago.setText("S/ " + String.format("%.2f", tour.propuestaPagoGuia));

        String img = (tour.imagenUris != null && !tour.imagenUris.isEmpty()) ? tour.imagenUris.get(0) : null;
        if (img != null && !img.isEmpty()) {
            Glide.with(this).load(img).placeholder(R.drawable.ic_person).into(binding.imgTour);
        } else {
            binding.imgTour.setImageResource(R.drawable.ic_person);
        }
    }

    private void configurarBotonSegunEstado() {
        String uid = (FirebaseAuth.getInstance().getCurrentUser() != null)
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) {
            binding.btnSolicitarTour.setEnabled(false);
            binding.btnSolicitarTour.setText("Sin sesión");
            return;
        }

        // Caso 1: Bolsa de guías (nadie la pidió aún)
        if (tour.estado == TourEstado.PENDIENTE_GUIA && (tour.guiaId == null || tour.guiaId.isEmpty())) {
            binding.btnSolicitarTour.setEnabled(true);
            binding.btnSolicitarTour.setText("Solicitar");
            binding.btnSolicitarTour.setOnClickListener(v -> confirmarSolicitar(uid));
            return;
        }

        // Caso 2: yo ya lo solicité (estado SOLICITADO y guiaId == mi uid) -> permitir cancelar
        if (tour.estado == TourEstado.SOLICITADO && uid.equals(tour.guiaId)) {
            binding.btnSolicitarTour.setEnabled(true);
            binding.btnSolicitarTour.setText("Cancelar solicitud");
            binding.btnSolicitarTour.setOnClickListener(v -> confirmarCancelar(uid));
            return;
        }

        // Otros casos: no disponible
        binding.btnSolicitarTour.setEnabled(false);
        binding.btnSolicitarTour.setText("No disponible");
        binding.btnSolicitarTour.setOnClickListener(null);
    }

    private void confirmarSolicitar(String uid) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Solicitar Tour")
                .setMessage("¿Deseas solicitar este tour?")
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .setPositiveButton("Aceptar", (d, w) -> solicitar(uid))
                .show();
    }

    private void solicitar(String uid) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", TourEstado.SOLICITADO.name());
        updates.put("guiaId", uid);

        db.collection("tours").document(tourId)
                .update(updates)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Solicitud enviada al admin", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void confirmarCancelar(String uid) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancelar solicitud")
                .setMessage("¿Deseas cancelar tu solicitud?")
                .setNegativeButton("No", (d, w) -> d.dismiss())
                .setPositiveButton("Sí", (d, w) -> cancelar(uid))
                .show();
    }

    private void cancelar(String uid) {
        // Regresa a bolsa SOLO si sigue siendo mío y sigue SOLICITADO
        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", TourEstado.PENDIENTE_GUIA.name());
        updates.put("guiaId", null);

        db.collection("tours").document(tourId)
                .update(updates)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Solicitud cancelada", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void cargarAdmin(String adminId) {
        if (adminId == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(adminId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombre = doc.getString("displayName");
                        binding.inputAdministrador.setText(nombre != null ? nombre : "—");
                    }
                });
    }


    private void cargarEmpresa(String empresaId) {
        FirebaseFirestore.getInstance()
                .collection("empresas")
                .document(empresaId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombre = doc.getString("nombre");
                        String telefono = doc.getString("telefono");
                        String adminId = doc.getString("adminId");

                        binding.inputEmpresa.setText(nombre != null ? nombre : "—");
                        binding.inputTelefono.setText(telefono != null ? telefono : "—");

                        cargarAdmin(adminId);
                    }
                });
    }

}
