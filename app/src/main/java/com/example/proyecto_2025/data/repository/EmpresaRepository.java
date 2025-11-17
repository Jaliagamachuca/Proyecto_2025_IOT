package com.example.proyecto_2025.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.proyecto_2025.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpresaRepository {

    private static final String PREF = "empresa_pref";
    private static final String KEY  = "empresa_json";
    private static final String TAG  = "EmpresaRepository";

    private final SharedPreferences sp;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public EmpresaRepository(Context ctx) {
        sp   = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // ================== CALLBACK ==================
    public interface EmpresaCallback {
        void onResult(Empresa empresa);
    }

    // ================== API PÚBLICA ==================

    /** Guarda empresa localmente y la sincroniza en Firestore. */
    public void save(Empresa e) {
        saveLocal(e);
        saveRemote(e);
    }

    /** Carga solo desde almacenamiento local (rápido). */
    public Empresa load() {
        Empresa e = new Empresa();
        String s = sp.getString(KEY, null);
        if (s == null) return e;
        try {
            JSONObject o = new JSONObject(s);

            e.id          = o.optString("id", null);
            e.adminId     = o.optString("adminId", null);
            e.status      = o.optString("status", "pending");

            e.nombre      = o.optString("nombre", "");
            e.descripcion = o.optString("descripcion", "");

            e.correo      = o.optString("correo", "");
            e.telefono    = o.optString("telefono", "");
            e.web         = o.optString("web", "");

            e.direccion   = o.optString("direccion", "");
            e.lat         = o.optDouble("lat", 0);
            e.lon         = o.optDouble("lon", 0);

            e.ratingPromedio    = o.optDouble("ratingPromedio", 0.0);
            e.totalValoraciones = o.optLong("totalValoraciones", 0);
            e.totalReservas     = o.optLong("totalReservas", 0);
            e.totalIngresos     = o.optDouble("totalIngresos", 0.0);

            JSONArray arr = o.optJSONArray("imagenes");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    e.imagenUris.add(arr.getString(i));
                }
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error parseando empresa local", ex);
        }
        return e;
    }

    /**
     * Carga desde Firestore (si existe empresa para este admin),
     * cachea localmente y devuelve por callback.
     */
    public void loadFromRemote(EmpresaCallback cb) {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            Log.w(TAG, "loadFromRemote: no hay usuario logueado, uso solo cache local");
            cb.onResult(load());
            return;
        }

        db.collection("empresas")
                .whereEqualTo("adminId", current.getUid())
                .limit(1)
                .get()
                .addOnSuccessListener(snaps -> {
                    if (snaps.isEmpty()) {
                        // No tiene empresa aún → devolvemos lo que haya local (vacío normalmente)
                        cb.onResult(load());
                        return;
                    }

                    // Tomamos la primera empresa de ese admin
                    com.google.firebase.firestore.DocumentSnapshot doc = snaps.getDocuments().get(0);

                    Empresa e = new Empresa();
                    e.id      = doc.getId();
                    e.adminId = doc.getString("adminId");

                    e.nombre       = doc.getString("nombre");
                    e.descripcion  = doc.getString("descripcionCorta");

                    e.correo       = doc.getString("email");
                    e.telefono     = doc.getString("telefono");
                    // aún no almacenamos "web" en Firestore, así que se queda vacío
                    e.web          = "";

                    e.direccion    = doc.getString("direccion");
                    Double lat     = doc.getDouble("lat");
                    Double lng     = doc.getDouble("lng");
                    e.lat          = lat != null ? lat : 0d;
                    e.lon          = lng != null ? lng : 0d;

                    e.status       = doc.getString("status") != null
                            ? doc.getString("status")
                            : "pending";

                    Double rating  = doc.getDouble("ratingPromedio");
                    Double ingresos= doc.getDouble("totalIngresos");
                    Long   totVal  = doc.getLong("totalValoraciones");
                    Long   totRes  = doc.getLong("totalReservas");

                    e.ratingPromedio    = rating  != null ? rating  : 0.0;
                    e.totalIngresos     = ingresos!= null ? ingresos: 0.0;
                    e.totalValoraciones = totVal  != null ? totVal  : 0;
                    e.totalReservas     = totRes  != null ? totRes  : 0;

                    List<String> fotos = (List<String>) doc.get("fotos");
                    if (fotos != null) e.imagenUris.addAll(fotos);

                    // Cache local actualizada
                    saveLocal(e);

                    cb.onResult(e);
                })
                .addOnFailureListener(err -> {
                    Log.e(TAG, "Error leyendo empresa remota", err);
                    cb.onResult(load());  // fallback a cache
                });
    }

    // ================== PRIVADO: LOCAL ==================

    private void saveLocal(Empresa e) {
        try {
            JSONObject o = new JSONObject();
            o.put("id",          e.id);
            o.put("adminId",     e.adminId);
            o.put("status",      e.status);

            o.put("nombre",      e.nombre);
            o.put("descripcion", e.descripcion);

            o.put("correo",      e.correo);
            o.put("telefono",    e.telefono);
            o.put("web",         e.web);

            o.put("direccion",   e.direccion);
            o.put("lat",         e.lat);
            o.put("lon",         e.lon);

            o.put("ratingPromedio",    e.ratingPromedio);
            o.put("totalValoraciones", e.totalValoraciones);
            o.put("totalReservas",     e.totalReservas);
            o.put("totalIngresos",     e.totalIngresos);

            JSONArray arr = new JSONArray();
            if (e.imagenUris != null) {
                for (String u : e.imagenUris) arr.put(u);
            }
            o.put("imagenes", arr);

            sp.edit().putString(KEY, o.toString()).apply();
        } catch (JSONException ex) {
            Log.e(TAG, "Error guardando empresa local", ex);
        }
    }

    // ================== PRIVADO: FIRESTORE ==================

    private void saveRemote(Empresa e) {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            Log.w(TAG, "No hay usuario autenticado, no se guarda en Firestore");
            return;
        }

        // Asegurar adminId
        if (e.adminId == null || e.adminId.isEmpty()) {
            e.adminId = current.getUid();
        }

        boolean isNew = (e.id == null || e.id.isEmpty());

        // Si no tiene id de empresa aún, crear uno nuevo
        if (isNew) {
            e.id = db.collection("empresas").document().getId();
            saveLocal(e); // guardamos id en cache
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", e.id);

        // Datos básicos
        data.put("nombre", e.nombre);
        data.put("descripcionCorta", e.descripcion);

        // Ubicación
        data.put("direccion", e.direccion);
        data.put("lat", e.lat);
        data.put("lng", e.lon);  // nuestro campo se llama lon

        // Contacto
        data.put("email", e.correo);
        data.put("telefono", e.telefono);

        // Imágenes
        data.put("fotos", e.imagenUris);

        // Lógica de negocio
        data.put("adminId", e.adminId);
        data.put("status", e.status);

        // Métricas
        data.put("ratingPromedio", e.ratingPromedio);
        data.put("totalValoraciones", e.totalValoraciones);
        data.put("totalReservas", e.totalReservas);
        data.put("totalIngresos", e.totalIngresos);

        // Timestamps
        data.put("updatedAt", FieldValue.serverTimestamp());
        if (isNew) {
            data.put("createdAt", FieldValue.serverTimestamp());
        }

        db.collection("empresas")
                .document(e.id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused ->
                        Log.d(TAG, "Empresa guardada en Firestore: " + e.id))
                .addOnFailureListener(err ->
                        Log.e(TAG, "Error guardando empresa en Firestore", err));

        // ====== Actualizar companyId del usuario en "users" ======
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("companyId", e.id);
        userUpdate.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(current.getUid())
                .set(userUpdate, SetOptions.merge())
                .addOnSuccessListener(unused ->
                        Log.d(TAG, "User.companyId actualizado: " + e.id))
                .addOnFailureListener(err ->
                        Log.e(TAG, "Error actualizando companyId del usuario", err));
    }
}
