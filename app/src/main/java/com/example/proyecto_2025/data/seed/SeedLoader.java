package com.example.proyecto_2025.data.seed;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.proyecto_2025.data.local.db.AppDatabase;
import com.example.proyecto_2025.data.local.entity.EmpresaEntity;
import com.example.proyecto_2025.data.local.entity.TourEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executors;

public class SeedLoader {

    static class EmpresaSeed {
        public String nombre, ciudad, pais, logoKey;
        public List<TourSeed> tours;
    }
    static class TourSeed {
        public String nombre, descripcion, portadaKey;
        public long inicioUtc, finUtc;
        public double precio;
        public float rating;
    }

    public static void loadIfNeeded(Context c) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.get(c);
            if (db.empresaDao().count() > 0 || db.tourDao().count() > 0) return;
            try {
                AssetManager am = c.getAssets();
                InputStream is = am.open("tours_seed.json");
                Type listType = new TypeToken<List<EmpresaSeed>>(){}.getType();
                List<EmpresaSeed> empresas = new Gson().fromJson(new InputStreamReader(is), listType);

                for (EmpresaSeed es : empresas) {
                    EmpresaEntity e = new EmpresaEntity();
                    e.nombre = es.nombre; e.ciudad = es.ciudad; e.pais = es.pais; e.logoKey = es.logoKey;
                    long empresaId = db.empresaDao().insert(e);

                    for (TourSeed ts : es.tours) {
                        TourEntity t = new TourEntity();
                        t.empresaId = empresaId;
                        t.nombre = ts.nombre;
                        t.descripcion = ts.descripcion;
                        t.inicioUtc = ts.inicioUtc;
                        t.finUtc = ts.finUtc;
                        t.precio = ts.precio;
                        t.rating = ts.rating;
                        t.portadaKey = ts.portadaKey;
                        db.tourDao().insert(t);
                    }
                }
            } catch (Exception ignored) { }
        });
    }
}
