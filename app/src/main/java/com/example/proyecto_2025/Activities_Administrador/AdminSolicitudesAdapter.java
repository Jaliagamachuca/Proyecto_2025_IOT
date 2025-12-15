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

        h.b.tvNombreGuia.setText(t.titulo != null ? t.titulo : "(sin título)");

        // placeholders por recycle
        h.b.tvDniGuia.setText("Guía: —");
        h.b.tvCorreoGuia.setText("Tel: —");

        // Empresa (si no tienes otro TextView, al menos no lo mezcles con Tel)
        // Si quieres mostrar empresa, ponlo en el nombre:
        if (t.empresaId != null && !t.empresaId.isEmpty()) {
            h.b.tvNombreGuia.setText((t.titulo != null ? t.titulo : "(sin título)") + " · " + t.empresaId);
        }

        // Estado (funciona si es enum o string)
        String estadoTxt = "—";
        try {
            // si es enum
            estadoTxt = (t.estado != null) ? t.estado.name() : "—";
        } catch (Exception ignore) {
            // si es string
            try {
                estadoTxt = (String) Tour.class.getField("estado").get(t);
            } catch (Exception ignored2) {}
        }
        h.b.tvEstadoSolicitud.setText(estadoTxt);

        // Datos del guía
        if (t.guiaId != null && !t.guiaId.isEmpty()) {
            db.collection("users").document(t.guiaId).get()
                    .addOnSuccessListener(doc -> {
                        String nombre = doc.getString("displayName");
                        String phone  = doc.getString("phone");

                        h.b.tvDniGuia.setText("Guía: " + (nombre != null && !nombre.isEmpty() ? nombre : t.guiaId));
                        h.b.tvCorreoGuia.setText("Tel: " + (phone != null && !phone.isEmpty() ? phone : "—"));
                    })
                    .addOnFailureListener(err -> {
                        h.b.tvDniGuia.setText("Guía UID: " + t.guiaId);
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
