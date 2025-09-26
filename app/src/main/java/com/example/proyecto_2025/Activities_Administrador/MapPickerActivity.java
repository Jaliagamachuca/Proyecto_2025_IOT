package com.example.proyecto_2025.Activities_Administrador;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.proyecto_2025.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPickerActivity extends AppCompatActivity {

    private MapView mapView;
    private Marker marker;
    private GeoPoint seleccionado;
    private TextView tvDireccion;
    private MyLocationNewOverlay myLocationOverlay;

    private final ActivityResultLauncher<String> permisoUbicacion =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    habilitarMiUbicacion();
                } else {
                    Toast.makeText(MapPickerActivity.this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config osmdroid (recomendado 6.x)
        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map_picker);

        mapView = findViewById(R.id.mapView);
        tvDireccion = findViewById(R.id.tvDireccionSel);
        Button btnOk = findViewById(R.id.btnOk);
        Button btnMiUbicacion = findViewById(R.id.btnMiUbicacion);

        // Setup MapView
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        GeoPoint lima = new GeoPoint(-12.0464, -77.0428);
        mapView.getController().setCenter(lima);

        // Marker reutilizable
        marker = new Marker(mapView);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        // Tap/long-press usando MapEventsOverlay (API actual)
        MapEventsOverlay tapOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override public boolean singleTapConfirmedHelper(GeoPoint p) { seleccionar(p); return true; }
            @Override public boolean longPressHelper(GeoPoint p) { seleccionar(p); return true; }
        });
        mapView.getOverlays().add(tapOverlay);

        // Confirmar selección
        btnOk.setOnClickListener(v -> {
            if (seleccionado == null) {
                Toast.makeText(this, "Selecciona un punto en el mapa", Toast.LENGTH_SHORT).show();
                return;
            }
            String direccion = reverseGeocode(seleccionado);
            Intent data = new Intent();
            data.putExtra("direccion", direccion != null ? direccion : coordToText(seleccionado));
            data.putExtra("lat", seleccionado.getLatitude());
            data.putExtra("lon", seleccionado.getLongitude());
            setResult(Activity.RESULT_OK, data);
            finish();
        });

        // Mi ubicación (opcional)
        btnMiUbicacion.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                habilitarMiUbicacion();
            }
        });
    }

    private void seleccionar(GeoPoint p) {
        seleccionado = p;
        marker.setPosition(p);
        marker.setTitle("Seleccionado");
        mapView.getController().animateTo(p);
        String dir = reverseGeocode(p);
        tvDireccion.setText(dir != null ? dir : coordToText(p));
        mapView.invalidate();
    }

    private void habilitarMiUbicacion() {
        if (myLocationOverlay == null) {
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
            myLocationOverlay.enableMyLocation();
            mapView.getOverlays().add(myLocationOverlay);
        }
        GeoPoint me = myLocationOverlay.getMyLocation();
        if (me != null) seleccionar(me);
        else Toast.makeText(this, "Obteniendo ubicación… toca el mapa para seleccionar", Toast.LENGTH_SHORT).show();
    }

    private String reverseGeocode(GeoPoint p) {
        try {
            Geocoder g = new Geocoder(this, Locale.getDefault());
            List<Address> list = g.getFromLocation(p.getLatitude(), p.getLongitude(), 1);
            if (list != null && !list.isEmpty()) {
                String line = list.get(0).getAddressLine(0);
                if (line != null && !line.isEmpty()) return line;
            }
        } catch (IOException ignored) {}
        return null;
    }

    private String coordToText(GeoPoint p) {
        return String.format(Locale.getDefault(), "Lat %.6f, Lon %.6f",
                p.getLatitude(), p.getLongitude());
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause()  { super.onPause();  mapView.onPause();  }
}
