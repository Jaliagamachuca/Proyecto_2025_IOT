package com.example.proyecto_2025.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.proyecto_2025.model.Admin;
import com.google.gson.Gson;

public class AdminRepository {

    private static final String PREFS_NAME = "admin_prefs";
    private static final String KEY_ADMIN = "admin_data";

    private final SharedPreferences prefs;
    private final Gson gson;

    public AdminRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Guarda los datos del admin en SharedPreferences
     */
    public void save(Admin admin) {
        String json = gson.toJson(admin);
        prefs.edit().putString(KEY_ADMIN, json).apply();
    }

    /**
     * Carga los datos del admin desde SharedPreferences
     * Si no existe, crea uno por defecto
     */
    public Admin load() {
        String json = prefs.getString(KEY_ADMIN, null);
        if (json == null) {
            // Crear admin por defecto
            Admin admin = new Admin();
            admin.setNombre("Juan Pérez");
            admin.setEmail("juan@empresa.com");
            admin.setTelefono("987654321");
            save(admin);
            return admin;
        }
        return gson.fromJson(json, Admin.class);
    }

    /**
     * Limpia los datos del admin (útil para cerrar sesión)
     */
    public void clear() {
        prefs.edit().remove(KEY_ADMIN).apply();
    }
}