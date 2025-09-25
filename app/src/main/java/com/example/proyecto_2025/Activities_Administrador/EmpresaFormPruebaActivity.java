package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class EmpresaFormPruebaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_form_prueba);

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etTelefono = findViewById(R.id.etTelefono);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String tel    = etTelefono.getText().toString().trim();

            // Comportamiento de prueba
            Toast.makeText(this,
                    "Guardado (prueba): " + nombre + " / " + correo + " / " + tel,
                    Toast.LENGTH_SHORT).show();

            finish(); // opcional, volver atrás
        });
    }
}
