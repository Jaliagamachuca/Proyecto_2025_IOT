package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalle del tour");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
            binding.toolbar.setNavigationOnClickListener(v ->
                    getOnBackPressedDispatcher().onBackPressed()
            );
        }

        repo = new TourRepository(this);

        // 1) Obtener id del intent
        String id = getIntent().getStringExtra("id");
        if (id == null || id.isEmpty()) {
            finish();
            return;
        }

        // 2) Cargar tour desde repo
        tour = repo.findById(id);
        if (tour == null) {
            finish();
            return;
        }

        // ===== Carrusel de imágenes =====
        if (tour.imagenUris != null && !tour.imagenUris.isEmpty()) {
            binding.rvImagenes.setVisibility(View.VISIBLE);
            binding.tvSinImagenes.setVisibility(View.GONE);

            // Convertir List<String> -> List<Uri>
            java.util.List<android.net.Uri> uris = new java.util.ArrayList<>();
            for (String s : tour.imagenUris) {
                try {
                    uris.add(android.net.Uri.parse(s));
                } catch (Exception ignored) {}
            }

            androidx.recyclerview.widget.LinearLayoutManager lm =
                    new androidx.recyclerview.widget.LinearLayoutManager(
                            this,
                            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                            false
                    );
            binding.rvImagenes.setLayoutManager(lm);

            com.example.proyecto_2025.adapter.ImageUriAdapter adapter =
                    new com.example.proyecto_2025.adapter.ImageUriAdapter(
                            uris,
                            null // sin eliminación en detalle
                    );
            binding.rvImagenes.setAdapter(adapter);

            // Opcional: efecto "una imagen por pantalla"
            androidx.recyclerview.widget.SnapHelper snapHelper =
                    new androidx.recyclerview.widget.PagerSnapHelper();
            snapHelper.attachToRecyclerView(binding.rvImagenes);

        } else {
            binding.rvImagenes.setVisibility(View.GONE);
            binding.tvSinImagenes.setVisibility(View.VISIBLE);
        }

        // 3) Configurar texto del botón según estado
        if (tour.estado == TourEstado.BORRADOR) {
            binding.btnPublicar.setText("Enviar a guías");
            binding.btnPublicar.setEnabled(true);

        } else if (tour.estado == TourEstado.SOLICITADO) {
            binding.btnPublicar.setText("Aceptar guía");
            binding.btnPublicar.setEnabled(true);

        } else if (tour.estado == TourEstado.PENDIENTE_GUIA) {
            // aquí puede ser: aún sin guía (recién enviado) o ya con guía (aceptado)
            if (tour.guiaId == null || tour.guiaId.isEmpty()) {
                binding.btnPublicar.setText("Esperando guía...");
                binding.btnPublicar.setEnabled(false);
            } else {
                binding.btnPublicar.setText("Publicar a clientes");
                binding.btnPublicar.setEnabled(true);
            }

        } else {
            binding.btnPublicar.setText("Publicar");
            binding.btnPublicar.setEnabled(false);
        }

        // ===== Datos básicos =====
        binding.tvTitulo.setText(tour.titulo);
        binding.tvEstado.setText(tour.estado != null
                ? tour.estado.name().replace("_", " ")
                : "Sin estado");
        binding.tvPrecio.setText(tour.precioTexto());
        if (tour.descripcionCorta != null && !tour.descripcionCorta.isEmpty()) {
            binding.tvDesc.setText(tour.descripcionCorta);
        } else {
            binding.tvDesc.setText("Sin descripción disponible");
        }

        // Cupos
        binding.tvCupos.setText("Cupos disponibles: " + tour.cupos);

        // ===== Ruta: cantidad + detalle =====
        int nPuntos = (tour.ruta == null) ? 0 : tour.ruta.size();
        binding.tvRuta.setText("Puntos en la ruta: " + nPuntos);

        if (nPuntos == 0) {
            binding.tvListaPuntos.setText("Sin puntos definidos");
            binding.btnVerMapa.setVisibility(View.GONE);
        } else {
            StringBuilder sbRuta = new StringBuilder();
            for (int i = 0; i < tour.ruta.size(); i++) {
                com.example.proyecto_2025.model.PuntoRuta p = tour.ruta.get(i);
                sbRuta.append(i + 1).append(". ");

                if (p.nombre != null && !p.nombre.isEmpty()) {
                    sbRuta.append(p.nombre);
                } else {
                    sbRuta.append("Punto sin nombre");
                }

                // Minutos estimados
                if (p.minutosEstimados > 0) {
                    sbRuta.append(" (").append(p.minutosEstimados).append(" min)");
                }

                // Coordenadas
                sbRuta.append("\n   lat: ").append(p.lat)
                        .append("  lon: ").append(p.lon)
                        .append("\n\n");
            }
            binding.tvListaPuntos.setText(sbRuta.toString().trim());
            binding.btnVerMapa.setVisibility(View.VISIBLE);
        }

        // ===== Botón "Ver ruta en mapa" =====
        binding.btnVerMapa.setOnClickListener(v -> {
            if (tour.ruta == null || tour.ruta.isEmpty()) {
                return;
            }

            android.content.Intent intent =
                    new android.content.Intent(this, TourRutaMapActivity.class);

            String jsonRuta = new com.google.gson.Gson().toJson(tour.ruta);
            intent.putExtra("ruta_json", jsonRuta);
            intent.putExtra("titulo", tour.titulo);

            startActivity(intent);
        });


        // ===== Fechas y horas =====
        java.text.SimpleDateFormat fmtFecha =
                new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        java.text.SimpleDateFormat fmtHora  =
                new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        if (tour.fechaInicioUtc > 0 && tour.fechaFinUtc > tour.fechaInicioUtc) {
            java.util.Date dIni = new java.util.Date(tour.fechaInicioUtc);
            java.util.Date dFin = new java.util.Date(tour.fechaFinUtc);

            // Fechas: "Del 03/12/2025 al 05/12/2025"
            binding.tvFechas.setText(
                    "Del " + fmtFecha.format(dIni) + " al " + fmtFecha.format(dFin));

            // Horario diario: "Horario: 15:00 - 21:00"
            binding.tvHorario.setText(
                    "Horario diario: " + fmtHora.format(dIni) + " - " + fmtHora.format(dFin));

            // Duración total en días/horas/minutos
            long diffMillis = tour.fechaFinUtc - tour.fechaInicioUtc;
            long totalMin   = diffMillis / (1000 * 60);

            long dias   = totalMin / (60 * 24);
            long resto  = totalMin % (60 * 24);
            long horas  = resto / 60;
            long minutos= resto % 60;

            StringBuilder dur = new StringBuilder("Duración total: ");
            if (dias > 0) {
                dur.append(dias).append(dias == 1 ? " día" : " días");
            }
            if (horas > 0) {
                if (dur.length() > "Duración total: ".length()) dur.append(", ");
                dur.append(horas).append(horas == 1 ? " hora" : " horas");
            }
            if (minutos > 0) {
                if (dur.length() > "Duración total: ".length()) dur.append(", ");
                dur.append(minutos).append(" min");
            }
            if (dur.toString().equals("Duración total: ")) {
                dur.append("no definida");
            }

            binding.tvDuracion.setText(dur.toString());

        } else {
            binding.tvFechas.setText("Fechas por definir");
            binding.tvHorario.setText("Horario por definir");
            binding.tvDuracion.setText("Duración total: no definida");
        }

        // ===== Servicios extra =====
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(tour.isIncluyeDesayuno())) sb.append("• Desayuno incluido\n");
        if (Boolean.TRUE.equals(tour.isIncluyeAlmuerzo())) sb.append("• Almuerzo incluido\n");
        if (Boolean.TRUE.equals(tour.isIncluyeCena())) sb.append("• Cena incluida\n");

        String serviciosText = (sb.length() == 0)
                ? "Sin servicios adicionales"
                : sb.toString().trim();
        binding.tvServiciosExtra.setText(serviciosText);

        // ===== Idiomas =====
        if (tour.idiomas == null || tour.idiomas.isEmpty()) {
            binding.tvIdiomas.setText("Sin idiomas definidos");
        } else {
            binding.tvIdiomas.setText(TextUtils.join(", ", tour.idiomas));
        }


        binding.btnPublicar.setOnClickListener(v -> {

            if (tour.estado == TourEstado.BORRADOR) {
                // 1) Admin: BORRADOR -> PENDIENTE_GUIA (visible para guías)
                tour.estado = TourEstado.PENDIENTE_GUIA;
                repo.upsert(tour);

                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
                binding.btnPublicar.setText("Esperando guía...");
                binding.btnPublicar.setEnabled(false);

                Snackbar.make(binding.getRoot(),
                        "Tour enviado a la bolsa de guías",
                        Snackbar.LENGTH_LONG).show();

            } else if (tour.estado == TourEstado.SOLICITADO) {
                // 2) Admin: Acepta al guía que solicitó
                if (tour.guiaId == null || tour.guiaId.isEmpty()) {
                    Snackbar.make(binding.getRoot(),
                            "No hay guía asociado a esta solicitud.",
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Confirmación admin: pasa a PENDIENTE_GUIA pero ya con guía asignado
                tour.estado = TourEstado.PENDIENTE_GUIA;
                repo.upsert(tour);

                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
                binding.btnPublicar.setText("Publicar a clientes");
                binding.btnPublicar.setEnabled(true);

                Snackbar.make(binding.getRoot(),
                        "Guía aceptado. Ya puedes publicar a clientes.",
                        Snackbar.LENGTH_LONG).show();

            } else if (tour.estado == TourEstado.PENDIENTE_GUIA) {
                // 3) Admin: Publicar a clientes (solo si ya hay guía asignado)
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


        // Empezar
        binding.btnEmpezar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.PUBLICADO) {
                tour.estado = TourEstado.EN_CURSO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
            } else {
                Snackbar.make(binding.getRoot(),
                        "Solo puedes empezar tours publicados",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        // Finalizar
        binding.btnFinalizar.setOnClickListener(v -> {
            if (tour.estado == TourEstado.EN_CURSO) {
                tour.estado = TourEstado.FINALIZADO;
                repo.upsert(tour);
                binding.tvEstado.setText(tour.estado.name().replace("_", " "));
            } else {
                Snackbar.make(binding.getRoot(),
                        "Solo puedes finalizar tours en curso",
                        Snackbar.LENGTH_LONG).show();
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
            // TODO: abrir edición
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

