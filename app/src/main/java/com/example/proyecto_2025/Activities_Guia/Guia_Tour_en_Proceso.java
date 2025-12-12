package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_HomeActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaTourEnProcesoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Guia_Tour_en_Proceso extends AppCompatActivity {

    private ActivityGuiaTourEnProcesoBinding binding;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaTourEnProcesoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Recibir objeto Tour
        tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            Toast.makeText(this, "No se recibió información del tour", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Mostrar datos
        binding.txtNombreTour.setText(tour.getNombreTour());
        binding.txtEmpresa.setText("Empresa: " + tour.getNombreEmpresa());
        binding.txtFecha.setText("Fecha: " + tour.getFechaTour());
        binding.txtDescripcion.setText(tour.getDescripcion());

        // 🔹 Cargar imagen principal del tour
        Glide.with(this)
                .load(tour.getFotoUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgTourPrincipal);

        // 🔹 Cargar imágenes bonitas de internet para los botones
        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/190/190411.png") // Check-in
                .into(binding.imgCheckIn);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/1055/1055646.png") // Progreso
                .into(binding.imgProgreso);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/1828/1828490.png") // Check-out
                .into(binding.imgCheckOut);

        // 🔹 Botones
        binding.btnCheckIn.setOnClickListener(v -> {
            Intent i = new Intent(this, Guia_Registrar_CheckIn.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });

        binding.btnProgreso.setOnClickListener(v -> {
            Intent i = new Intent(this, Guia_Registrar_Progreso.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });

        binding.btnCheckOut.setOnClickListener(v -> {
            Intent i = new Intent(this, Guia_Registrar_CheckOut.class);
            i.putExtra("tour", tour);
            startActivity(i);
        });

        // 🔹 Finalizar tour
        binding.btnFinaliarTour.setOnClickListener(view -> finalizarTour());
    }

    private void finalizarTour() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Finalizar Tour")
                .setMessage("¿Estás seguro de finalizar este tour?")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Aceptar", (dialog, which) ->
                        Toast.makeText(this, "Tour finalizado correctamente", Toast.LENGTH_SHORT).show())
                .show();
    }
}
