package com.example.proyecto_2025.Activities_Usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;

import java.util.List;
import java.util.Locale;

public class ToursEmpresaAdapter extends RecyclerView.Adapter<ToursEmpresaAdapter.Holder> {

    public interface OnTourClick {
        void onClick(ToursPorEmpresaActivity.TourItem tour);
    }

    private final List<ToursPorEmpresaActivity.TourItem> data;
    private final OnTourClick listener;

    public ToursEmpresaAdapter(List<ToursPorEmpresaActivity.TourItem> data, OnTourClick listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour_empresa, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        var t = data.get(position);

        h.tvTitulo.setText(t.titulo != null ? t.titulo : "Tour");
        h.tvDesc.setText(t.descripcionCorta != null ? t.descripcionCorta : "");
        h.tvPrecio.setText(String.format(Locale.getDefault(), "S/ %.2f", t.precio));
        h.tvCupos.setText("Cupos: " + t.cupos);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDesc, tvPrecio, tvCupos;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloTour);
            tvDesc   = itemView.findViewById(R.id.tvDescTour);
            tvPrecio = itemView.findViewById(R.id.tvPrecioTour);
            tvCupos  = itemView.findViewById(R.id.tvCuposTour);
        }
    }
}
