package com.example.proyecto_2025.Activities_Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.databinding.ActivityToursPorEmpresaBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ToursPorEmpresaActivity extends AppCompatActivity {

    private static final String TAG = "TOURS_EMPRESA";

    private ActivityToursPorEmpresaBinding binding;
    private FirebaseFirestore db;

    private ToursEmpresaAdapter adapter; // por ahora usas tu adapter actual
    private final List<TourItem> data = new ArrayList<>();

    // ✅ ÚNICO DTO (sin duplicados)
    public static class TourItem {
        public String id;
        public String titulo;
        public String descripcionCorta;
        public double precio;
        public int cupos;
        public long fechaInicioUtc;
        public String imagenUrl;  // portada
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToursPorEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String empresaDocId   = intent.getStringExtra("empresaDocId");
        String empresaAdminId = intent.getStringExtra("empresaAdminId");
        String empresaNombre  = intent.getStringExtra("empresaNombre");

        Log.d(TAG, "extras empresaDocId=" + empresaDocId + " empresaAdminId=" + empresaAdminId + " empresaNombre=" + empresaNombre);

        binding.toolbar.setTitle((empresaNombre != null && !empresaNombre.trim().isEmpty())
                ? "Tours de " + empresaNombre
                : "Tours de la empresa");

        if ((empresaDocId == null || empresaDocId.trim().isEmpty()) &&
                (empresaAdminId == null || empresaAdminId.trim().isEmpty())) {
            Toast.makeText(this, "Empresa inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.rvTours.setLayoutManager(new LinearLayoutManager(this));

        // ✅ De momento dejo tu adapter existente (pero ya recibirá TourItem con imagenUrl y fechaInicioUtc)
        adapter = new ToursEmpresaAdapter(data, new ToursEmpresaAdapter.Listener() {
            @Override
            public void onVerDetalle(TourItem tour) {
                // aquí abre un detalle CLIENTE si tienes, o muestra modal
                Toast.makeText(ToursPorEmpresaActivity.this, tour.titulo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReservar(TourItem tour) {
                // aquí llamas tu lógica real de ReservaRepository
                reservarTour(tour);
            }
        });


        binding.rvTours.setAdapter(adapter);

        cargarToursConFallback(empresaDocId, empresaAdminId);
    }
    private void reservarTour(TourItem tour) {
        Toast.makeText(
                this,
                "Reserva solicitada: " + (tour.titulo != null ? tour.titulo : "Tour"),
                Toast.LENGTH_SHORT
        ).show();

        // Aquí luego conectamos ReservaRepository o Firestore
    }

    private void cargarToursConFallback(String empresaDocId, String empresaAdminId) {
        binding.progress.setVisibility(View.VISIBLE);
        binding.tvVacio.setVisibility(View.GONE);

        ErrCb errCb = this::showError;

        if (empresaDocId != null && !empresaDocId.trim().isEmpty()) {
            Log.d(TAG, "Query por empresaId=empresaDocId: " + empresaDocId);

            queryToursByEmpresaId(empresaDocId, list -> {
                if (!list.isEmpty()) {
                    applyList(list);
                } else {
                    if (empresaAdminId != null && !empresaAdminId.trim().isEmpty()) {
                        Log.d(TAG, "Fallback query por empresaId=empresaAdminId: " + empresaAdminId);
                        queryToursByEmpresaId(empresaAdminId, this::applyList, errCb);
                    } else {
                        applyList(list);
                    }
                }
            }, errCb);

        } else {
            Log.d(TAG, "Query directo por empresaId=empresaAdminId: " + empresaAdminId);
            queryToursByEmpresaId(empresaAdminId, this::applyList, errCb);
        }
    }

    private interface ListCb { void onDone(List<TourItem> list); }
    private interface ErrCb { void onErr(Exception e); }

    @SuppressWarnings("unchecked")
    private void queryToursByEmpresaId(@NonNull String empresaId, @NonNull ListCb cb, @NonNull ErrCb errCb) {
        db.collection("tours")
                .whereEqualTo("empresaId", empresaId)
                .whereEqualTo("estado", "PUBLICADO")
                .get()
                .addOnSuccessListener(snaps -> {
                    List<TourItem> list = new ArrayList<>();
                    Log.d(TAG, "Resultados=" + snaps.size() + " para empresaId=" + empresaId);

                    for (DocumentSnapshot d : snaps.getDocuments()) {
                        TourItem t = new TourItem();
                        @SuppressWarnings("unchecked")
                        List<String> imgs = (List<String>) d.get("imagenUris");
                        t.imagenUrl = (imgs != null && !imgs.isEmpty()) ? imgs.get(0) : null;

                        // id
                        t.id = d.getString("id");
                        if (t.id == null || t.id.trim().isEmpty()) t.id = d.getId();

                        // titulo
                        t.titulo = d.getString("titulo");
                        if (t.titulo == null) t.titulo = d.getString("nombre");

                        // descripcion
                        t.descripcionCorta = d.getString("descripcionCorta");

                        // precio
                        Double precio = d.getDouble("precioPorPersona");
                        if (precio == null) precio = d.getDouble("precio");
                        t.precio = (precio != null) ? precio : 0.0;

                        // cupos
                        Long cupos = d.getLong("cupos");
                        t.cupos = (cupos != null) ? cupos.intValue() : 0;

                        // ✅ fechaInicioUtc
                        Long f = d.getLong("fechaInicioUtc");
                        t.fechaInicioUtc = (f != null) ? f : 0L;



                        Log.d(TAG, "tour id=" + t.id + " titulo=" + t.titulo + " img=" + (t.imagenUrl != null));

                        list.add(t);
                    }
                    cb.onDone(list);
                })
                .addOnFailureListener(errCb::onErr);
    }

    private void applyList(List<TourItem> list) {
        data.clear();
        data.addAll(list);
        adapter.notifyDataSetChanged();

        binding.progress.setVisibility(View.GONE);
        binding.tvVacio.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showError(Exception err) {
        Log.e(TAG, "Error cargando tours", err);
        binding.progress.setVisibility(View.GONE);
        Toast.makeText(this, "Error cargando tours: " + err.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
