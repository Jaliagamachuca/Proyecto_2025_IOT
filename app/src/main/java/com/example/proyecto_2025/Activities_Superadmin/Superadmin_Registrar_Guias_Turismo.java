package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminGuiasTurismoBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarGuiasTurismoBinding;

public class Superadmin_Registrar_Guias_Turismo extends BaseActivity {

    private ActivitySuperadminRegistrarGuiasTurismoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRegistrarGuiasTurismoBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btn1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Registrar_Guia_Turismo.class);
            startActivity(intent);
        });

        binding.btn2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Rechazar_Guia_Turismo.class);
            startActivity(intent);
        });

        binding.btn3.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Registrar_Guia_Turismo.class);
            startActivity(intent);
        });

        binding.btn4.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Rechazar_Guia_Turismo.class);
            startActivity(intent);
        });

    }
}