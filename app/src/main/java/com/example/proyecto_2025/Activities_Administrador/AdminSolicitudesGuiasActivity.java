package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminSolicitudesGuiasActivity extends AppCompatActivity {

    private RecyclerView rv;
    private AdminSolicitudesAdapter adapter;
    private final List<Tour> data = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solicitudes_guias);

        rv = findViewById(R.id.rvSolicitudesGuias);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminSolicitudesAdapter(this, data);
        rv.setAdapter(adapter);

        escucharSolicitudes();
    }

    private void escucharSolicitudes() {
        db.collection("tours")
                .whereEqualTo("estado", "SOLICITADO")
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    data.clear();
                    for (var doc : snap.getDocuments()) {
                        Tour t = doc.toObject(Tour.class);
                        if (t != null) {
                            t.id = doc.getId();
                            data.add(t);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
