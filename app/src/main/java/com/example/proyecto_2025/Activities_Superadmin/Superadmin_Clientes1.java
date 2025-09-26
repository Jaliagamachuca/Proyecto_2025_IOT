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
import com.example.proyecto_2025.databinding.ActivitySuperadminClientes1Binding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Clientes1 extends AppCompatActivity {

    private ActivitySuperadminClientes1Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminClientes1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn1.setOnClickListener(view ->
                activarCliente());

        binding.btn2.setOnClickListener(view ->
                desactivarCliente());

        binding.InfoCliente1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Ver_Cliente.class);
            startActivity(intent);
        });

        binding.InfoCliente2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Superadmin_Ver_Cliente.class);
            startActivity(intent);
        });
    }
    public void activarCliente() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Cliente");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void desactivarCliente() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Desactivar Cliente");
        dialogBuilder.setMessage("¿Está seguro de desactivar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}