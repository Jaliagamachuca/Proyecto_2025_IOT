package com.example.proyecto_2025.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TourRepository {
    private static final String PREF = "tour_repo";
    private static final String KEY  = "tour_list";
    private final SharedPreferences sp;
    private final Gson gson = new Gson();

    // 🔥 Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TourRepository(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    // ================= LOCAL (SharedPreferences) =================

    public synchronized List<Tour> findAll() {
        String json = sp.getString(KEY, "");
        if (json == null || json.isEmpty()) return new ArrayList<>();
        Type type = new TypeToken<List<Tour>>(){}.getType();
        try { return gson.fromJson(json, type); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private synchronized void saveAll(List<Tour> tours) {
        sp.edit().putString(KEY, gson.toJson(tours)).apply();
    }

    public synchronized Tour findById(String id) {
        for (Tour t : findAll()) if (t.id.equals(id)) return t;
        return null;
    }

    public synchronized List<Tour> findLastN(int n) {
        List<Tour> list = findAll();
        Collections.reverse(list);
        return list.subList(0, Math.min(n, list.size()));
    }

    // ================= FIRESTORE SYNC HELPERS =================

    private Map<String, Object> toMap(Tour t) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.id);
        map.put("empresaId", t.empresaId);
        map.put("titulo", t.titulo);
        map.put("descripcionCorta", t.descripcionCorta);
        map.put("precioPorPersona", t.precioPorPersona);
        map.put("cupos", t.cupos);
        map.put("idiomas", t.idiomas);
        map.put("servicios", t.servicios);
        map.put("imagenUris", t.imagenUris);
        map.put("fechaInicioUtc", t.fechaInicioUtc);
        map.put("fechaFinUtc", t.fechaFinUtc);
        map.put("ruta", t.ruta); // PuntoRuta es POJO simple, Firestore lo soporta
        map.put("guiaId", t.guiaId);
        map.put("propuestaPagoGuia", t.propuestaPagoGuia);
        map.put("pagoEsPorcentaje", t.pagoEsPorcentaje);
        map.put("estado", t.estado != null ? t.estado.name() : null);
        return map;
    }

    private void syncToFirestore(Tour t) {
        if (t == null || t.id == null) return;
        db.collection("tours")
                .document(t.id)
                .set(toMap(t), SetOptions.merge());
    }

    private void deleteFromFirestore(String id) {
        if (id == null) return;
        db.collection("tours")
                .document(id)
                .delete();
    }

    // ================= API PÚBLICA (LOCAL + FIRESTORE) =================

    public synchronized void upsert(Tour t) {
        // Actualizar cache local
        List<Tour> list = findAll();
        int idx = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id.equals(t.id)) {
                idx = i;
                break;
            }
        }
        if (idx >= 0) list.set(idx, t); else list.add(t);
        saveAll(list);

        // Sincronizar con Firestore
        syncToFirestore(t);
    }

    public synchronized void delete(String id) {
        List<Tour> list = findAll();
        list.removeIf(t -> t.id.equals(id));
        saveAll(list);

        deleteFromFirestore(id);
    }
}
