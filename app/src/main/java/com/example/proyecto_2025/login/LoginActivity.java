package com.example.proyecto_2025.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.Activities_Administrador.Admin_HomeActivity;
import com.example.proyecto_2025.Activities_Guia.Guia_HomeActivity;
import com.example.proyecto_2025.Activities_Superadmin.Superadmin_HomeActivity;
import com.example.proyecto_2025.Activities_Usuario.Cliente_HomeActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository repo;
    private TextInputEditText etCorreo, etPassword;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        repo = new AuthRepository();
        etCorreo   = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);

        // botón ACCEDER
        findViewById(R.id.btn_acceder).setOnClickListener(v -> {
            String email = etCorreo.getText() == null ? "" : etCorreo.getText().toString().trim();
            String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,"Ingresa correo y contraseña.",Toast.LENGTH_SHORT).show();
                return;
            }

            repo.login(email, pass, new AuthRepository.Callback<DocumentSnapshot>() {
                @Override public void onSuccess(DocumentSnapshot snap) {
                    if (!snap.exists()) {
                        Toast.makeText(LoginActivity.this, "Perfil no encontrado.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String role   = snap.getString("role");
                    String status = snap.getString("status");
                    if (status == null) status = "active";

                    // 🔻🔻🔻 FLUJO REAL DE VERIFICACIÓN (COMENTADO)
                    // Boolean verReq  = snap.getBoolean("verificationRequired");
                    // Boolean emailOk = snap.getBoolean("emailVerified");
                    // if (verReq == null) verReq = false;
                    // if (emailOk == null) emailOk = false;
                    // if (verReq && !emailOk) {
                    //     Intent i = new Intent(LoginActivity.this, CodigoConfirmacionActivity.class);
                    //     i.putExtra("email", snap.getString("email"));
                    //     startActivity(i);
                    //     return;
                    // }
                    // 🔺🔺🔺

                    // guía pendiente → lo mandamos a la pantalla de "espera aprobación"
                    if ("guia".equalsIgnoreCase(role) && "pending".equalsIgnoreCase(status)) {
                        startActivity(new Intent(LoginActivity.this, PendienteGuiaActivity.class));
                        finish();
                        return;
                    }

                    // usuario deshabilitado (excepto superadmin)
                    if (!"active".equalsIgnoreCase(status) && !"superadmin".equalsIgnoreCase(role)) {
                        Toast.makeText(LoginActivity.this, "Tu cuenta está deshabilitada.", Toast.LENGTH_LONG).show();
                        repo.signOut();
                        return;
                    }

                    routeByRole(role);
                }

                @Override public void onError(Exception e) {
                    Toast.makeText(LoginActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // recuperar password
        findViewById(R.id.txt_olvidaste_password).setOnClickListener(v -> {
            String email = etCorreo.getText()==null? "" : etCorreo.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this,"Escribe tu correo arriba.",Toast.LENGTH_SHORT).show();
                return;
            }
            com.google.firebase.auth.FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email)
                    .addOnSuccessListener(x -> Toast.makeText(this,"Te enviamos un correo de recuperación.",Toast.LENGTH_LONG).show())
                    .addOnFailureListener(err -> Toast.makeText(this,err.getMessage(),Toast.LENGTH_LONG).show());
        });

        // ir a registro
        findViewById(R.id.txt_registrarse).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void routeByRole(String role){
        Class<?> next = Cliente_HomeActivity.class;
        if ("superadmin".equalsIgnoreCase(role)) {
            next = Superadmin_HomeActivity.class;
        } else if ("admin".equalsIgnoreCase(role)) {
            next = Admin_HomeActivity.class;
        } else if ("guia".equalsIgnoreCase(role))  {
            next = Guia_HomeActivity.class;
        }
        startActivity(new Intent(this, next));
        finish();
    }
}
