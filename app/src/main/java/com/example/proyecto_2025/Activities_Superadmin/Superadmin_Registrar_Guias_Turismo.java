package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_2025.adapter.GuideRequestAdapter;
import com.example.proyecto_2025.databinding.ActivitySuperadminRegistrarGuiasTurismoBinding;
import com.example.proyecto_2025.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Superadmin_Registrar_Guias_Turismo extends AppCompatActivity {

    private ActivitySuperadminRegistrarGuiasTurismoBinding binding;
    private FirebaseFirestore db;

    private GuideRequestAdapter adapter;
    private final List<User> listaOriginal = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRegistrarGuiasTurismoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Recycler
        adapter = new GuideRequestAdapter(this, guia -> {
            // Abrir pantalla de revisión / edición
            Intent i = new Intent(this, Superadmin_Registrar_Guia_Turismo.class);
            i.putExtra("user", guia);
            startActivity(i);
        });

        binding.rvSolicitudesGuias.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSolicitudesGuias.setAdapter(adapter);

        // Búsqueda
        binding.etBuscarGuiaSolicitud.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarSolicitudes();
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarSolicitudes();
    }


    private void cargarSolicitudes() {
        db.collection("users")
                .whereEqualTo("role", "guia")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snaps -> {
                    listaOriginal.clear();
                    for (QueryDocumentSnapshot d : snaps) {
                        User u = d.toObject(User.class);
                        if (u.getUid() == null) {
                            u.setUid(d.getId());
                        }
                        listaOriginal.add(u);
                    }
                    actualizarVista();
                })
                .addOnFailureListener(e -> {
                    listaOriginal.clear();
                    actualizarVista();
                });
    }

    private void actualizarVista() {
        if (listaOriginal.isEmpty()) {
            binding.tvEmptySolicitudes.setVisibility(View.VISIBLE);
            adapter.setItems(new ArrayList<>());
        } else {
            binding.tvEmptySolicitudes.setVisibility(View.GONE);
            adapter.setItems(listaOriginal);
        }
    }

    private void filtrar(String q) {
        if (q == null || q.trim().isEmpty()) {
            adapter.setItems(listaOriginal);
            return;
        }
        String query = q.toLowerCase();
        List<User> filtrados = new ArrayList<>();
        for (User u : listaOriginal) {
            String nombre = u.getNombreCompleto() == null ? "" : u.getNombreCompleto().toLowerCase();
            String correo = u.getEmail() == null ? "" : u.getEmail().toLowerCase();
            if (nombre.contains(query) || correo.contains(query)) {
                filtrados.add(u);
            }
        }
        adapter.setItems(filtrados);
    }
}
