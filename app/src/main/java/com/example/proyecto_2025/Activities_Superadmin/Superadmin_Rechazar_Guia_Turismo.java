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
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarGuiaTurismoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Rechazar_Guia_Turismo extends BaseActivity {

    private ActivitySuperadminRechazarGuiaTurismoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRechazarGuiaTurismoBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btnRechazarGuia.setOnClickListener(view ->
                RechazarGuia());

    }
    public void RechazarGuia() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Rechazar Guía de Turismo");
        dialogBuilder.setMessage("¿Está seguro de rechazar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}