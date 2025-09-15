package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;

public class Superadmin_Activity_VistaInicial extends BaseActivity {

    private ActivitySuperadminVistaInicialBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVistaInicialBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.button3.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Administradores_Sede.class);
            startActivity(intent);
        });

        binding.button4.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Guias_Turismo.class);
            startActivity(intent);
        });

        binding.button5.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Clientes1.class);
            startActivity(intent);
        });
    }
}
