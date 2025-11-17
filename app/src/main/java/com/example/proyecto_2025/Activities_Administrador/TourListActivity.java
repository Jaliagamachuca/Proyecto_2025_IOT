package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.TourAdapter;
import com.example.proyecto_2025.data.repository.TourRepository;
import com.example.proyecto_2025.databinding.ActivityTourListBinding;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;

import java.util.ArrayList;
import java.util.List;

public class TourListActivity extends AppCompatActivity {

    private ActivityTourListBinding binding;
    private TourRepository repo;
    private final List<Tour> data = new ArrayList<>();
    private TourAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Tus Tours");

        repo = new TourRepository(this);
        setupRecycler();
        setupFilters();

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, TourFormActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        load(TourEstado.values()); // por defecto todos
    }

    private void setupRecycler() {
        adapter = new TourAdapter(data, this::openDetail, this::openMore);
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.setAdapter(adapter);
    }

    private void setupFilters() {
        // chips simples (ids en el layout)
        binding.chipTodos.setOnClickListener(v -> load(TourEstado.values()));
        binding.chipBorrador.setOnClickListener(v -> load(TourEstado.BORRADOR));
        binding.chipPendiente.setOnClickListener(v -> load(TourEstado.PENDIENTE_GUIA));
        binding.chipPublicado.setOnClickListener(v -> load(TourEstado.PUBLICADO));
        binding.chipEnCurso.setOnClickListener(v -> load(TourEstado.EN_CURSO));
        binding.chipFinalizado.setOnClickListener(v -> load(TourEstado.FINALIZADO));
    }

    private void load(TourEstado... estados) {
        List<Tour> all = repo.findAll();

        // UID del admin actual (lo usamos como empresaId)
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        String empresaIdActual = fbUser != null ? fbUser.getUid() : null;

        data.clear();
        outer:
        for (Tour t : all) {
            // Filtrar por empresaId si está seteado
            if (empresaIdActual != null && t.empresaId != null
                    && !empresaIdActual.equals(t.empresaId)) {
                continue; // tour de otra empresa
            }

            for (TourEstado e : estados) {
                if (t.estado == e) {
                    data.add(t);
                    continue outer;
                }
            }
        }

        binding.empty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }


    private void openDetail(Tour t) {
        Intent i = new Intent(this, TourDetalleActivity.class);
        i.putExtra("id", t.id);
        startActivity(i);
    }

    private void openMore(Tour t, View anchor) {
        PopupMenu pm = new PopupMenu(this, anchor);
        MenuInflater mi = pm.getMenuInflater();
        mi.inflate(R.menu.menu_tour_item, pm.getMenu());
        pm.setOnMenuItemClickListener(item -> onMoreItem(item, t));
        pm.show();
    }

    private boolean onMoreItem(MenuItem item, Tour t) {
        int id = item.getItemId();
        if (id == R.id.action_duplicate) {
            Tour clone = new Tour();
            clone.titulo = t.titulo + " (copia)";
            clone.descripcionCorta = t.descripcionCorta;
            clone.precioPorPersona = t.precioPorPersona;
            clone.cupos = t.cupos;
            clone.idiomas = new ArrayList<>(t.idiomas);
            clone.servicios = new ArrayList<>(t.servicios);
            clone.imagenUris = new ArrayList<>(t.imagenUris);
            clone.ruta = new ArrayList<>(t.ruta);

            clone.setIncluyeDesayuno(t.isIncluyeDesayuno());
            clone.setIncluyeAlmuerzo(t.isIncluyeAlmuerzo());
            clone.setIncluyeCena(t.isIncluyeCena());

            repo.upsert(clone);
            load(TourEstado.values());
            return true;
        } else if (id == R.id.action_delete) {
            repo.delete(t.id);
            load(TourEstado.values());
            return true;
        }
        return false;
    }
}
