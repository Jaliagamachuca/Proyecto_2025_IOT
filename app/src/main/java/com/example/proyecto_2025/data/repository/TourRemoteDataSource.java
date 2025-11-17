package com.example.proyecto_2025.data.repository;

import androidx.annotation.Nullable;

import com.example.proyecto_2025.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Fuente de datos remota para tours (Firestore).
 * Se usa SOLO para guardar tours creados/actualizados por el admin.
 */
public class TourRemoteDataSource {

    private static final String COLLECTION = "tours";

    private final FirebaseFirestore db;

    public TourRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSuccess();
        void onError(Exception e);
    }

    /**
     * Guarda o actualiza un tour en Firestore.
     * - Usa tour.id como ID de documento (si es null, se genera uno).
     * - Sube el objeto completo (POJO).
     */
    public void save(Tour tour, @Nullable Callback cb) {
        if (tour.id == null || tour.id.isEmpty()) {
            tour.id = UUID.randomUUID().toString();
        }

        db.collection(COLLECTION)
                .document(tour.id)
                .set(tour)
                .addOnSuccessListener(unused -> {
                    if (cb != null) cb.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (cb != null) cb.onError(e);
                });
    }

    /**
     * Elimina un tour en Firestore por id.
     */
    public void delete(String tourId, @Nullable Callback cb) {
        db.collection(COLLECTION)
                .document(tourId)
                .delete()
                .addOnSuccessListener(unused -> {
                    if (cb != null) cb.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (cb != null) cb.onError(e);
                });
    }
}
