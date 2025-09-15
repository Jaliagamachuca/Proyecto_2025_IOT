package com.example.proyecto_2025.Activities_Superadmin;

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
import com.example.proyecto_2025.databinding.ActivitySuperadminGuiasTurismoBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarAdministradorBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Guias_Turismo extends BaseActivity {

    private ActivitySuperadminGuiasTurismoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminGuiasTurismoBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btn1.setOnClickListener(view ->
                activarGuia());

        binding.btn2.setOnClickListener(view ->
                desactivarGuia());


        binding.InfoGuia1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Ver_Guia_Turismo.class);
            startActivity(intent);
        });

        binding.InfoGuia2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Ver_Guia_Turismo.class);
            startActivity(intent);
        });

        binding.btnRegistrarGuia.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Registrar_Guias_Turismo.class);
            startActivity(intent);
        });
    }

    public void activarGuia() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Guía de Turismo");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void desactivarGuia() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Desactivar Guía de Turismo");
        dialogBuilder.setMessage("¿Está seguro de desactivar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}
