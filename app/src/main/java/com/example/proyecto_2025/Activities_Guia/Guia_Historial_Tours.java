package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaHistorialToursBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaSolictarNuevoTourBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaToursPendientesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Guia_Historial_Tours extends AppCompatActivity {

    private ActivityGuiaHistorialToursBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaHistorialToursBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.InfoTour1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.InfoTour2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.btnDescargarPDF.setOnClickListener(view ->
                descargarTour());

    }

    public void descargarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("PDF");
        dialogBuilder.setMessage("¿Está seguro de descargar la informacion en formato PDF?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}