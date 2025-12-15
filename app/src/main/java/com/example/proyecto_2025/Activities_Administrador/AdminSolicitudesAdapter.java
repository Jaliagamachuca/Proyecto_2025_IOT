package com.example.proyecto_2025.Activities_Administrador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.databinding.ItemSolicitudGuiaBinding;
import com.example.proyecto_2025.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class AdminSolicitudesAdapter extends RecyclerView.Adapter<AdminSolicitudesAdapter.VH> {

    private final Context ctx;
    private final List<Tour> data;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminSolicitudesAdapter(Context ctx, List<Tour> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSolicitudGuiaBinding b = ItemSolicitudGuiaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Tour t = data.get(position);

        // 1) Tour (título)
        String titulo = (t.titulo != null && !t.titulo.trim().isEmpty()) ? t.titulo : "(sin título)";
        h.b.tvNombreGuia.setText("Tour: " + titulo);

        // 2) Pago guía
        // Ajusta el nombre del campo según tu Tour (en tu AssignGuideActivity usas propuestaPagoGuia)
        double pago = 0.0;
        try { pago = t.propuestaPagoGuia; } catch (Exception ignore) {}
        h.b.tvCorreoGuia.setText(String.format("Pago guía: S/ %.2f", pago));

        // 3) Estado (SOLICITADO, etc.)
        String estadoTxt = "—";
        if (t.estado != null) {
            try { estadoTxt = t.estado.name(); } catch (Exception e) { estadoTxt = String.valueOf(t.estado); }
        }
        h.b.tvEstadoSolicitud.setText(estadoTxt);

        // 4) Guía: por defecto (para evitar "saltos" por el RecyclerView)
        h.b.tvDniGuia.setText("Guía: (pendiente)");

        // 5) Buscar nombre del guía SOLO si hay guiaId
        if (t.guiaId != null && !t.guiaId.trim().isEmpty()) {
            final String guiaId = t.guiaId;   // captura segura

            db.collection("users").document(guiaId).get()
                    .addOnSuccessListener(doc -> {
                        // evita que un callback viejo escriba en otra tarjeta reciclada
                        int pos = h.getBindingAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) return;
                        Tour current = data.get(pos);
                        if (current == null || current.guiaId == null || !current.guiaId.equals(guiaId)) return;

                        String nombre = doc.getString("displayName");
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            h.b.tvDniGuia.setText("Guía: " + nombre);
                        } else {
                            h.b.tvDniGuia.setText("Guía: (pendiente)");
                        }
                    })
                    .addOnFailureListener(err -> {
                        h.b.tvDniGuia.setText("Guía: (pendiente)");
                    });
        }

        h.b.btnRevisarGuia.setOnClickListener(v -> {
            Intent i = new Intent(ctx, AssignGuideActivity.class);
            i.putExtra("tourId", t.id);
            ctx.startActivity(i);
        });
    }




    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemSolicitudGuiaBinding b;
        VH(ItemSolicitudGuiaBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
