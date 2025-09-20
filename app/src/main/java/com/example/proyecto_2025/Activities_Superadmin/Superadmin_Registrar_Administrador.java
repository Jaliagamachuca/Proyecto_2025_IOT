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
import com.example.proyecto_2025.databinding.ActivitySuperadminAdministradoresSedeBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarAdministradorBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Registrar_Administrador extends BaseActivity {

    private ActivitySuperadminRegistrarAdministradorBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRegistrarAdministradorBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btnRegistrarAdministrador.setOnClickListener(view ->
                RegistrarAdministrador());
    }

    public void RegistrarAdministrador() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Registrar Administrador");
        dialogBuilder.setMessage("¿Está seguro de registrar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}