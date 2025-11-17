package com.example.proyecto_2025.login;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private AuthRepository repo;

    private TextInputEditText etNombre, etCorreo, etPass, etPass2, etDni;
    private AutoCompleteTextView rolView, idiomaView, zonaView;
    private TextInputLayout layoutDni, layoutIdiomas, layoutZona;
    private CheckBox cbTerminos;

    // FUENTES para selección múltiple
    private List<String> idiomasList;
    private List<String> zonasList;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_registro);

        repo = new AuthRepository();

        // -------------------------------
        // REFERENCIAS XML
        // -------------------------------
        etNombre = findViewById(R.id.et_nombre);
        etCorreo = findViewById(R.id.et_correo);
        etPass   = findViewById(R.id.et_password);
        etPass2  = findViewById(R.id.et_confirm_password);
        rolView  = findViewById(R.id.spinner_rol);
        cbTerminos = findViewById(R.id.cb_terminos);

        layoutDni = findViewById(R.id.layout_dni);
        etDni     = findViewById(R.id.et_dni);

        layoutIdiomas = findViewById(R.id.layout_idiomas);
        idiomaView    = findViewById(R.id.spinner_idiomas);

        layoutZona = findViewById(R.id.layout_zonaOp);
        zonaView    = findViewById(R.id.spinner_zonaOp);

        findViewById(R.id.btn_volver).setOnClickListener(v -> finish());

        // -------------------------------
        // CONFIGURAR ROLES
        // -------------------------------
        String[] roles = {"cliente", "guia"};
        rolView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles));

        rolView.setOnItemClickListener((parent, view, pos, id) -> {
            boolean esGuia = roles[pos].equals("guia");

            layoutDni.setVisibility(esGuia ? View.VISIBLE : View.GONE);
            layoutIdiomas.setVisibility(esGuia ? View.VISIBLE : View.GONE);
            layoutZona.setVisibility(esGuia ? View.VISIBLE : View.GONE);
        });

        // -------------------------------
        // LISTAS BASE PARA MULTISELECT
        // -------------------------------
        idiomasList = Arrays.asList("es", "en", "fr", "it", "pt", "de");
        zonasList = Arrays.asList(
                "Amazonas",
                "Áncash",
                "Apurímac",
                "Arequipa",
                "Ayacucho",
                "Cajamarca",
                "Callao",
                "Cusco",
                "Huancavelica",
                "Huánuco",
                "Ica",
                "Junín",
                "La Libertad",
                "Lambayeque",
                "Lima",
                "Loreto",
                "Madre de Dios",
                "Moquegua",
                "Pasco",
                "Piura",
                "Puno",
                "San Martín",
                "Tacna",
                "Tumbes",
                "Ucayali"
        );


        // -------------------------------
        // MULTISELECT: IDIOMAS
        // -------------------------------
        idiomaView.setOnClickListener(v -> {
            boolean[] checked = new boolean[idiomasList.size()];
            List<String> seleccion = new ArrayList<>();

            new AlertDialog.Builder(this)
                    .setTitle("Selecciona idiomas")
                    .setMultiChoiceItems(
                            idiomasList.toArray(new String[0]),
                            checked,
                            (dialog, index, isChecked) -> {
                                if (isChecked) seleccion.add(idiomasList.get(index));
                                else seleccion.remove(idiomasList.get(index));
                            }
                    )
                    .setPositiveButton("Aceptar", (d, w) -> {
                        idiomaView.setText(String.join(", ", seleccion));
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // -------------------------------
        // MULTISELECT: ZONA OPERACIÓN
        // -------------------------------
        zonaView.setOnClickListener(v -> {
            boolean[] checked = new boolean[zonasList.size()];
            List<String> seleccion = new ArrayList<>();

            new AlertDialog.Builder(this)
                    .setTitle("Zonas de operación")
                    .setMultiChoiceItems(
                            zonasList.toArray(new String[0]),
                            checked,
                            (dialog, index, isChecked) -> {
                                if (isChecked) seleccion.add(zonasList.get(index));
                                else seleccion.remove(zonasList.get(index));
                            }
                    )
                    .setPositiveButton("Aceptar", (d, w) -> {
                        zonaView.setText(String.join(", ", seleccion));
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // -------------------------------
        // BOTÓN CONTINUAR
        // -------------------------------
        findViewById(R.id.btn_continuar).setOnClickListener(v -> registrar());
    }

    // =======================================================================
    // REGISTRAR USUARIO
    // =======================================================================
    private void registrar() {
        String nombre = safe(etNombre);
        String email  = safe(etCorreo);
        String pass   = safe(etPass);
        String pass2  = safe(etPass2);
        String rol    = safe(rolView);

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            toast("Completa todos los campos obligatorios.");
            return;
        }
        if (!pass.equals(pass2)) {
            toast("Las contraseñas no coinciden.");
            return;
        }
        if (!cbTerminos.isChecked()) {
            toast("Debes aceptar los términos y condiciones.");
            return;
        }

        Map<String,Object> u = new HashMap<>();
        u.put("displayName", nombre);
        u.put("email", email);
        u.put("role", rol);
        u.put("photoUrl", "");
        u.put("createdAt", Timestamp.now());
        u.put("updatedAt", Timestamp.now());
        u.put("companyId", null);

        // ===============================
        // CLIENTE
        // ===============================
        if (rol.equals("cliente")) {
            u.put("status", "active");
            u.put("reservasTotales", 0);
            u.put("ultimaReservaAt", null);
        }

        // ===============================
        // GUÍA
        // ===============================
        if (rol.equals("guia")) {
            String dni = safe(etDni);
            if (dni.isEmpty()) {
                toast("El guía debe ingresar DNI.");
                return;
            }

            u.put("dni", dni);
            u.put("status", "pending"); // espera aprobación

            // IDIOMAS
            String idiomaStr = safe(idiomaView);
            List<String> idiomas = idiomaStr.isEmpty()
                    ? new ArrayList<>()
                    : Arrays.asList(idiomaStr.split(",\\s*"));
            u.put("idiomas", idiomas);

            // ZONA OPERACIÓN
            String zonaStr = safe(zonaView);
            List<String> zonas = zonaStr.isEmpty()
                    ? new ArrayList<>()
                    : Arrays.asList(zonaStr.split(",\\s*"));
            u.put("zonaOperacion", zonas);

            // CAMPOS AUTOMÁTICOS DEL GUÍA
            u.put("ratingPromedio", 0.0);
            u.put("totalValoraciones", 0);
            u.put("toursRealizados", 0);
        }

        // Auth + Firestore
        repo.signUpWithExtraFields(email, pass, u, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (rol.equals("guia"))
                    toast("Cuenta creada. Espera aprobación del Superadmin.");
                else
                    toast("Registro exitoso.");

                finish();
            }

            @Override
            public void onError(Exception e) {
                toast("Error: " + e.getMessage());
            }
        });
    }

    // -------------------------------
    // HELPERS
    // -------------------------------
    private String safe(TextInputEditText t){
        return t.getText()==null ? "" : t.getText().toString().trim();
    }
    private String safe(AutoCompleteTextView t){
        return t.getText()==null ? "" : t.getText().toString().trim();
    }
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
