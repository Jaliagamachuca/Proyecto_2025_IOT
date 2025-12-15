package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Guide;

import java.util.Locale;

public class GuideProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.guide_profile);

        Guide g = (Guide) getIntent().getSerializableExtra("guide");
        if (g == null) return;

        ImageView img = findViewById(R.id.img);
        TextView name = findViewById(R.id.name);
        TextView langs = findViewById(R.id.langs);
        TextView zone = findViewById(R.id.zone);
        TextView rating = findViewById(R.id.rating);

        Glide.with(this)
                .load(g.getPhotoUrl())
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(img);

        name.setText(g.getName());

        langs.setText(
                "Idiomas: " + g.getLanguages()
                        .toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", " · ")
        );

        zone.setText("Zona: " + g.getZone());

        rating.setText(
                String.format(Locale.getDefault(), "★ %.1f", g.getRating())
        );
    }
}
