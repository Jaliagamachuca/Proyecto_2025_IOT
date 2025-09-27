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

import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Registrar_Administrador;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Administrador;
import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaToursPendientesBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminAdministradoresSedeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Guia_Tours_Pendientes extends AppCompatActivity {

    private ActivityGuiaToursPendientesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaToursPendientesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Tour_en_Proceso.class);
            startActivity(intent);
        });

        binding.btn2.setOnClickListener(view ->
                iniciarTour());


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
    }

    public void iniciarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Iniciar Tour");
        dialogBuilder.setMessage("¿Está seguro de iniciar este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}