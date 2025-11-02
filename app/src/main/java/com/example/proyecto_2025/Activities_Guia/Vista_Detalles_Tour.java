package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityVistaDetallesTourBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Vista_Detalles_Tour extends AppCompatActivity {

    private ActivityVistaDetallesTourBinding binding;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVistaDetallesTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Recibir el objeto Tour
        tour = (Tour) getIntent().getSerializableExtra("tour_seleccionado");

        if (tour == null) {
            Toast.makeText(this, "Error: No se recibió información del tour", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Mostrar los datos
        binding.inputEmpresa.setText(tour.getNombreEmpresa());
        binding.inputNombreTour.setText(tour.getNombreTour());
        binding.inputDescripcion.setText(tour.getDescripcion());
        binding.inputFechaInicio.setText(tour.getFechaTour());
        binding.inputAdministrador.setText(tour.getNombreAdministrador());
        binding.inputTelefono.setText(tour.getTelefonoAdministrador());
        binding.inputEstado.setText(tour.getEstadoCompleto());

        // 🔹 Mostrar pago ofrecido
        String pagoTexto = String.format("S/ %.2f", tour.getPagoOfrecido());
        binding.inputPago.setText(pagoTexto);

        // 🔹 Mostrar imagen
        if (tour.getFotoUrl() != null && !tour.getFotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(tour.getFotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(binding.imgTour);
        } else {
            binding.imgTour.setImageResource(R.drawable.ic_person);
        }

        // 🔹 Configurar botón según subEstado
        configurarBoton();
    }

    private void configurarBoton() {
        if ("solicitado".equalsIgnoreCase(tour.getSubEstado())) {
            binding.btnSolicitarTour.setText("Rechazar");
            binding.btnSolicitarTour.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            binding.btnSolicitarTour.setOnClickListener(v -> {
                mostrarDialogRechazar();
            });
        } else {
            binding.btnSolicitarTour.setText("Solicitar");
            binding.btnSolicitarTour.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            binding.btnSolicitarTour.setOnClickListener(v -> {
                mostrarDialogSolicitar();
            });
        }
    }

    // 🔹 Diálogo para solicitar tour
    private void mostrarDialogSolicitar() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Solicitar Tour")
                .setMessage("¿Deseas solicitar el tour '" + tour.getNombreTour() + "'?")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    tour.setSubEstado("solicitado");
                    configurarBoton(); // actualiza el botón
                    Toast.makeText(this, "Has solicitado el tour: " + tour.getNombreTour(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // 🔹 Diálogo para rechazar solicitud
    private void mostrarDialogRechazar() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancelar Solicitud")
                .setMessage("¿Deseas cancelar tu solicitud del tour '" + tour.getNombreTour() + "'?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Sí", (dialog, which) -> {
                    tour.setSubEstado("no solicitado");
                    configurarBoton(); // actualiza el botón
                    Toast.makeText(this, "Has cancelado tu solicitud para: " + tour.getNombreTour(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
