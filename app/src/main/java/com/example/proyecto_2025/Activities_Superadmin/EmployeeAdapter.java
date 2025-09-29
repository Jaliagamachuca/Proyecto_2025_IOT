package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.IrvEmployeeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> listaEmpleados;
    private Context context;

    private static String TAG = "msg-test-EmployeeViewHolder";

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        IrvEmployeeBinding binding = IrvEmployeeBinding.inflate(inflater, parent, false);
        return new EmployeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = listaEmpleados.get(position);
        holder.employee = employee;

        // Mostrar nombre completo
        String fullName = employee.getFirstName() + " " + employee.getLastName();
        holder.binding.textViewFullName.setText(fullName);

        // 🔹 Botón "Ver información"
        holder.binding.buttonInformacion.setOnClickListener(view -> {
            Intent intent;

            double salario = employee.getSalary();
            if (salario <= 8000) {
                intent = new Intent(context, Superadmin_Ver_Administrador.class);
            } else if (salario <= 10000) {
                intent = new Intent(context, Superadmin_Ver_Guia_Turismo.class);
            } else {
                intent = new Intent(context, Superadmin_Ver_Cliente.class);
            }

            intent.putExtra("employee", employee);
            context.startActivity(intent);
        });

        // 🔹 Lógica del botón según salario
        if (employee.getSalary() >= 10000) {
            holder.binding.buttonActivar.setText("DESACTIVAR");
            holder.binding.buttonActivar.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            holder.binding.buttonActivar.setOnClickListener(v -> mostrarDialogDesactivar(employee));
        } else {
            holder.binding.buttonActivar.setText("ACTIVAR");
            holder.binding.buttonActivar.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            holder.binding.buttonActivar.setOnClickListener(v -> mostrarDialogActivar(employee));
        }
    }

    @Override
    public int getItemCount() {
        return listaEmpleados.size();
    }

    public List<Employee> getListaEmpleados() {
        return listaEmpleados;
    }

    public void setListaEmpleados(List<Employee> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        IrvEmployeeBinding binding;
        Employee employee;

        public EmployeeViewHolder(IrvEmployeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private void mostrarDialogActivar(Employee empleado) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Activar Administrador");
        dialogBuilder.setMessage("¿Está seguro de activar al usuario " + empleado.getFirstName() + "?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) ->
                Log.d("msg-test", "btn neutral")
        );
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                Log.d("msg-test", "Usuario activado: " + empleado.getFirstName())
        );
        dialogBuilder.show();
    }

    private void mostrarDialogDesactivar(Employee empleado) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Desactivar Administrador");
        dialogBuilder.setMessage("¿Está seguro de desactivar al usuario " + empleado.getFirstName() + "?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) ->
                Log.d("msg-test", "btn neutral")
        );
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                Log.d("msg-test", "Usuario desactivado: " + empleado.getFirstName())
        );
        dialogBuilder.show();
    }
}
