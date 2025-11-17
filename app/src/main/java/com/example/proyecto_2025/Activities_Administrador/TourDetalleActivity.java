package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.repository.TourRepository;
import com.example.proyecto_2025.databinding.ActivityTourDetalleBinding;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.android.material.snackbar.Snackbar;

public class TourDetalleActivity extends AppCompatActivity {

    private ActivityTourDetalleBinding binding;
    private TourRepository repo;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourDetalleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (tour.estado == TourEstado.BORRADOR) {
            binding.btnPublicar.setText("Enviar a guías");
        } else if (tour.estado == TourEstado.PENDIENTE_GUIA) {
            binding.btnPublicar.setText("Publicar a clientes");
        } else {
            binding.btnPublicar.setText("Publicar");
            binding.btnPublicar.setEnabled(false); // ya no tiene sentido publicar de nuevo
        }
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalle del tour");
        }

        repo = new TourRepository(this);

        String id = getIntent().getStringExtra("id");
        tour = repo.findById(id);
        if (tour == null) {
            finish();
            return;
        }

        // Datos básicos
        binding.tvTitulo.setText(tour.titulo);
        binding.tvEstado.setText(tour.estado != null
                ? tour.estado.name().replace("_", " ")
                : "Sin estado");
        binding.tvPrecio.setText(tour.precioTexto());
        binding.tvDesc.setText(tour.descripcionCorta);
        binding.tvCupos.setText(String.valueOf(tour.cupos));
        binding.tvRuta.setText(tour.ruta == null
                ? "0 puntos"
                : (tour.ruta.size() + " puntos"));

        // ===== FECHA + HORA A PARTIR DE fechaInicioUtc / fechaFinUtc =====
        java.text.SimpleDateFormat fmtFecha = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        java.text.SimpleDateFormat fmtHora  = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        if (tour.fechaInicioUtc > 0 && tour.fechaFinUtc > 0) {
            java.util.Date dIni = new java.util.Date(tour.fechaInicioUtc);
            java.util.Date dFin = new java.util.Date(tour.fechaFinUtc);

            // Estos TextView deben existir en tu XML de detalle
            binding.tvFechas.setText(fmtFecha.format(dIni) + " - " + fmtFecha.format(dFin));
            binding.tvHorario.setText(fmtHora.format(dIni) + " - " + fmtHora.format(dFin));
        } else {
            binding.tvFechas.setText("Sin fecha definida");
            binding.tvHorario.setText("Sin horario definido");
        }
// ===== FIN FECHA + HORA =====


        // ===== NUEVO: Servicios adicionales (desayuno / almuerzo / cena) =====
        StringBuilder sb = new StringBuilder();

        if (Boolean.TRUE.equals(tour.isIncluyeDesayuno())) {
            sb.append("• Desayuno incluido\n");
        }
        if (Boolean.TRUE.equals(tour.isIncluyeAlmuerzo())) {
            sb.append("• Almuerzo incluido\n");
        }
        if (Boolean.TRUE.equals(tour.isIncluyeCena())) {
            sb.append("• Cena incluida\n");
        }

        String serviciosText = (sb.length() == 0)
                ? "Sin servicios adicionales"
                : sb.toString().trim();

        binding.tvServiciosExtra.setText(serviciosText);
        // ===== FIN NUEVO =====

        // Botón publicar: BORRADOR → PENDIENTE_GUIA → PUBLICADO
        binding.btnPublicar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.BORRADOR) {
                // 1) Enviar a bolsa de guías
                tour.estado = TourEstado.PENDIENTE_GUIA;
                repo.upsert(tour);

                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
                binding.btnPublicar.setText("Publicar a clientes");

                Snackbar.make(binding.getRoot(),
                        "Tour enviado a la bolsa de guías",
                        Snackbar.LENGTH_LONG).show();

            } else if (tour.estado == TourEstado.PENDIENTE_GUIA) {
                // 2) Publicar a clientes SOLO si ya hay guía asignado
                if (tour.guiaId == null || tour.guiaId.isEmpty()) {
                    Snackbar.make(binding.getRoot(),
                            "Aún no hay guía asignado. No puedes publicar a clientes.",
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                tour.estado = TourEstado.PUBLICADO;
                repo.upsert(tour);

                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
                Snackbar.make(binding.getRoot(),
                        "Tour publicado para clientes",
                        Snackbar.LENGTH_LONG).show();

                binding.btnPublicar.setEnabled(false);

            } else {
                Snackbar.make(binding.getRoot(),
                        "Este tour ya no puede cambiarse desde aquí.",
                        Snackbar.LENGTH_LONG).show();
            }
        });


        // Botón empezar
        binding.btnEmpezar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.PUBLICADO) {
                tour.estado = TourEstado.EN_CURSO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
            } else {
                Snackbar.make(binding.getRoot(), "Solo puedes empezar tours publicados", Snackbar.LENGTH_LONG).show();
            }
        });

        // Botón finalizar
        binding.btnFinalizar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.EN_CURSO) {
                tour.estado = TourEstado.FINALIZADO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
            } else {
                Snackbar.make(binding.getRoot(), "Solo puedes finalizar tours en curso", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tour_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            // TODO: abrir edición (puedes reutilizar el form con el ID)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
