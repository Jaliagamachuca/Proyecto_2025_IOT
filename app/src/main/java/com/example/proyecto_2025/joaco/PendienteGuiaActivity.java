package com.example.proyecto_2025.joaco;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class PendienteGuiaActivity extends AppCompatActivity {

    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendiente_guia_joaco);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
    }

    private void setupListeners() {
        btnCerrarSesion.setOnClickListener(v -> {
            finish();
        });
    }
}