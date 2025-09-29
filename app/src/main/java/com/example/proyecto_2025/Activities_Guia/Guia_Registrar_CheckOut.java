package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaRegistrarCheckInBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaRegistrarCheckOutBinding;

public class Guia_Registrar_CheckOut extends AppCompatActivity {

    private ActivityGuiaRegistrarCheckOutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaRegistrarCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}