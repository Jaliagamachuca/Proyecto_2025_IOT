package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarPasswordActivity extends AppCompatActivity {

    private Button btnContinuar;
    private ImageButton btnVolver;
    private TextInputEditText etCorreo;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);

        auth = FirebaseAuth.getInstance();
        initViews();
        setupListeners();
    }

    private void initViews() {
        btnContinuar = findViewById(R.id.btn_continuar);
        btnVolver    = findViewById(R.id.btn_volver);
        etCorreo     = findViewById(R.id.et_correo);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish()); // volver al login

        btnContinuar.setOnClickListener(v -> {
            String email = etCorreo.getText() == null ? "" : etCorreo.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo electrónico.", Toast.LENGTH_LONG).show();
                return;
            }

            // enviar correo de recuperación real
            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Te enviamos un correo para restablecer tu contraseña.", Toast.LENGTH_LONG).show();

                        // volver al login limpio
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
