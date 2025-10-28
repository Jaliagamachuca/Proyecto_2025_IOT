package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class CrearPasswordActivity extends AppCompatActivity {

    private Button btnConfirmar;
    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_joaco);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnConfirmar = findViewById(R.id.btn_confirmar);
        btnVolver = findViewById(R.id.btn_volver);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> {
            finish(); // Volver a la pantalla anterior
        });

        btnConfirmar.setOnClickListener(v -> {
            // Mostrar mensaje de éxito y volver al login
            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();

            // Navegar al login y limpiar stack de activities
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}