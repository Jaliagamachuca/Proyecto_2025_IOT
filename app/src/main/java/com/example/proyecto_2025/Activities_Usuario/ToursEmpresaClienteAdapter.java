package com.example.proyecto_2025.Activities_Usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ItemTourClienteBinding;

import java.util.List;

public class ToursEmpresaClienteAdapter extends RecyclerView.Adapter<ToursEmpresaClienteAdapter.VH> {

    public interface Listener {
        void onReservar(ToursPorEmpresaActivity.TourItem tour);
        void onVerDetalle(ToursPorEmpresaActivity.TourItem tour);
    }

    private final List<ToursPorEmpresaActivity.TourItem> data;
    private final Listener listener;

    public ToursEmpresaClienteAdapter(List<ToursPorEmpresaActivity.TourItem> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTourClienteBinding b = ItemTourClienteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ToursPorEmpresaActivity.TourItem t = data.get(position);

        h.b.tvTitulo.setText(t.titulo != null ? t.titulo : "Tour");
        h.b.tvDesc.setText(t.descripcionCorta != null ? t.descripcionCorta : "");
        h.b.tvPrecio.setText("S/ " + String.format("%.2f", t.precio));
        h.b.tvCupos.setText("Cupos: " + t.cupos);

        if (t.imagenUrl != null && !t.imagenUrl.trim().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(t.imagenUrl)
                    .placeholder(R.drawable.macchupicchu)   // pon tu placeholder
                    .error(R.drawable.macchupicchu)
                    .into(h.b.ivTour);
        } else {
            h.b.ivTour.setImageResource(R.drawable.macchupicchu);
        }

        h.b.btnReservar.setOnClickListener(v -> {
            if (listener != null) listener.onReservar(t);
        });

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onVerDetalle(t);
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemTourClienteBinding b;
        VH(ItemTourClienteBinding b) { super(b.getRoot()); this.b = b; }
    }
}
