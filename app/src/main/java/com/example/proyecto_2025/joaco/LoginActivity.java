package com.example.proyecto_2025.joaco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;

// IMPORTA TUS ACTIVIDADES REALES DE CADA ROL:
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_Activity_VistaInicial;
import com.example.proyecto_2025.Activities_Administrador.Administrador_Activity_VistaInicial;
import com.example.proyecto_2025.Activities_Guia.Guia_Activity_VistaInicial;
import com.example.proyecto_2025.Activities_Usuario.Usuario_Activity_VistaInicial;

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
        btnAcceder.setOnClickListener(v -> showRolePicker());

        txtRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });

        txtOlvidastePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, RecuperarPasswordActivity.class));
        });
    }

    private void showRolePicker() {
        final CharSequence[] roles = {"Superadmin", "Admin", "Guía", "Usuario"};
        new AlertDialog.Builder(this)
                .setTitle("Ingresar como…")
                .setItems(roles, (dialog, which) -> {
                    switch (which) {
                        case 0: // Superadmin
                            startActivity(new Intent(this, Superadmin_Activity_VistaInicial.class));
                            break;
                        case 1: // Admin
                            startActivity(new Intent(this, Administrador_Activity_VistaInicial.class));
                            break;
                        case 2: // Guía
                            startActivity(new Intent(this, Guia_Activity_VistaInicial.class));
                            break;
                        case 3: // Usuario
                            // Guarda un email simple para esta fase local
                            getSharedPreferences("user_prefs", MODE_PRIVATE)
                                    .edit().putString("current_user_email", "usuario@demo.com").apply();
                            startActivity(new Intent(this, Usuario_Activity_VistaInicial.class));
                            break;
                    }
                })
                .show();
    }
}
