package com.example.proyecto_2025.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.firebase.firestore.DocumentSnapshot;
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

    // ==== Callbacks ====
    public interface ToursCallback {
        void onSuccess(List<Tour> tours);
        void onError(Exception e);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(Exception e);
    }

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

        map.put("incluyeDesayuno", t.isIncluyeDesayuno());
        map.put("incluyeAlmuerzo", t.isIncluyeAlmuerzo());
        map.put("incluyeCena", t.isIncluyeCena());

        return map;
    }




    private Tour fromDoc(DocumentSnapshot doc) {
        // Convierte el documento a Tour usando el POJO
        Tour t = doc.toObject(Tour.class);
        if (t == null) return null;

        // Si el campo id no vino o está vacío, usa el id del doc
        if (t.id == null || t.id.isEmpty()) {
            t.id = doc.getId();
        }

        // Normalizar listas para evitar null
        if (t.idiomas == null)   t.idiomas   = new ArrayList<>();
        if (t.servicios == null) t.servicios = new ArrayList<>();
        if (t.imagenUris == null)t.imagenUris= new ArrayList<>();
        if (t.ruta == null)      t.ruta      = new ArrayList<>();

        // Normalizar estado (puede venir como String)
        Object estadoObj = doc.get("estado");
        if (estadoObj instanceof String) {
            try {
                t.estado = TourEstado.valueOf((String) estadoObj);
            } catch (IllegalArgumentException ignored) {}
        }
        if (t.estado == null) {
            t.estado = TourEstado.BORRADOR;
        }

        // Normalizar servicios adicionales (para docs antiguos sin estos campos)
        Boolean d = doc.getBoolean("incluyeDesayuno");
        Boolean a = doc.getBoolean("incluyeAlmuerzo");
        Boolean c = doc.getBoolean("incluyeCena");
        t.setIncluyeDesayuno(d != null && d);
        t.setIncluyeAlmuerzo(a != null && a);
        t.setIncluyeCena(c != null && c);

        return t;
    }

    private void syncToFirestore(Tour t, SimpleCallback cb) {
        if (t == null || t.id == null) {
            if (cb != null) cb.onError(new IllegalArgumentException("tour o id nulos"));
            return;
        }
        db.collection("tours")
                .document(t.id)
                .set(toMap(t), SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    if (cb != null) cb.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (cb != null) cb.onError(e);
                });
    }

    private void syncToFirestore(Tour t) {
        syncToFirestore(t, null);
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

    // ==== Lecturas desde Firestore ====

    // Tours de una empresa (para admin y "ver tours" de empresa)
    public void fetchToursByEmpresa(String empresaId, final ToursCallback callback) {
        db.collection("tours")
                .whereEqualTo("empresaId", empresaId)
                .get()
                .addOnSuccessListener(qs -> {
                    List<Tour> list = new ArrayList<>();
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Tour t = fromDoc(doc);
                        if (t != null) list.add(t);
                    }
                    // opcional: actualizar cache local
                    saveAll(list);
                    if (callback != null) callback.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    // Tours PUBLICADOS de todas las empresas (dashboard cliente)
    public void fetchPublishedToursForDashboard(final ToursCallback callback) {
        db.collection("tours")
                .whereEqualTo("estado", TourEstado.PUBLICADO.name())
                .get()
                .addOnSuccessListener(qs -> {
                    List<Tour> list = new ArrayList<>();
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Tour t = fromDoc(doc);
                        if (t != null) list.add(t);
                    }
                    if (callback != null) callback.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    // Tours PUBLICADOS de una empresa (cuando el cliente entra a "ver tours")
    public void fetchPublishedToursByEmpresa(String empresaId, final ToursCallback callback) {
        db.collection("tours")
                .whereEqualTo("empresaId", empresaId)
                .whereEqualTo("estado", TourEstado.PUBLICADO.name())
                .get()
                .addOnSuccessListener(qs -> {
                    List<Tour> list = new ArrayList<>();
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Tour t = fromDoc(doc);
                        if (t != null) list.add(t);
                    }
                    if (callback != null) callback.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

}
