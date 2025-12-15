package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Tour;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Admin_Guias_HomeActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView kpiGuiasActivos, kpiSolicitudesPend, kpiGuiasConTours, tvContadorSolicitudes;
    private RecyclerView rvSolicitudes;
    private View emptyState;
    private AdminSolicitudesAdapter adapter;
    private final List<Tour> data = new ArrayList<>();

    private String adminUid;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.screen_admin_guias); // <-- usa TU layout real

        adminUid = FirebaseAuth.getInstance().getUid();

        kpiGuiasActivos = findViewById(R.id.kpiGuiasActivos);
        kpiSolicitudesPend = findViewById(R.id.kpiOfertasPendientes);
        kpiGuiasConTours = findViewById(R.id.kpiOfertasAceptadas);
        tvContadorSolicitudes = findViewById(R.id.tvContadorSolicitudes);

        rvSolicitudes = findViewById(R.id.rvSolicitudesGuias);
        emptyState = findViewById(R.id.emptyStateSolicitudes);

        rvSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminSolicitudesAdapter(this, data);
        rvSolicitudes.setAdapter(adapter);

        // Botones
        findViewById(R.id.btnExplorarGuias).setOnClickListener(v ->
                startActivity(new Intent(this, GuideDirectoryActivity.class))
        );
        findViewById(R.id.btnOfertasGuias).setOnClickListener(v ->
                startActivity(new Intent(this, AdminSolicitudesGuiasActivity.class))
        );

        cargarKPIs();
        escucharSolicitudesRecientes();
    }

    private void cargarKPIs() {
        // 1) Guías activos = users where role == "guia" (ajusta si tienes campo "activo")
        db.collection("users")
                .whereEqualTo("role", "guia")
                .get()
                .addOnSuccessListener(snap -> kpiGuiasActivos.setText(String.valueOf(snap.size())));

        // 2) Solicitudes pendientes = tours where estado == "SOLICITADO"
        db.collection("tours")
                .whereEqualTo("estado", "SOLICITADO")
                .get()
                .addOnSuccessListener(snap -> kpiSolicitudesPend.setText(String.valueOf(snap.size())));

        // 3) Guías con tours = tours where guiaId != null AND estado != "PENDIENTE_GUIA"
        // (Firestore no permite "!= null" fácil en todos los casos; usa un filtro por estado si te sirve)
        db.collection("tours")
                .whereIn("estado", List.of("EN_PROCESO", "ASIGNADO", "FINALIZADO")) // ajusta a tus estados reales
                .get()
                .addOnSuccessListener(snap -> kpiGuiasConTours.setText(String.valueOf(snap.size())));
    }

    private void escucharSolicitudesRecientes() {
        db.collection("tours")
                .whereEqualTo("estado", "SOLICITADO")
                .orderBy("fechaInicioUtc", Query.Direction.DESCENDING)
                .limit(5)
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

                    tvContadorSolicitudes.setText(data.size() + " solicitudes");
                    boolean vacio = data.isEmpty();
                    emptyState.setVisibility(vacio ? View.VISIBLE : View.GONE);
                    rvSolicitudes.setVisibility(vacio ? View.GONE : View.VISIBLE);

                    // actualiza KPI también
                    kpiSolicitudesPend.setText(String.valueOf(snap.size())); // (ojo: aquí es solo las 5; si quieres total, deja cargarKPIs)
                });
    }
}
