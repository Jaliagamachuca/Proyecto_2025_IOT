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

        // 1) Título del tour
        h.b.tvNombreGuia.setText(t.titulo != null ? t.titulo : "(sin título)");

        // 2) Empresa
        h.b.tvCorreoGuia.setText(t.empresaId != null ? ("Empresa: " + t.empresaId) : "Empresa: —");

        // 3) Datos del guía (si existe guiaId)
        if (t.guiaId != null && !t.guiaId.isEmpty()) {
            h.b.tvDniGuia.setText("Guía UID: " + t.guiaId);

            db.collection("users").document(t.guiaId).get()
                    .addOnSuccessListener(doc -> {
                        String nombre = doc.getString("displayName");
                        String phone  = doc.getString("phone");

                        if (nombre != null && !nombre.isEmpty()) {
                            h.b.tvDniGuia.setText("Guía: " + nombre);
                        }
                        if (phone != null && !phone.isEmpty()) {
                            h.b.tvCorreoGuia.setText("Tel: " + phone);
                        }
                    })
                    .addOnFailureListener(err -> {
                        // deja el UID si falla
                    });
        } else {
            h.b.tvDniGuia.setText("Guía UID: —");
        }

        // 4) Estado
        h.b.tvEstadoSolicitud.setText(t.estado != null ? t.estado.name() : "—");

        // 5) Botón revisar
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
