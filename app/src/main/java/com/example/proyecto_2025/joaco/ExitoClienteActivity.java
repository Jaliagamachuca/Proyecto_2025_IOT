package com.example.proyecto_2025.joaco;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class ExitoClienteActivity extends AppCompatActivity {

    private Button btnIrInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exito_cliente_joaco);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnIrInicio = findViewById(R.id.btn_ir_inicio);
    }

    private void setupListeners() {
        btnIrInicio.setOnClickListener(v -> {
            // Volver al login o ir a pantalla principal
            finish();
        });
    }
}
