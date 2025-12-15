package com.example.proyecto_2025.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Guide implements Serializable {

    // ===== IDs =====
    public String uid;   // id del documento Firestore

    // ===== Datos básicos =====
    public String displayName;
    public String phone;
    public String photoUrl;

    // ===== Métricas =====
    public float ratingPromedio = 0f;
    public int totalValoraciones = 0;
    public int toursRealizados = 0;

    // ===== Arrays =====
    public List<String> idiomas = new ArrayList<>();
    public List<String> zonaOperacion = new ArrayList<>();

    // ===== Requerido por Firestore =====
    public Guide() {}

    // ===== GETTERS (para adapters y activities) =====
    public String getId() { return uid; }

    public String getName() {
        return displayName != null ? displayName : "";
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public String getPhotoUrl() {
        return photoUrl != null ? photoUrl : "";
    }

    public float getRating() {
        return ratingPromedio;
    }

    public List<String> getLanguages() {
        return idiomas != null ? idiomas : new ArrayList<>();
    }

    /** Devuelve una zona legible (primera o "-") */
    public String getZone() {
        if (zonaOperacion != null && !zonaOperacion.isEmpty()) {
            return zonaOperacion.get(0);
        }
        return "-";
    }
}
