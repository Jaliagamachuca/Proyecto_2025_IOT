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
import com.example.proyecto_2025.databinding.ActivityGuiaTourEnProcesoBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Guia_Tour_en_Proceso extends AppCompatActivity {

    private ActivityGuiaTourEnProcesoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaTourEnProcesoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Registrar_CheckIn.class);
            startActivity(intent);
        });

        binding.button3.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Registrar_Progreso.class);
            startActivity(intent);
        });

        binding.button4.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Registrar_CheckOut.class);
            startActivity(intent);
        });

        binding.btnFinalizarTour.setOnClickListener(view ->
                FinalizarTour());

    }

    public void FinalizarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Finalizar Tour");
        dialogBuilder.setMessage("¿Está seguro de finalizar este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}