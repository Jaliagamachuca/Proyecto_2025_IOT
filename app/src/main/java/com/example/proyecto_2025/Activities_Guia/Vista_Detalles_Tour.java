package com.example.proyecto_2025.Activities_Guia;

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
import com.example.proyecto_2025.databinding.ActivitySuperadminVerGuiaTurismoBinding;
import com.example.proyecto_2025.databinding.ActivityVistaDetallesTourBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Vista_Detalles_Tour extends AppCompatActivity {

    private ActivityVistaDetallesTourBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVistaDetallesTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSolicitarTour.setOnClickListener(view ->
                solicitarTour());

    }

    public void solicitarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Solicitar Tour");
        dialogBuilder.setMessage("¿Está seguro de solicitar este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}