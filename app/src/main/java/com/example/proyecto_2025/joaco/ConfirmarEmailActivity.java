package com.example.proyecto_2025.joaco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class ConfirmarEmailActivity extends AppCompatActivity {

    private Button btnContinuar;
    private Button btnReenviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_email_joaco);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnContinuar = findViewById(R.id.btn_continuar);
        btnReenviar = findViewById(R.id.btn_reenviar);
    }

    private void setupListeners() {
        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CodigoConfirmacionActivity.class);
            startActivity(intent);
        });

        btnReenviar.setOnClickListener(v -> {
            // Simular reenvío de código
            android.widget.Toast.makeText(this, "Código reenviado", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}