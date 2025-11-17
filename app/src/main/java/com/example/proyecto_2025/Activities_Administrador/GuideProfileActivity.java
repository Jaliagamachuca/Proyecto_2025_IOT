package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Guide;

public class GuideProfileActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.guide_profile);

        Guide g = (Guide) getIntent().getSerializableExtra("guide");

        ImageView img = findViewById(R.id.img);
        TextView name = findViewById(R.id.name);
        TextView langs = findViewById(R.id.langs);
        TextView bio = findViewById(R.id.bio);
        TextView zone = findViewById(R.id.zone);
        TextView rating = findViewById(R.id.rating);


        Glide.with(this).load(g.getPhotoUrl()).into(img);
        name.setText(g.getName());
        langs.setText("Idiomas: "+g.getLanguages().toString().replace("[","").replace("]","").replace(","," · "));
        bio.setText(g.getBio());
        zone.setText("Zona: "+g.getZone());
        rating.setText(String.format("★ %.1f", g.getRating()));


    }
}
