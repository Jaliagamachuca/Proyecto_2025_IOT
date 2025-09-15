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
import com.example.proyecto_2025.databinding.ActivitySuperadminAdministradoresSedeBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Administradores_Sede extends BaseActivity {

    private ActivitySuperadminAdministradoresSedeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminAdministradoresSedeBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btn1.setOnClickListener(view ->
                activarAdministrador());

        binding.btn2.setOnClickListener(view ->
                desactivarAdministrador());

        binding.btnRegistrarAdministrador.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Registrar_Administrador.class);
            startActivity(intent);
        });
    }

    public void activarAdministrador() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Administrador");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void desactivarAdministrador() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Desactivar Administrador");
        dialogBuilder.setMessage("¿Está seguro de desactivar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}