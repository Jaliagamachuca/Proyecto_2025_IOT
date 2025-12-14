package com.example.proyecto_2025.Activities_Administrador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.databinding.ItemSolicitudGuiaBinding;
import com.example.proyecto_2025.model.Tour;

import java.util.List;

public class AdminSolicitudesAdapter extends RecyclerView.Adapter<AdminSolicitudesAdapter.VH> {

    private final Context ctx;
    private final List<Tour> data;

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

        // Mapea Tour -> lo que quieres mostrar en la card
        h.b.tvNombreGuia.setText(t.titulo != null ? t.titulo : "(sin título)");
        h.b.tvCorreoGuia.setText(t.empresaId != null ? ("Empresa: " + t.empresaId) : "Empresa: —");
        h.b.tvDniGuia.setText(t.guiaId != null ? ("Guía UID: " + t.guiaId) : "Guía UID: —");
        h.b.tvEstadoSolicitud.setText(t.estado != null ? t.estado.name() : "—");

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
