package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.databinding.ActivityGuiaRegistrarCheckOutBinding;

import java.util.List;

public class Guia_Registrar_CheckOut extends AppCompatActivity {

    private ActivityGuiaRegistrarCheckOutBinding binding;
    private Tour tour;
    private List<String> listaUsuarios;
    private CheckOutUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaRegistrarCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1️⃣ Recibir el tour
        tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour == null) {
            finish();
            return;
        }

        // 2️⃣ Obtener usuarios del tour
        listaUsuarios = tour.getUsuarios();

        // 3️⃣ Configurar RecyclerView
        adapter = new CheckOutUserAdapter(listaUsuarios, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
}
