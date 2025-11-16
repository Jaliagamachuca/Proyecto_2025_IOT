package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaRegistrarCheckInBinding;
import com.example.proyecto_2025.databinding.ActivityGuiaRegistrarProgresoBinding;

import java.util.ArrayList;

public class Guia_Registrar_Progreso extends AppCompatActivity {

    private ActivityGuiaRegistrarProgresoBinding binding;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaRegistrarProgresoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            Toast.makeText(this, "No se pudo cargar el tour", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configurar RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        UbicacionAdapter adapter = new UbicacionAdapter(
                new ArrayList<>(tour.getUbicaciones()),
                ubicacion -> {
                    // Acción cuando se presiona el botón por cada ubicación
                    Toast.makeText(this,
                            "Registrado en " + ubicacion.getNombre(),
                            Toast.LENGTH_SHORT).show();
                }
        );

        binding.recyclerView.setAdapter(adapter);
    }
}
