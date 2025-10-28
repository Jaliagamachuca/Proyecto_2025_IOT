package com.example.proyecto_2025.login;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;

public class RegistroActivity extends AppCompatActivity {
    private AuthRepository repo;

    private TextInputEditText etNombre, etCorreo, etPass, etPass2;
    private AutoCompleteTextView rolView;
    private CheckBox cbTerminos;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_registro);
        repo = new AuthRepository();

        etNombre = findViewById(R.id.et_nombre);
        etCorreo = findViewById(R.id.et_correo);
        etPass   = findViewById(R.id.et_password);
        etPass2  = findViewById(R.id.et_confirm_password);
        rolView  = findViewById(R.id.spinner_rol);
        cbTerminos = findViewById(R.id.cb_terminos);

        // Dropdown de roles ("cliente" / "guia")
        String[] roles = new String[]{"cliente","guia"};
        rolView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles));
        rolView.setText(roles[0], false); // valor por defecto

        findViewById(R.id.btn_volver).setOnClickListener(v -> finish());

        findViewById(R.id.btn_continuar).setOnClickListener(v -> {
            String name  = safe(etNombre);
            String email = safe(etCorreo);
            String pass  = safe(etPass);
            String pass2 = safe(etPass2);
            String role  = safe(rolView).toLowerCase();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                toast("Completa nombre, correo y contraseña."); return;
            }
            if (pass.length() < 6) { toast("La contraseña debe tener al menos 6 caracteres."); return; }
            if (!pass.equals(pass2)) { toast("Las contraseñas no coinciden."); return; }
            if (!role.equals("cliente") && !role.equals("guia")) { toast("Selecciona un rol válido."); return; }
            if (!cbTerminos.isChecked()) { toast("Debes aceptar los términos y condiciones."); return; }

            repo.signUp(name, email, pass, role, new AuthRepository.Callback<Void>() {
                @Override public void onSuccess(Void unused) {
                    toast("Cuenta creada. Inicia sesión."); finish();
                }
                @Override public void onError(Exception e) {
                    toast("Error: "+e.getMessage());
                }
            });
        });
    }

    private String safe(TextInputEditText t){ return t.getText()==null? "": t.getText().toString().trim(); }
    private String safe(AutoCompleteTextView t){ return t.getText()==null? "": t.getText().toString().trim(); }
    private void toast(String m){ Toast.makeText(this,m,Toast.LENGTH_LONG).show(); }
}
