package com.example.proyecto_2025.Activities_Usuario;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityEditarPerfilClienteBinding;
import com.example.proyecto_2025.databinding.ActivityEditarPerfilGuiaBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivityCliente extends AppCompatActivity {

    ActivityEditarPerfilClienteBinding binding;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditarPerfilClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Cargar datos recibidos
        binding.inputNombre.setText(getIntent().getStringExtra("nombre"));
        binding.inputCorreo.setText(getIntent().getStringExtra("email"));
        binding.inputTelefono.setText(getIntent().getStringExtra("telefono"));
        binding.inputDni.setText(getIntent().getStringExtra("dni"));
        binding.inputFechaNacmiento.setText(getIntent().getStringExtra("fechaNacmiento"));
        binding.inputDomicilio.setText(getIntent().getStringExtra("domicilio"));


        // Manejar campos editables
        configurarCamposEditables();

        // Guardar cambios
        binding.btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    /** Cambia un TextInputEditText a editable cuando lo tocan */
    private void configurarCamposEditables() {
        configurarCampo(binding.inputNombre);
        configurarCampo(binding.inputCorreo);
        configurarCampo(binding.inputTelefono);
        configurarCampo(binding.inputDni);
        configurarCampo(binding.inputFechaNacmiento);
        configurarCampo(binding.inputDomicilio);
    }

    private void configurarCampo(TextInputEditText campo) {
        campo.setOnClickListener(v -> {
            campo.setEnabled(true);
            campo.setTextColor(Color.BLACK);
        });
    }

    /** Guarda los datos en Firestore */
    private void guardarCambios() {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("displayName", binding.inputNombre.getText().toString());
        cambios.put("email", binding.inputCorreo.getText().toString());
        cambios.put("phone", binding.inputTelefono.getText().toString());
        cambios.put("dni", binding.inputDni.getText().toString());
        cambios.put("fechaNacimiento", binding.inputFechaNacmiento.getText().toString());
        cambios.put("domicilio", binding.inputDomicilio.getText().toString());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update(cambios)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                );
    }
}