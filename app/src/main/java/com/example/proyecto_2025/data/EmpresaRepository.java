package com.example.proyecto_2025.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proyecto_2025.model.Empresa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmpresaRepository {
    private static final String PREF = "empresa_pref";
    private static final String KEY = "empresa_json";

    private final SharedPreferences sp;

    public EmpresaRepository(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void save(Empresa e) {
        try {
            JSONObject o = new JSONObject();
            o.put("nombre", e.nombre);
            o.put("correo", e.correo);
            o.put("telefono", e.telefono);
            o.put("web", e.web);
            o.put("descripcion", e.descripcion);
            o.put("direccion", e.direccion);
            o.put("lat", e.lat);
            o.put("lon", e.lon);
            JSONArray arr = new JSONArray();
            if (e.imagenUris != null) for (String u : e.imagenUris) arr.put(u);
            o.put("imagenes", arr);
            sp.edit().putString(KEY, o.toString()).apply();
        } catch (JSONException ex) { /* ignore simple demo */ }
    }

    public Empresa load() {
        Empresa e = new Empresa();
        String s = sp.getString(KEY, null);
        if (s == null) return e;
        try {
            JSONObject o = new JSONObject(s);
            e.nombre = o.optString("nombre", "");
            e.correo = o.optString("correo", "");
            e.telefono = o.optString("telefono", "");
            e.web = o.optString("web", "");
            e.descripcion = o.optString("descripcion", "");
            e.direccion = o.optString("direccion", "");
            e.lat = o.optDouble("lat", 0);
            e.lon = o.optDouble("lon", 0);
            JSONArray arr = o.optJSONArray("imagenes");
            if (arr != null) for (int i=0;i<arr.length();i++) e.imagenUris.add(arr.getString(i));
        } catch (JSONException ex) { /* ignore */ }
        return e;
    }
}
