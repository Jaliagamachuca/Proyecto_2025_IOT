package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerClientesBinding;
import com.example.proyecto_2025.databinding.ActivitySuperadminVerGuiaTurismoBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Superadmin_Ver_Cliente extends AppCompatActivity {

    private ActivitySuperadminVerClientesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVerClientesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Employee employee = (Employee) intent.getSerializableExtra("employee");

        if (employee != null) {
            // 🔹 Asignar valores dinámicos
            binding.inputNombre.setText(employee.getFirstName());
            binding.inputApellidos.setText(employee.getLastName());
            binding.inputDni.setText(employee.getJobId()); // ajusta a tu modelo real
            // binding.inputFechaNacimiento.setText(employee.getBirthDate()); // idem si tienes campo
            binding.inputCorreo.setText(employee.getEmail());
            binding.inputTelefono.setText(employee.getPhoneNumber());
            binding.inputDomicilio.setText(String.valueOf(employee.getSalary()));
        }

        // 🔹 Configurar el botón según condición
        if (employee != null) {
            if (employee.getSalary() >= 10000) {
                binding.btnActivarCliente.setText("DESACTIVAR");
                binding.btnActivarCliente.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.holo_red_dark)
                );
                binding.btnActivarCliente.setOnClickListener(v -> mostrarDialogDesactivar(employee));
            } else {
                binding.btnActivarCliente.setText("ACTIVAR");
                binding.btnActivarCliente.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.holo_green_dark)
                );
                binding.btnActivarCliente.setOnClickListener(v -> mostrarDialogActivar(employee));
            }
        }
    }

    private void mostrarDialogActivar(Employee empleado) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Activar Cliente")
                .setMessage("¿Está seguro de activar al usuario " + empleado.getFirstName() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) ->
                        Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) ->
                        Log.d("msg-test", "Usuario activado: " + empleado.getFirstName()))
                .show();
    }

    private void mostrarDialogDesactivar(Employee empleado) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Desactivar Cliente")
                .setMessage("¿Está seguro de desactivar al usuario " + empleado.getFirstName() + "?")
                .setNeutralButton(R.string.cancel, (dialog, i) ->
                        Log.d("msg-test", "cancelado"))
                .setPositiveButton(R.string.ok, (dialog, i) ->
                        Log.d("msg-test", "Usuario desactivado: " + empleado.getFirstName()))
                .show();
    }
}