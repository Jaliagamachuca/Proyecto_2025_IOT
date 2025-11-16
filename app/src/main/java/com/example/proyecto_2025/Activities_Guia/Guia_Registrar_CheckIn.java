package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.Activities_Superadmin.EmployeeAdapter;
import com.example.proyecto_2025.Activities_Superadmin.User;
import com.example.proyecto_2025.Activities_Superadmin.UserRepository;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaRegistrarCheckInBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaTourEnProcesoBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;

import java.util.List;

public class Guia_Registrar_CheckIn extends AppCompatActivity {

    private ActivityGuiaRegistrarCheckInBinding binding;
    private Tour tour;
    private List<String> listaUsuarios; // ← usuarios del tour
    private CheckInUserAdapter adapter; // ← nuestro adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaRegistrarCheckInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1️⃣ Recibir el tour
        tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            finish();
            return;
        }

        // 2️⃣ Obtener la lista de usuarios del tour
        listaUsuarios = tour.getUsuarios(); // ← List<String>

        // 3️⃣ Configurar el RecyclerView
        adapter = new CheckInUserAdapter(listaUsuarios, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
}
