package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.TourRepository;
import com.example.proyecto_2025.databinding.ActivityTourDetalleBinding;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.android.material.snackbar.Snackbar;

public class TourDetalleActivity extends AppCompatActivity {

    private ActivityTourDetalleBinding binding;
    private TourRepository repo;
    private Tour tour;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourDetalleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Detalle del tour");

        repo = new TourRepository(this);
        String id = getIntent().getStringExtra("id");
        tour = repo.findById(id);
        if (tour == null) { finish(); return; }

        binding.tvTitulo.setText(tour.titulo);
        binding.tvEstado.setText(tour.estado.name().replace("_"," "));
        binding.tvPrecio.setText(tour.precioTexto());
        binding.tvDesc.setText(tour.descripcionCorta);
        binding.tvCupos.setText(String.valueOf(tour.cupos));
        binding.tvRuta.setText(tour.ruta.size()+" puntos");

        binding.btnPublicar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.PENDIENTE_GUIA) {
                // simula aceptación del guía (en tu app real esto vendrá del lado guía)
                tour.estado = TourEstado.PUBLICADO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_"," "));
                Snackbar.make(binding.getRoot(), "Tour publicado", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(binding.getRoot(), "Requiere estado Pendiente de guía", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.btnEmpezar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.PUBLICADO) {
                tour.estado = TourEstado.EN_CURSO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_"," "));
            }
        });

        binding.btnFinalizar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.EN_CURSO) {
                tour.estado = TourEstado.FINALIZADO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_"," "));
            }
        });
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tour_detalle, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            // TODO: abrir edición (puedes reutilizar el form con el ID)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
