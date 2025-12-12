package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.repository.TourRepository;
import com.example.proyecto_2025.model.PuntoRuta;
import com.example.proyecto_2025.model.Tour;
import com.google.android.material.appbar.MaterialToolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class TourRutaMapActivity extends AppCompatActivity {

    private MapView mapView;
    private TourRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración OSMDroid
        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_tour_ruta_map);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mapView = findViewById(R.id.mapRuta);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        repo = new TourRepository(this);

        // id del tour que viene del detalle
        String id = getIntent().getStringExtra("id");
        if (id == null || id.isEmpty()) {
            finish();
            return;
        }

        Tour tour = repo.findById(id);
        if (tour == null || tour.ruta == null || tour.ruta.isEmpty()) {
            finish();
            return;
        }

        dibujarRuta(tour.ruta);
    }

    private void dibujarRuta(List<PuntoRuta> puntosRuta) {
        // Lista de puntos para la polilínea y el zoom
        List<GeoPoint> geoPoints = new ArrayList<>();

        int i = 1;
        for (PuntoRuta p : puntosRuta) {
            GeoPoint gp = new GeoPoint(p.lat, p.lon);
            geoPoints.add(gp);

            // Marcador
            Marker m = new Marker(mapView);
            m.setPosition(gp);
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            String titulo = (p.nombre != null && !p.nombre.isEmpty())
                    ? p.nombre
                    : "Punto " + i;
            m.setTitle(titulo);

            // Texto extra del marcador
            StringBuilder sb = new StringBuilder();
            if (p.minutosEstimados > 0) {
                sb.append("Estancia: ")
                        .append(p.minutosEstimados)
                        .append(" min\n");
            }
            sb.append("Lat: ").append(p.lat).append("\n");
            sb.append("Lon: ").append(p.lon);

            m.setSubDescription(sb.toString());
            mapView.getOverlays().add(m);

            i++;
        }

        // Polyline que une todos los puntos
        Polyline polyline = new Polyline();
        polyline.setPoints(geoPoints);
        polyline.setWidth(6f);   // warning deprecado pero funciona sin problema
        mapView.getOverlays().add(polyline);

        // Ajustar zoom para que se vea toda la ruta
        if (!geoPoints.isEmpty()) {
            BoundingBox bb = BoundingBox.fromGeoPoints(geoPoints);
            mapView.zoomToBoundingBox(bb, true, 100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // por si usas el back del action bar estándar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
