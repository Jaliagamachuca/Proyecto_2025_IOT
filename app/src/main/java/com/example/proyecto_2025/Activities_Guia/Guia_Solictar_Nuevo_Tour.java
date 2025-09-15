package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Registrar_Administrador;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Administrador;
import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaSolictarNuevoTourBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaToursPendientesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Guia_Solictar_Nuevo_Tour extends BaseActivity {

    private ActivityGuiaSolictarNuevoTourBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaSolictarNuevoTourBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());

        binding.btn1.setOnClickListener(view ->
                solicitarTour());

        binding.btn2.setOnClickListener(view ->
                rechazarTour());

        binding.btn3.setOnClickListener(view ->
                errorSolicitar());

        binding.btn4.setOnClickListener(view ->
                errorCancelar());

        binding.InfoTour1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        binding.InfoTour2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        binding.InfoTour3.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        binding.InfoTour4.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });
    }

    public void solicitarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Solicitar Tour");
        dialogBuilder.setMessage("¿Está seguro de solicitar este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void rechazarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Cancelar Tour");
        dialogBuilder.setMessage("¿Está seguro cancelar la solicitud de este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void errorSolicitar() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("ERROR");
        dialogBuilder.setMessage("Usted ya tiene otro tour solicitado con la misma fecha, cancele la solicitud anterior y vuelva a intentarlo ");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void errorCancelar() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("ERROR");
        dialogBuilder.setMessage("Este Tour ya ha sido publicado por el Administrador de la Empresa, ya no puede cancelar su solicitud");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

}