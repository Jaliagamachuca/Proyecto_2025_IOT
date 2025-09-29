package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.OfferAdapter;
import com.example.proyecto_2025.data.OfferRepository;
import com.example.proyecto_2025.model.Offer;
import java.util.*;

public class OfferInboxActivity extends AppCompatActivity implements OfferAdapter.OnAction {

    private OfferAdapter adapter;
    private List<Offer> view = new ArrayList<>();

    private void load(Offer.Status s){
        view.clear();
        view.addAll(OfferRepository.get().byStatus(s));
        adapter.notifyDataSetChanged();
    }

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.offer_inbox);

        RecyclerView rv = findViewById(R.id.rvOffers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfferAdapter(this, view, this);
        rv.setAdapter(adapter);

        Button bPend=findViewById(R.id.btnPend);
        Button bAcep=findViewById(R.id.btnAcep);
        Button bRech=findViewById(R.id.btnRech);
        Button bVenc=findViewById(R.id.btnVenc);

        bPend.setOnClickListener(v->load(Offer.Status.PENDIENTE));
        bAcep.setOnClickListener(v->load(Offer.Status.ACEPTADA));
        bRech.setOnClickListener(v->load(Offer.Status.RECHAZADA));
        bVenc.setOnClickListener(v->load(Offer.Status.VENCIDA));

        load(Offer.Status.PENDIENTE);
    }

    @Override public void onAssign(Offer o) {
        Intent i = new Intent(this, AssignGuideActivity.class);
        i.putExtra("offerId", o.getId());
        startActivity(i);
    }

    @Override public void onDetail(Offer o) {
        // Simplemente mostrar asignar para aceptadas; podrías abrir un detalle
        if(o.getStatus()== Offer.Status.ACEPTADA) onAssign(o);
    }
}
