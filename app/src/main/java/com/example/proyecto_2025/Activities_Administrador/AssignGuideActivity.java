package com.example.proyecto_2025.Activities_Administrador;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.*;
import com.example.proyecto_2025.model.*;

public class AssignGuideActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.assign_guide);

        String offerId = getIntent().getStringExtra("offerId");
        com.example.proyecto_2025.model.Offer o =
                com.example.proyecto_2025.data.OfferRepository.get().byId(offerId);
        com.example.proyecto_2025.model.Guide g =
                com.example.proyecto_2025.data.GuideRepository.get().byId(o.getGuideId());

        TextView tv = findViewById(R.id.tvSummary);
        // Usamos el ID de tour como placeholder hasta enlazar tu TourRepository real
        String tourDisplay = (o.getTourId() != null) ? o.getTourId() : "(sin tour)";
        tv.setText("Tour: " + tourDisplay
                + "\nGuía: " + (g != null ? g.getName() : "(desconocido)")
                + "\nPago: " + o.getPayMode() + " S/" + o.getAmount());

        Button btn = findViewById(R.id.btnConfirm);
        btn.setOnClickListener(v -> {
            // Aquí solo confirmamos. Cuando integremos con tu TourRepository real,
            // marcamos el tour como "con guía".
            android.widget.Toast.makeText(this, "Guía asignado.", android.widget.Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
