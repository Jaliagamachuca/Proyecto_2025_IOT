package com.example.proyecto_2025.Activities_Superadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarAdministradorBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
        binding.etFechaNacimientoAdmin.setOnClickListener(v -> mostrarDatePicker());

        db = FirebaseFirestore.getInstance();

        binding.btnRegistrarAdministrador.setOnClickListener(view -> RegistrarAdministrador());
    }
    private void mostrarDatePicker() {
        final java.util.Calendar cal = java.util.Calendar.getInstance();

        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH);
        int day = cal.get(java.util.Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog dp = new android.app.DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    // Formato dd/MM/yyyy
                    String dia = (d < 10 ? "0" + d : String.valueOf(d));
                    String mes = (m + 1 < 10 ? "0" + (m + 1) : String.valueOf(m + 1));
                    String fecha = dia + "/" + mes + "/" + y;
                    binding.etFechaNacimientoAdmin.setText(fecha);
                },
                year, month, day
        );

        // Evitar fechas futuras
        dp.getDatePicker().setMaxDate(System.currentTimeMillis());
        dp.show();
    }

    private void RegistrarAdministrador() {
        // Leer campos
        String nombre = getText(binding.etNombreAdmin);
        String apellidos = getText(binding.etApellidosAdmin);
        String dni = getText(binding.etDniAdmin);
        String fechaNac = getText(binding.etFechaNacimientoAdmin);
        String correo = getText(binding.etCorreoAdmin);
        String telefono = getText(binding.etTelefonoAdmin);
        String domicilio = getText(binding.etDomicilioAdmin);
        // edad es opcional
        String edadStr = getText(binding.etEdadAdmin);

        // Validaciones mínimas
        if (TextUtils.isEmpty(nombre)) {
            binding.tilNombreAdmin.setError("Ingrese el nombre");
            return;
        } else {
            binding.tilNombreAdmin.setError(null);
        }

        if (TextUtils.isEmpty(apellidos)) {
            binding.tilApellidosAdmin.setError("Ingrese los apellidos");
            return;
        } else {
            binding.tilApellidosAdmin.setError(null);
        }

        if (TextUtils.isEmpty(dni)) {
            binding.tilDniAdmin.setError("Ingrese el DNI");
            return;
        } else {
            binding.tilDniAdmin.setError(null);
        }

        if (TextUtils.isEmpty(correo)) {
            binding.tilCorreoAdmin.setError("Ingrese el correo");
            return;
        } else {
            binding.tilCorreoAdmin.setError(null);
        }

        // Confirmación
        String nombreCompleto = nombre + " " + apellidos;
        new MaterialAlertDialogBuilder(this)
                .setTitle("Registrar Administrador")
                .setMessage("¿Está seguro de registrar a:\n\n" + nombreCompleto + "\n" + correo + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) ->
                        Log.d("msg-test", "Registro admin cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) ->
                        guardarAdministrador(nombre, apellidos, dni, fechaNac, correo, telefono, domicilio))
                .show();
    }

    private String getText(android.widget.EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void guardarAdministrador(String nombre,
                                      String apellidos,
                                      String dni,
                                      String fechaNac,
                                      String correo,
                                      String telefono,
                                      String domicilio) {

        String displayName = (nombre + " " + apellidos).trim();

        Map<String, Object> data = new HashMap<>();
        // Campos base del esquema
        data.put("displayName", displayName);
        data.put("email", correo);
        data.put("dni", dni);
        data.put("phone", telefono);
        data.put("role", "admin");
        data.put("status", "active");
        // Si aún no usas companyId, puedes dejarlo null o no ponerlo
        // data.put("companyId", null);

        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());

        // Compatibilidad con tu modelo anterior
        data.put("nombre", nombre);
        data.put("apellidos", apellidos);
        data.put("fechaNacimiento", fechaNac);
        data.put("domicilio", domicilio);

        db.collection("users")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    // Guardamos uid = id del documento para que el User se arme bien luego
                    docRef.update("uid", docRef.getId());

                    Toast.makeText(this,
                            "Administrador registrado correctamente.",
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("RegistrarAdmin", "Error al registrar administrador", e);
                    Toast.makeText(this,
                            "Error al registrar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
