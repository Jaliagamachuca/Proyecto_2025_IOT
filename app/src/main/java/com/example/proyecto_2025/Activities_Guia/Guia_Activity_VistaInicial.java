package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Administradores_Sede;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Clientes1;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Guias_Turismo;
import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;

public class Guia_Activity_VistaInicial extends BaseActivity {

    private ActivityGuiaVistaInicialBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaVistaInicialBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.button2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Solictar_Nuevo_Tour.class);
            startActivity(intent);
        });

        binding.button3.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Tours_Pendientes.class);
            startActivity(intent);
        });

        binding.button4.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Guia_Historial_Tours.class);
            startActivity(intent);
        });
    }
}