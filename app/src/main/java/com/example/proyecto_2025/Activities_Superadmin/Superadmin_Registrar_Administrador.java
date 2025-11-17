package com.example.proyecto_2025.Activities_Superadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarAdministradorBinding;
import com.example.proyecto_2025.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Superadmin_Registrar_Administrador extends AppCompatActivity {

    private ActivitySuperadminRegistrarAdministradorBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRegistrarAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // DatePicker para fecha de nacimiento
        binding.etFechaNacimientoAdmin.setOnClickListener(v -> mostrarDatePicker());

        // Click en Registrar
        binding.btnRegistrarAdministrador.setOnClickListener(view ->
                validarYConfirmarRegistro());
    }

    // ================== DATEPICKER ==================
    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    // m es 0-based
                    String fecha = String.format("%02d/%02d/%04d", d, (m + 1), y);
                    binding.etFechaNacimientoAdmin.setText(fecha);
                },
                year, month, day
        );
        dp.show();
    }

    // ================== VALIDACIÓN Y CONFIRMACIÓN ==================
    private void validarYConfirmarRegistro() {
        String nombre      = textOf(binding.etNombreAdmin);
        String apellidos   = textOf(binding.etApellidosAdmin);
        String dni         = textOf(binding.etDniAdmin);
        String fechaNac    = textOf(binding.etFechaNacimientoAdmin);
        String correo      = textOf(binding.etCorreoAdmin);
        String telefono    = textOf(binding.etTelefonoAdmin);
        String domicilio   = textOf(binding.etDomicilioAdmin);

        // Validaciones mínimas
        if (TextUtils.isEmpty(nombre)) {
            binding.tilNombreAdmin.setError("Obligatorio");
            return;
        } else binding.tilNombreAdmin.setError(null);

        if (TextUtils.isEmpty(apellidos)) {
            binding.tilApellidosAdmin.setError("Obligatorio");
            return;
        } else binding.tilApellidosAdmin.setError(null);

        if (TextUtils.isEmpty(dni)) {
            binding.tilDniAdmin.setError("Obligatorio");
            return;
        } else binding.tilDniAdmin.setError(null);

        if (TextUtils.isEmpty(correo)) {
            binding.tilCorreoAdmin.setError("Obligatorio");
            return;
        } else binding.tilCorreoAdmin.setError(null);

        // Para el proyecto, usaremos el DNI como contraseña inicial
        String password = dni;

        String displayName = nombre + " " + apellidos;

        new MaterialAlertDialogBuilder(this)
                .setTitle("Registrar Administrador")
                .setMessage("¿Registrar al administrador:\n\n" +
                        displayName + "\n" +
                        "Email: " + correo + "\n\n" +
                        "Se usará el DNI como contraseña inicial.")
                .setNegativeButton(R.string.cancel, (d, i) -> d.dismiss())
                .setPositiveButton(R.string.ok, (d, i) -> {
                    crearAdminEnAuthYFirestore(
                            displayName, dni, correo, telefono, domicilio, fechaNac, password
                    );
                })
                .show();
    }

    private String textOf(android.widget.TextView tv) {
        return tv.getText() == null ? "" : tv.getText().toString().trim();
    }

    // ================== CREAR EN AUTH + FIRESTORE ==================
    private void crearAdminEnAuthYFirestore(String displayName,
                                            String dni,
                                            String email,
                                            String phone,
                                            String domicilio,
                                            String fechaNacimiento,
                                            String password) {

        // 1) Usamos un FirebaseApp secundario para NO cerrar la sesión del superadmin
        FirebaseApp defaultApp = FirebaseApp.getInstance();
        FirebaseOptions options = defaultApp.getOptions();

        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance("secondary_admin_create");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.initializeApp(
                    this,
                    options,
                    "secondary_admin_create"
            );
        }

        FirebaseAuth tempAuth = FirebaseAuth.getInstance(secondaryApp);

        // 2) Crear usuario en Authentication
        tempAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener((AuthResult result) -> {
                    String uid = result.getUser().getUid();

                    // 3) Crear documento en "users" con role = admin
                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", uid);
                    data.put("displayName", displayName);
                    data.put("email", email);
                    data.put("phone", phone);
                    data.put("dni", dni);
                    data.put("role", "admin");
                    data.put("status", "active");
                    data.put("companyId", null);

                    // Campos opcionales de tu modelo
                    data.put("nombre", displayName);      // compat
                    data.put("apellidos", null);          // si luego quieres separarlo
                    data.put("fechaNacimiento", fechaNacimiento);
                    data.put("domicilio", domicilio);

                    // Métricas cliente/guía en null o 0
                    data.put("ratingPromedio", 0.0);
                    data.put("totalValoraciones", 0L);
                    data.put("toursRealizados", 0L);
                    data.put("reservasTotales", 0L);

                    data.put("createdAt", FieldValue.serverTimestamp());
                    data.put("updatedAt", FieldValue.serverTimestamp());

                    db.collection("users")
                            .document(uid)
                            .set(data)
                            .addOnSuccessListener(v -> {
                                Toast.makeText(
                                        this,
                                        "Administrador creado correctamente.",
                                        Toast.LENGTH_LONG
                                ).show();
                                finish(); // volver al home -> onResume recarga listas
                            })
                            .addOnFailureListener(e -> {
                                Log.e("superadmin-admin", "Error guardando en Firestore", e);
                                Toast.makeText(
                                        this,
                                        "Error guardando datos del admin: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("superadmin-admin", "Error creando usuario en Auth", e);
                    Toast.makeText(
                            this,
                            "Error creando usuario en Authentication: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}
