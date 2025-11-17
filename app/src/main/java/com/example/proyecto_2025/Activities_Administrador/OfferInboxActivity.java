package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.OfferAdapter;
import com.example.proyecto_2025.data.repository.OfferRepository;
import com.example.proyecto_2025.model.Offer;
import com.google.android.material.chip.Chip;

import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.*;

public class OfferInboxActivity extends AppCompatActivity implements OfferAdapter.OnAction {

    private OfferAdapter adapter;
    private List<Offer> view = new ArrayList<>();
    private LinearLayout emptyState;
    private RecyclerView rvOffers;
    private TextView tvEmptyTitle;

    private void load(Offer.Status s){
        view.clear();
        view.addAll(OfferRepository.get().byStatus(s));
        adapter.notifyDataSetChanged();

        // Mostrar/ocultar empty state
        if (view.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvOffers.setVisibility(View.GONE);

            // Actualizar mensaje según el filtro
            switch (s) {
                case PENDIENTE:
                    tvEmptyTitle.setText("No hay solicitudes pendientes");
                    break;
                case ACEPTADA:
                    tvEmptyTitle.setText("No hay solicitudes aceptadas");
                    break;
                case RECHAZADA:
                    tvEmptyTitle.setText("No hay solicitudes rechazadas");
                    break;
                case VENCIDA:
                    tvEmptyTitle.setText("No hay solicitudes vencidas");
                    break;
            }
        } else {
            emptyState.setVisibility(View.GONE);
            rvOffers.setVisibility(View.VISIBLE);
        }
    }

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.offer_inbox);

        // RecyclerView
        rvOffers = findViewById(R.id.rvOffers);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfferAdapter(this, view, this);
        rvOffers.setAdapter(adapter);

        // Empty state
        emptyState = findViewById(R.id.emptyState);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);


        // Chips (ahora son Chip en lugar de Button)
        Chip bPend = findViewById(R.id.btnPend);
        Chip bAcep = findViewById(R.id.btnAcep);
        Chip bRech = findViewById(R.id.btnRech);
        Chip bVenc = findViewById(R.id.btnVenc);

        bPend.setOnClickListener(v -> load(Offer.Status.PENDIENTE));
        bAcep.setOnClickListener(v -> load(Offer.Status.ACEPTADA));
        bRech.setOnClickListener(v -> load(Offer.Status.RECHAZADA));
        bVenc.setOnClickListener(v -> load(Offer.Status.VENCIDA));

        // Cargar pendientes por defecto
        load(Offer.Status.PENDIENTE);
    }

    @Override public void onAssign(Offer o) {
        Intent i = new Intent(this, AssignGuideActivity.class);
        i.putExtra("offerId", o.getId());
        startActivity(i);
    }

    @Override public void onDetail(Offer o) {
        Intent i = new Intent(this, OfferDetailActivity.class);
        i.putExtra("offerId", o.getId());
        startActivity(i);
    }
}