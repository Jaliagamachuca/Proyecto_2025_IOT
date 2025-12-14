package com.example.proyecto_2025.Activities_Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityToursPorEmpresaBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ToursPorEmpresaActivity extends AppCompatActivity {

    private ActivityToursPorEmpresaBinding binding;
    private FirebaseFirestore db;

    private ToursEmpresaAdapter adapter;
    private final List<TourItem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToursPorEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Toolbar + back
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String empresaId = getIntent().getStringExtra("empresaId");
        String empresaNombre = getIntent().getStringExtra("empresaNombre");

        if (empresaNombre != null && !empresaNombre.isEmpty()) {
            binding.toolbar.setTitle("Tours de " + empresaNombre);
        } else {
            binding.toolbar.setTitle("Tours de la empresa");
        }

        if (empresaId == null || empresaId.isEmpty()) {
            Toast.makeText(this, "Empresa inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.rvTours.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ToursEmpresaAdapter(data, tour -> {
            // Abrir detalle del tour (usa el tuyo si ya tienes un detalle para cliente)
            // Por ahora, abre el detalle de admin SOLO si te sirve para demo (no recomendado).
            Intent i = new Intent(this, com.example.proyecto_2025.Activities_Administrador.TourDetalleActivity.class);
            i.putExtra("id", tour.id);
            startActivity(i);
        });
        binding.rvTours.setAdapter(adapter);

        cargarTours(empresaId);
    }

    private void cargarTours(@NonNull String empresaId) {
        binding.progress.setVisibility(View.VISIBLE);
        binding.tvVacio.setVisibility(View.GONE);

        db.collection("tours")
                .whereEqualTo("empresaId", empresaId)
                .whereEqualTo("estado", "PUBLICADO") // IMPORTANT: solo publicados
                .get()
                .addOnSuccessListener(snaps -> {
                    data.clear();

                    for (DocumentSnapshot d : snaps.getDocuments()) {
                        TourItem t = new TourItem();
                        t.id = d.getString("id");
                        if (t.id == null || t.id.isEmpty()) t.id = d.getId();

                        t.titulo = d.getString("titulo");
                        if (t.titulo == null) t.titulo = d.getString("nombre"); // por si tu campo se llama distinto

                        t.descripcionCorta = d.getString("descripcionCorta");

                        Double precio = d.getDouble("precio");
                        t.precio = (precio != null) ? precio : 0.0;

                        Long cupos = d.getLong("cupos");
                        t.cupos = (cupos != null) ? cupos.intValue() : 0;

                        data.add(t);
                    }

                    adapter.notifyDataSetChanged();

                    binding.progress.setVisibility(View.GONE);
                    binding.tvVacio.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(err -> {
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Error cargando tours: " + err.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // DTO simple para esta pantalla
    public static class TourItem {
        public String id;
        public String titulo;
        public String descripcionCorta;
        public double precio;
        public int cupos;
    }
}
