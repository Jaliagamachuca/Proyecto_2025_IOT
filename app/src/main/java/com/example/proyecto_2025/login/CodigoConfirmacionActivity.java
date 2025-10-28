package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class CodigoConfirmacionActivity extends AppCompatActivity {

    private Button btnContinuar;
    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_confirmacion_joaco);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnContinuar = findViewById(R.id.btn_continuar);
        btnVolver = findViewById(R.id.btn_volver);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> {
            finish(); // Volver a la pantalla anterior
        });

        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearPasswordActivity.class);
            startActivity(intent);
        });
    }
}