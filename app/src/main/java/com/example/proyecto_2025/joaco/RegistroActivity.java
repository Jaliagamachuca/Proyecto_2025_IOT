package com.example.proyecto_2025.joaco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class RegistroActivity extends AppCompatActivity {

    private Button btnContinuar;
    private AutoCompleteTextView spinnerRol;
    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_joaco);  // Archivo correcto

        initViews();
        setupDropdown();
        setupListeners();
    }

    private void initViews() {
        btnContinuar = findViewById(R.id.btn_continuar);
        spinnerRol = findViewById(R.id.spinner_rol);
        btnVolver = findViewById(R.id.btn_volver);
    }

    private void setupDropdown() {
        String[] roles = {"Cliente", "Guía de turismo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, roles);
        spinnerRol.setAdapter(adapter);
    }

    private void setupListeners() {

        btnVolver.setOnClickListener(v -> {
            finish(); // Cierra la activity actual y vuelve a la anterior
        });

        btnContinuar.setOnClickListener(v -> {
            String rolSeleccionado = spinnerRol.getText().toString();

            Intent intent = new Intent(this, ConfirmacionActivity.class);
            intent.putExtra("rol", rolSeleccionado);
            startActivity(intent);
        });
    }
}