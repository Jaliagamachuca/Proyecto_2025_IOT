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

import com.example.proyecto_2025.databinding.IrvEmployeeBinding;

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

        if (position % 2.0 == 1) {
            holder.binding.getRoot().setBackgroundColor(0xF3E5F5FF);
        } else {
            holder.binding.getRoot().setBackgroundColor(Color.WHITE); // o el color que uses normalmente
        }

        TextView tvFirstName = holder.binding.textViewFirstName;
        TextView tvLastName = holder.binding.textViewLastName;
        TextView tvSalary = holder.binding.textViewSalary;

        tvFirstName.setText(employee.getFirstName());
        tvLastName.setText(employee.getLastName());
        tvSalary.setText(" S/. " + String.valueOf(employee.getSalary()));

        if (employee.getSalary() >= 10000) {
            tvSalary.setTextColor(Color.RED);
        } else {
            tvSalary.setTextColor(Color.BLACK);
        }

        holder.binding.buttonInformacion.setOnClickListener(view -> {
            String id = employee.getId();
            Log.d(TAG, "Presionando el empleado con id: " + id);

            Intent intent = new Intent(context, EmployeeDetailActivity.class);
            intent.putExtra("employee", employee);

            context.startActivity(intent);
        });
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
}
