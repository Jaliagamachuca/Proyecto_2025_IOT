package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.repository.OfferRepository;
import com.example.proyecto_2025.model.Offer;

public class Admin_Guias_HomeActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.admin_guias_home);

        TextView kpiPend = findViewById(R.id.kpiPend);
        TextView kpiAcep = findViewById(R.id.kpiAcep);
        kpiPend.setText(String.valueOf(OfferRepository.get().byStatus(Offer.Status.PENDIENTE).size()));
        kpiAcep.setText(String.valueOf(OfferRepository.get().byStatus(Offer.Status.ACEPTADA).size()));

        Button btnExplorar = findViewById(R.id.btnExplorar);
        Button btnOfertas  = findViewById(R.id.btnOfertas);
        Button btnAsignar  = findViewById(R.id.btnAsignar);

        btnExplorar.setOnClickListener(v-> startActivity(new Intent(this, GuideDirectoryActivity.class)));
        btnOfertas.setOnClickListener(v-> startActivity(new Intent(this, OfferInboxActivity.class)));
        btnAsignar.setOnClickListener(v-> startActivity(new Intent(this, OfferInboxActivity.class)));
    }
}
