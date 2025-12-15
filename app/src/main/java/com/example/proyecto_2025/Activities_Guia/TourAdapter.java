package com.example.proyecto_2025.Activities_Guia;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ItemTourBinding;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private final List<Tour> tourList;
    private final Context context;

    public TourAdapter(Context context, List<Tour> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTourBinding binding = ItemTourBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TourViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);

        holder.binding.textNombreTour.setText(
                tour.titulo != null ? tour.titulo : "Tour"
        );

        // Si no tienes fotoUrl en model.Tour, deja placeholder
        // (en tu TourDetalle usas imagenUris, así que aquí muestro la primera si existe)
        String primeraImg = null;
        if (tour.imagenUris != null && !tour.imagenUris.isEmpty()) {
            primeraImg = tour.imagenUris.get(0);
        }

        if (primeraImg != null && !primeraImg.isEmpty()) {
            Glide.with(context)
                    .load(primeraImg)
                    .placeholder(R.drawable.ic_person)
                    .into(holder.binding.imageTour);
        } else {
            holder.binding.imageTour.setImageResource(R.drawable.ic_person);
        }

        holder.binding.buttonVerInformacion.setOnClickListener(v -> {
            Intent intent = new Intent(context, Vista_Detalles_Tour.class);
            intent.putExtra("id", tour.id); // importante: abrir por id real
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // --- Lógica del guía ---
        if (tour.estado == TourEstado.PENDIENTE_GUIA && (tour.guiaId == null || tour.guiaId.isEmpty())) {
            holder.binding.buttonAccionTour.setText("Solicitar tour");
            holder.binding.buttonAccionTour.setEnabled(true);
            holder.binding.buttonAccionTour.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, android.R.color.holo_green_dark)
            );

            holder.binding.buttonAccionTour.setOnClickListener(v -> solicitarComoGuia(tour));

        } else {
            holder.binding.buttonAccionTour.setText("No disponible");
            holder.binding.buttonAccionTour.setEnabled(false);
            holder.binding.buttonAccionTour.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, android.R.color.darker_gray)
            );
            holder.binding.buttonAccionTour.setOnClickListener(null);
        }
    }

    private void solicitarComoGuia(Tour tour) {
        final String TAG = "TOUR_SOLICITAR";

        Log.d(TAG, "=== solicitarComoGuia() ===");

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        Log.d(TAG, "uid=" + uid
                + " tourId=" + (tour != null ? tour.id : "null"));

        if (uid == null) {
            Log.e(TAG, "ABORT: uid null (no hay sesión activa)");
            Toast.makeText(context, "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tour == null || tour.id == null || tour.id.isEmpty()) {
            Log.e(TAG, "ABORT: tour o tour.id null/vacío");
            Toast.makeText(context, "Tour sin ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "Iniciando transaction sobre tours/" + tour.id);

        db.runTransaction(transaction -> {

            var ref = db.collection("tours").document(tour.id);
            var snap = transaction.get(ref);

            if (!snap.exists()) {
                Log.e(TAG, "TX ABORT: documento no existe");
                throw new IllegalStateException("El tour ya no existe.");
            }

            String estadoActual = snap.getString("estado");
            String guiaActual   = snap.getString("guiaId");

            Log.d(TAG, "TX READ: estadoActual=" + estadoActual
                    + " guiaActual=" + guiaActual);

            // ===== VALIDACIONES DE NEGOCIO =====
            if (!TourEstado.PENDIENTE_GUIA.name().equals(estadoActual)) {
                Log.w(TAG, "TX FAIL: estado != PENDIENTE_GUIA (" + estadoActual + ")");
                throw new IllegalStateException("Este tour ya no está disponible.");
            }

            if (guiaActual != null && !guiaActual.isEmpty()) {
                Log.w(TAG, "TX FAIL: guiaId ya asignado (" + guiaActual + ")");
                throw new IllegalStateException("Este tour ya fue tomado por otro guía.");
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("estado", TourEstado.SOLICITADO.name());
            updates.put("updatedAt", FieldValue.serverTimestamp());

            updates.put("guiaId", uid);
            // 👇 si tus rules exigen updatedAt, actívalo
            // updates.put("updatedAt", FieldValue.serverTimestamp());

            Log.d(TAG, "TX UPDATE PATCH=" + updates);

            transaction.update(ref, updates);

            Log.d(TAG, "TX UPDATE queued OK");

            return null;

        }).addOnSuccessListener(unused -> {
            Log.d(TAG, "SUCCESS: solicitud enviada tours/" + tour.id
                    + " guiaId=" + uid);
            Toast.makeText(context, "Solicitud enviada al admin", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {

            Log.e(TAG, "FAILURE solicitarComoGuia: " + e.getMessage(), e);

            // 🔥 CLAVE: identificar error exacto de Firestore
            if (e instanceof com.google.firebase.firestore.FirebaseFirestoreException) {
                com.google.firebase.firestore.FirebaseFirestoreException fe =
                        (com.google.firebase.firestore.FirebaseFirestoreException) e;
                Log.e(TAG, "Firestore code=" + fe.getCode()
                        + " (" + fe.getCode().name() + ")");
            }

            Toast.makeText(context,
                    e.getMessage() != null ? e.getMessage() : "Error al solicitar tour",
                    Toast.LENGTH_LONG).show();
        });
    }



    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        ItemTourBinding binding;

        public TourViewHolder(@NonNull ItemTourBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateData(List<Tour> nuevaLista) {
        tourList.clear();
        tourList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

}
