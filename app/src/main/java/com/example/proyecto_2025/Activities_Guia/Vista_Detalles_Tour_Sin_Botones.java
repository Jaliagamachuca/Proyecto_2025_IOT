package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Administrador;
import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaToursPendientesBinding;
import com.example.proyecto_2025.databinding.ActivityVistaDetallesTourSinBotonesBinding;

public class Vista_Detalles_Tour_Sin_Botones extends BaseActivity {

    private ActivityVistaDetallesTourSinBotonesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVistaDetallesTourSinBotonesBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

    }
}