package com.example.proyecto_2025.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.proyecto_2025.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private final Context appContext;

    public EmpresaRepository(Context ctx) {
        this.appContext = ctx.getApplicationContext();
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
                    // La guardamos como "descripcionCorta" en Firestore
                    e.descripcion  = doc.getString("descripcionCorta");

                    e.correo       = doc.getString("email");
                    e.telefono     = doc.getString("telefono");
                    // ⬇️ AHORA sí leemos la web desde Firestore
                    e.web          = doc.getString("web");

                    e.direccion    = doc.getString("direccion");
                    Double lat     = doc.getDouble("lat");
                    Double lng     = doc.getDouble("lng");
                    e.lat          = lat != null ? lat : 0d;
                    e.lon          = lng != null ? lng : 0d;

                    String status  = doc.getString("status");
                    e.status       = status != null ? status : "pending";

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

        final String uid = current.getUid();

        // Forzar adminId siempre al UID actual
        e.adminId = uid;

        // Defaults seguros
        if (e.status == null || e.status.trim().isEmpty()) e.status = "pending";
        if (e.nombre == null) e.nombre = "";
        if (e.descripcion == null) e.descripcion = "";
        if (e.direccion == null) e.direccion = "";
        if (e.correo == null) e.correo = "";
        if (e.telefono == null) e.telefono = "";
        if (e.web == null) e.web = "";
        if (e.imagenUris == null) e.imagenUris = java.util.Collections.emptyList();

        boolean isNew = (e.id == null || e.id.trim().isEmpty());

        if (isNew) {
            e.id = db.collection("empresas").document().getId();
            saveLocal(e);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", e.id);

        data.put("nombre", e.nombre);
        data.put("descripcionCorta", e.descripcion);

        data.put("direccion", e.direccion);
        data.put("lat", e.lat);
        data.put("lng", e.lon);

        data.put("email", e.correo);
        data.put("telefono", e.telefono);
        data.put("web", e.web);

        data.put("fotos", e.imagenUris);

        // IMPORTANTE: adminId debe viajar en el payload para que CREATE pase en rules
        data.put("adminId", uid);
        data.put("status", e.status);

        data.put("ratingPromedio", e.ratingPromedio);
        data.put("totalValoraciones", e.totalValoraciones);
        data.put("totalReservas", e.totalReservas);
        data.put("totalIngresos", e.totalIngresos);

        data.put("updatedAt", FieldValue.serverTimestamp());
        if (isNew) data.put("createdAt", FieldValue.serverTimestamp());

        Log.d(TAG, "saveRemote empresaId=" + e.id + " isNew=" + isNew + " adminId=" + uid);

        var empresaRef = db.collection("empresas").document(e.id);

        if (isNew) {
            // CREATE (sobrescribe doc)
            empresaRef.set(data)
                    .addOnSuccessListener(unused -> {
                        Log.d(TAG, "Empresa creada en Firestore: " + e.id);
                        actualizarCompanyIdUsuario(uid, e.id);
                    })
                    .addOnFailureListener(err ->
                            Log.e(TAG, "Error creando empresa en Firestore", err));
        } else {
            // UPDATE (merge)
            empresaRef.set(data, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        Log.d(TAG, "Empresa actualizada en Firestore: " + e.id);
                        actualizarCompanyIdUsuario(uid, e.id);
                    })
                    .addOnFailureListener(err ->
                            Log.e(TAG, "Error actualizando empresa en Firestore", err));
        }
    }

    private void actualizarCompanyIdUsuario(String uid, String empresaId) {
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("companyId", empresaId);
        userUpdate.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .set(userUpdate, SetOptions.merge())
                .addOnSuccessListener(u2 ->
                        Log.d(TAG, "User.companyId actualizado: " + empresaId))
                .addOnFailureListener(err2 ->
                        Log.e(TAG, "Error actualizando companyId del usuario", err2));
    }



    public interface UploadFotosCallback {
        void onSuccess(List<String> downloadUrls);
        void onError(Exception e);
    }

    public void uploadFotosEmpresa(String empresaId, List<Uri> localUris, UploadFotosCallback cb) {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) { cb.onError(new IllegalStateException("No hay usuario autenticado")); return; }
        if (empresaId == null || empresaId.trim().isEmpty()) { cb.onError(new IllegalArgumentException("empresaId vacío")); return; }
        if (localUris == null || localUris.isEmpty()) { cb.onSuccess(new ArrayList<>()); return; }

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Filtrar URIs válidos
        List<Uri> uris = new ArrayList<>();
        for (Uri u : localUris) if (u != null) uris.add(u);

        if (uris.isEmpty()) { cb.onSuccess(new ArrayList<>()); return; }

        List<String> out = java.util.Collections.synchronizedList(new ArrayList<>());
        java.util.concurrent.atomic.AtomicInteger done = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicBoolean finished = new java.util.concurrent.atomic.AtomicBoolean(false);

        final int total = uris.size();

        for (int i = 0; i < uris.size(); i++) {
            Uri originalUri = uris.get(i);

            Uri uploadUri;
            try {
                // 🔒 Lo más robusto: copiar a cache para evitar SecurityException con MediaDocumentsProvider
                uploadUri = copyToCacheAndGetUri(originalUri);
            } catch (Exception ex) {
                if (finished.compareAndSet(false, true)) cb.onError(ex);
                return;
            }

            String name = "foto_" + System.currentTimeMillis() + "_" + i + ".jpg";
            String uid = current.getUid();

            StorageReference ref = storage.getReference()
                    .child("empresas")
                    .child(uid)
                    .child(empresaId)
                    .child(name);


            com.google.firebase.storage.StorageMetadata meta =
                    new com.google.firebase.storage.StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();

            ref.putFile(uploadUri, meta)
                    .continueWithTask(t -> {
                        if (!t.isSuccessful()) throw (t.getException() != null ? t.getException() : new RuntimeException("Upload falló"));
                        return ref.getDownloadUrl();
                    })
                    .addOnSuccessListener(downloadUri -> {
                        out.add(downloadUri.toString());
                        int d = done.incrementAndGet();
                        if (d == total && finished.compareAndSet(false, true)) cb.onSuccess(new ArrayList<>(out));
                    })
                    .addOnFailureListener(err -> {
                        if (finished.compareAndSet(false, true)) cb.onError(err);
                    });
        }
    }

    /**
     * Copia el contenido del content:// a un archivo temporal en cache y devuelve Uri file://.
     * Esto evita el crash: "requires that you obtain access using ACTION_OPEN_DOCUMENT..."
     */
    private Uri copyToCacheAndGetUri(Uri src) throws Exception {
        android.content.ContentResolver cr = appContext.getContentResolver(); // <-- asegúrate de tener appContext en el repo

        java.io.InputStream in = cr.openInputStream(src);
        if (in == null) throw new IllegalStateException("No se pudo abrir InputStream del Uri");

        java.io.File outFile = new java.io.File(appContext.getCacheDir(),
                "empresa_upload_" + System.currentTimeMillis() + ".jpg");

        java.io.OutputStream out = new java.io.FileOutputStream(outFile);
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) > 0) out.write(buf, 0, len);

        out.flush();
        out.close();
        in.close();

        return Uri.fromFile(outFile);
    }



    // ================== LIMPIAR CACHE LOCAL ==================
    public void clear() {
        sp.edit().remove(KEY).apply();
    }
}
