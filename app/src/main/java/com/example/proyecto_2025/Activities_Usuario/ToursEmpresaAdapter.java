package com.example.proyecto_2025.Activities_Usuario;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class ToursEmpresaAdapter extends RecyclerView.Adapter<ToursEmpresaAdapter.Holder> {

    public interface Listener {
        void onVerDetalle(ToursPorEmpresaActivity.TourItem tour);
        void onReservar(ToursPorEmpresaActivity.TourItem tour);
    }

    private final List<ToursPorEmpresaActivity.TourItem> data;
    private final Listener listener;

    public ToursEmpresaAdapter(List<ToursPorEmpresaActivity.TourItem> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour_empresa, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        ToursPorEmpresaActivity.TourItem t = data.get(position);

        h.tvTitulo.setText(t.titulo != null ? t.titulo : "Tour");
        h.tvDesc.setText(t.descripcionCorta != null ? t.descripcionCorta : "");
        h.tvPrecio.setText(String.format(Locale.getDefault(), "S/ %.2f", t.precio));
        h.tvCupos.setText("Cupos: " + t.cupos);

        // ✅ Imagen (portada)
        if (t.imagenUrl != null && !t.imagenUrl.trim().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(t.imagenUrl)
                    .centerCrop()
                    .placeholder(R.drawable.macchupicchu)
                    .error(R.drawable.macchupicchu)
                    .into(h.ivPortada);
        } else {
            h.ivPortada.setImageResource(R.drawable.macchupicchu);
        }

        // ✅ Si no hay cupos, deshabilita reservar (UX)
        boolean disponible = t.cupos > 0;
        h.btnReservar.setEnabled(disponible);
        h.btnReservar.setAlpha(disponible ? 1f : 0.55f);

        // ✅ Evitar doble click rápido
        h.btnReservar.setOnClickListener(v -> {
            if (listener == null) return;
            h.btnReservar.setEnabled(false);
            listener.onReservar(t);
            // Nota: re-habilitar se hace cuando la Activity confirme éxito/fracaso.
            // Si quieres, lo re-habilito aquí con postDelayed, pero es mejor hacerlo con resultado real.
        });

        // ✅ Ver detalle (tap en card)
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onVerDetalle(t);
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView ivPortada;
        TextView tvTitulo, tvDesc, tvPrecio, tvCupos;
        MaterialButton btnReservar;

        @SuppressLint("WrongViewCast")
        public Holder(@NonNull View itemView) {
            super(itemView);
            ivPortada   = itemView.findViewById(R.id.ivTourPortada);
            tvTitulo    = itemView.findViewById(R.id.tvTituloTour);
            tvDesc      = itemView.findViewById(R.id.tvDescTour);
            tvPrecio    = itemView.findViewById(R.id.tvPrecioTour);
            tvCupos     = itemView.findViewById(R.id.tvCuposTour);
            btnReservar = itemView.findViewById(R.id.btnReservarTour);
        }
    }
}
