package com.example.proyecto_2025.Activities_Superadmin;

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
import com.example.proyecto_2025.databinding.ActivitySuperadminVerAdministradorBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerGuiaTurismoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Guia_Turismo extends AppCompatActivity {

    private ActivitySuperadminVerGuiaTurismoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerGuiaTurismoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnActivarGuia.setOnClickListener(view ->
                ActivarGuia());

    }

    public void ActivarGuia() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Guía de Turismo");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}