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
import com.example.proyecto_2025.databinding.ActivitySuperadminRechazarGuiaTurismoBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerAdministradorBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Administrador extends AppCompatActivity {

    private ActivitySuperadminVerAdministradorBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnActivarAdministrador.setOnClickListener(view ->
                ActivarAdministrador());

    }

    public void ActivarAdministrador() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Administrador");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}