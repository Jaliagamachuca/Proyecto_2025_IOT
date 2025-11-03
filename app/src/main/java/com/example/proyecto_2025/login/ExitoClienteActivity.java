package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.google.firebase.auth.FirebaseAuth;

public class ExitoClienteActivity extends AppCompatActivity {

    private Button btnIrInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exito_cliente);

        btnIrInicio = findViewById(R.id.btn_ir_inicio);

        btnIrInicio.setOnClickListener(v -> {
            // por si el usuario quedó logueado tras el registro
            FirebaseAuth.getInstance().signOut();

            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
