package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;

public class ConfirmacionActivity extends AppCompatActivity {

    private Button btnOk;
    private String rolSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        rolSeleccionado = getIntent().getStringExtra("rol"); // "cliente" o "guia"

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnOk = findViewById(R.id.btn_ok);
    }

    private void setupListeners() {
        btnOk.setOnClickListener(v -> {
            Intent intent;

            if ("cliente".equalsIgnoreCase(rolSeleccionado)) {
                intent = new Intent(this, ExitoClienteActivity.class);
            } else if ("guia".equalsIgnoreCase(rolSeleccionado) ||
                    "guía de turismo".equalsIgnoreCase(rolSeleccionado)) {
                intent = new Intent(this, PendienteGuiaActivity.class);
            } else {
                // por defecto, tratarlo como cliente
                intent = new Intent(this, ExitoClienteActivity.class);
            }

            startActivity(intent);
            finish();
        });
    }
}
