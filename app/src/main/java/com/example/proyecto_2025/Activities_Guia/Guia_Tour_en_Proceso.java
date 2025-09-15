package com.example.proyecto_2025.Activities_Guia;

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
import com.example.proyecto_2025.databinding.ActivityGuiaTourEnProcesoBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;

public class Guia_Tour_en_Proceso extends BaseActivity {

    private ActivityGuiaTourEnProcesoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaTourEnProcesoBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

    }
}