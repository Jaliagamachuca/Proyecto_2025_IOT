package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.google.firebase.auth.FirebaseAuth;

public class PendienteGuiaActivity extends AppCompatActivity {

    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendiente_guia);

        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);

        btnCerrarSesion.setOnClickListener(v -> {
            // 1. cerrar sesión
            FirebaseAuth.getInstance().signOut();
            // 2. ir al login y limpiar activities
            Intent i = new Intent(PendienteGuiaActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
