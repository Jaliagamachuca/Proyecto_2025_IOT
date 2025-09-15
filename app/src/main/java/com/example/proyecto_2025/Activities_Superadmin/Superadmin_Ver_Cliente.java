package com.example.proyecto_2025.Activities_Superadmin;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerClientesBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerGuiaTurismoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Cliente extends BaseActivity {

    private ActivitySuperadminVerClientesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerClientesBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btnActivarCliente.setOnClickListener(view ->
                ActivarCliente());

    }

    public void ActivarCliente() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Cliente");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}