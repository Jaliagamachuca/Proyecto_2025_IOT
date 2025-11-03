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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearPasswordActivity extends AppCompatActivity {

    private Button btnConfirmar;
    private ImageButton btnVolver;
    private TextInputEditText etPassword, etConfirmPassword;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnConfirmar       = findViewById(R.id.btn_confirmar);
        btnVolver          = findViewById(R.id.btn_volver);
        etPassword         = findViewById(R.id.et_password);
        etConfirmPassword  = findViewById(R.id.et_confirm_password);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());

        btnConfirmar.setOnClickListener(v -> {
            String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();
            String pass2 = etConfirmPassword.getText() == null ? "" : etConfirmPassword.getText().toString().trim();

            if (pass.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Completa ambas contraseñas.", Toast.LENGTH_LONG).show();
                return;
            }
            if (!pass.equals(pass2)) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
                return;
            }
            if (pass.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser u = auth.getCurrentUser();
            if (u == null) {
                Toast.makeText(this, "Tu sesión expiró. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show();
                goLogin();
                return;
            }

            // 1) actualizar password en Auth
            u.updatePassword(pass)
                    .addOnSuccessListener(x -> {
                        // 2) actualizar doc en Firestore (opcional, pero útil)
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("updatedAt", FieldValue.serverTimestamp());
                        updates.put("passwordSet", true); // por si quieres saber si ya la cambió

                        db.collection("users").document(u.getUid()).update(updates)
                                .addOnCompleteListener(t -> {
                                    Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();
                                    // cerrar sesión y mandar al login
                                    auth.signOut();
                                    goLogin();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // puede fallar si requiere re-autenticación
                        Toast.makeText(this, "No se pudo actualizar la contraseña: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
