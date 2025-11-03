package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class CodigoConfirmacionActivity extends AppCompatActivity {

    private EditText et1, et2, et3, et4;
    private Button btnContinuar;
    private ImageButton btnVolver;
    private TextView tvSubtitulo;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_confirmacion);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
        fillEmailIfAvailable();
    }

    private void initViews() {
        btnContinuar = findViewById(R.id.btn_continuar);
        btnVolver    = findViewById(R.id.btn_volver);
        et1 = findViewById(R.id.et_digit1);
        et2 = findViewById(R.id.et_digit2);
        et3 = findViewById(R.id.et_digit3);
        et4 = findViewById(R.id.et_digit4);
        tvSubtitulo = findViewById(R.id.tv_subtitulo);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());

        btnContinuar.setOnClickListener(v -> {
            String d1 = et1.getText() == null ? "" : et1.getText().toString().trim();
            String d2 = et2.getText() == null ? "" : et2.getText().toString().trim();
            String d3 = et3.getText() == null ? "" : et3.getText().toString().trim();
            String d4 = et4.getText() == null ? "" : et4.getText().toString().trim();

            String codeUser = d1 + d2 + d3 + d4;
            if (codeUser.length() != 4) {
                Toast.makeText(this, "Ingresa los 4 dígitos.", Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser u = auth.getCurrentUser();
            if (u == null) {
                Toast.makeText(this, "Vuelve a iniciar sesión para confirmar.", Toast.LENGTH_LONG).show();
                return;
            }

            String uid = u.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(snap -> {
                        if (!snap.exists()) {
                            Toast.makeText(this, "No se encontró tu perfil.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String codeDb = snap.getString("verificationCode");
                        if (codeDb == null) {
                            Toast.makeText(this, "No hay código registrado. Vuelve a registrarte.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // si en BD está en 6 dígitos y en UI en 4 → comparamos primeros 4
                        if (codeDb.length() > 4) {
                            codeDb = codeDb.substring(0, 4);
                        }

                        if (!codeDb.equals(codeUser)) {
                            Toast.makeText(this, "Código incorrecto.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // si el código es correcto → actualizar flags
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("emailVerified", true);
                        updates.put("verificationRequired", false);
                        updates.put("updatedAt", FieldValue.serverTimestamp());

                        db.collection("users").document(uid).update(updates)
                                .addOnSuccessListener(x -> {
                                    Toast.makeText(this, "Correo verificado.", Toast.LENGTH_LONG).show();
                                    // seguir tu flujo original → crear password
                                    Intent i = new Intent(this, CrearPasswordActivity.class);
                                    startActivity(i);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    private void fillEmailIfAvailable() {
        // si el login te mandó el email como extra, lo mostramos
        String email = getIntent().getStringExtra("email");
        if (email == null) {
            // si no vino por intent, tratamos de leerlo del currentUser
            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
            if (u != null) email = u.getEmail();
        }
        if (email != null && tvSubtitulo != null) {
            tvSubtitulo.setText("Se envió un código de 4 dígitos\na " + email);
        }
    }
}
