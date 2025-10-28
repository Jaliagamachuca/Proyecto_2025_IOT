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

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        repo = new AuthRepository();
        etCorreo   = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);

        findViewById(R.id.btn_acceder).setOnClickListener(v -> {
            String email = etCorreo.getText() == null ? "" : etCorreo.getText().toString().trim();
            String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,"Ingresa correo y contraseña.",Toast.LENGTH_SHORT).show();
                return;
            }

            repo.login(email, pass, new AuthRepository.Callback<DocumentSnapshot>() {
                @Override public void onSuccess(DocumentSnapshot snap) {
                    routeByRole(snap.getString("role"));
                }
                @Override public void onError(Exception e) {
                    Toast.makeText(LoginActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // (Opcional) listeners de “olvidaste contraseña” y “registrarse”
        findViewById(R.id.txt_olvidaste_password).setOnClickListener(v -> {
            String email = etCorreo.getText()==null? "" : etCorreo.getText().toString().trim();
            if (email.isEmpty()) { Toast.makeText(this,"Escribe tu correo arriba.",Toast.LENGTH_SHORT).show(); return; }
            com.google.firebase.auth.FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email)
                    .addOnSuccessListener(x -> Toast.makeText(this,"Te enviamos un correo de recuperación.",Toast.LENGTH_LONG).show())
                    .addOnFailureListener(err -> Toast.makeText(this,err.getMessage(),Toast.LENGTH_LONG).show());
        });

        findViewById(R.id.txt_registrarse).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void routeByRole(String role){
        Class<?> next = Cliente_HomeActivity.class;
        if ("superadmin".equals(role)) next = Superadmin_HomeActivity.class;
        else if ("admin".equals(role)) next = Admin_HomeActivity.class;
        else if ("guia".equals(role))  next = Guia_HomeActivity.class;
        startActivity(new Intent(this, next));
        finish();
    }
}
