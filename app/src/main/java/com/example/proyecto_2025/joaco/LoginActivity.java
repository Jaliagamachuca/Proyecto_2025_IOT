package com.example.proyecto_2025.joaco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnAcceder;
    private TextView txtRegistrarse;
    private TextView txtOlvidastePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_joaco);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnAcceder = findViewById(R.id.btn_acceder);
        txtRegistrarse = findViewById(R.id.txt_registrarse);
        txtOlvidastePassword = findViewById(R.id.txt_olvidaste_password);
    }

    private void setupListeners() {
        btnAcceder.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Función de login pendiente", android.widget.Toast.LENGTH_SHORT).show();
        });

        txtRegistrarse.setOnClickListener(v -> {
            navigateToRegistro();
        });

        txtOlvidastePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecuperarPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToRegistro() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
}
