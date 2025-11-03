package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfirmarEmailActivity extends AppCompatActivity {

    private Button btnContinuar;
    private Button btnReenviar;
    private TextView txtEmailUsuario;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_email);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        initViews();
        fillEmail();
        setupListeners();
    }

    private void initViews() {
        btnContinuar    = findViewById(R.id.btn_continuar);
        btnReenviar     = findViewById(R.id.btn_reenviar);
        txtEmailUsuario = findViewById(R.id.txt_email_usuario);
    }

    private void fillEmail() {
        // 1) si vino por intent, úsalo
        String email = getIntent().getStringExtra("email");

        // 2) si no vino, usa el del usuario logueado
        if (email == null) {
            FirebaseUser u = auth.getCurrentUser();
            if (u != null) {
                email = u.getEmail();
            }
        }

        if (email != null) {
            txtEmailUsuario.setText(email);
        }
    }

    private void setupListeners() {
        btnContinuar.setOnClickListener(v -> {
            // pasamos el email para mostrarlo en la pantalla de 4 dígitos
            String email = txtEmailUsuario.getText().toString();
            Intent intent = new Intent(this, CodigoConfirmacionActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnReenviar.setOnClickListener(v -> reenviarCodigo());
    }

    private void reenviarCodigo() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) {
            Toast.makeText(this, "Vuelve a iniciar sesión para reenviar.", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = u.getUid();

        // 1. generar nuevo código (4 dígitos)
        String newCode = gen4Code();

        // 2. actualizar en Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("verificationCode", newCode);
        updates.put("verificationRequired", true);
        updates.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(x -> {
                    // 3. enviar correo de verificación de Firebase (el link)
                    u.sendEmailVerification();

                    Toast.makeText(this, "Te reenviamos el código al correo.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al reenviar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private String gen4Code() {
        int n = new Random().nextInt(9000) + 1000; // 1000..9999
        return String.valueOf(n);
    }
}
