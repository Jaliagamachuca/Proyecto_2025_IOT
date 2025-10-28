package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class ConfirmacionActivity extends AppCompatActivity {

    private Button btnConfirmar;
    private String rolSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_joaco);
        rolSeleccionado = getIntent().getStringExtra("rol");
        initViews();
        setupListeners();
    }

    private void initViews() {
        btnConfirmar = findViewById(R.id.btn_confirmar);

    }

    private void setupListeners() {
        btnConfirmar.setOnClickListener(v -> {
            Intent intent;

            if ("Cliente".equals(rolSeleccionado)) {
                intent = new Intent(this, ExitoClienteActivity.class);
            } else if ("Guía de turismo".equals(rolSeleccionado)) {
                intent = new Intent(this, PendienteGuiaActivity.class);
            } else {
                // Por defecto, ir a cliente
                intent = new Intent(this, ExitoClienteActivity.class);
            }

            startActivity(intent);
        });

    }
}