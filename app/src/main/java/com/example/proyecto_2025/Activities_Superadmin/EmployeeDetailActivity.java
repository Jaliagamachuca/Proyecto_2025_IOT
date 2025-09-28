package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityEmployeeDetailBinding;

public class EmployeeDetailActivity extends AppCompatActivity {

    ActivityEmployeeDetailBinding binding;

    private static String TAG = "msg-test-EmployeeDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        Employee employee = (Employee) intent.getSerializableExtra("employee");
        binding.textViewDetailFirstName.setText("  " + employee.getFirstName());
        binding.textViewDetailLastName.setText("  " + employee.getLastName());
        binding.textViewDetailSalary.setText(" S/. " + employee.getSalary());
        binding.textViewDetailEmail.setText("  " + employee.getEmail());
        binding.textViewDetailPhoneNumber.setText("  " + employee.getPhoneNumber());

        Log.d(TAG, "Presionando el empleado con id: " + employee.getId());

    }
}