package com.example.proyecto_2025.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Tour;

import java.util.ArrayList;
import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.VH> {

    public interface OnItemClick { void onClick(Tour t); }
    public interface OnMoreClick { void onMore(Tour t, View anchor); }

    private final List<Tour> data;
    private final OnItemClick onItemClick;
    private final OnMoreClick onMoreClick;

    public TourAdapter(List<Tour> data, OnItemClick onItemClick, OnMoreClick onMoreClick) {
        this.data = data; this.onItemClick = onItemClick; this.onMoreClick = onMoreClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_tour_card, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Tour t = data.get(i);

        h.tvTitulo.setText(t.titulo == null ? "(Sin título)" : t.titulo);
        h.tvPrecio.setText(t.precioTexto());
        h.tvCupos.setText("Cupos: " + t.cupos);

        // estado
        if (h.chipEstado != null && t.estado != null) {
            h.chipEstado.setText(t.estado.name().replace("_", " "));

            int colorRes;
            switch (t.estado) {
                case PUBLICADO:
                    colorRes = R.color.estado_publicado;
                    break;
                case EN_CURSO:
                    colorRes = R.color.estado_en_curso;
                    break;
                case BORRADOR:
                    colorRes = R.color.estado_borrador;
                    break;
                default:
                    colorRes = R.color.estado_pendiente;
                    break;
            }
            h.chipEstado.setChipBackgroundColorResource(colorRes);
        }

        // portada
        if (!t.imagenUris.isEmpty()) {
            h.ivPortada.setImageURI(Uri.parse(t.imagenUris.get(0)));
        } else {
            h.ivPortada.setImageResource(R.drawable.ic_image_placeholder);
        }

        // 🚩 NUEVO: servicios adicionales (desayuno / almuerzo / cena)
        List<String> extras = new ArrayList<>();
        if (Boolean.TRUE.equals(t.isIncluyeDesayuno())) extras.add("desayuno");
        if (Boolean.TRUE.equals(t.isIncluyeAlmuerzo())) extras.add("almuerzo");
        if (Boolean.TRUE.equals(t.isIncluyeCena())) extras.add("cena");

        String serviciosText = extras.isEmpty()
                ? "Sin servicios adicionales"
                : "Incluye: " + TextUtils.join(", ", extras);

        h.tvServicios.setText(serviciosText);

        // listeners
        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(t);
        });
        h.btnMore.setOnClickListener(v -> {
            if (onMoreClick != null) onMoreClick.onMore(t, h.btnMore);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPortada, btnMore;
        TextView tvTitulo, tvPrecio, tvCupos, tvServicios;
        com.google.android.material.chip.Chip chipEstado;

        VH(@NonNull View v){
            super(v);
            ivPortada  = v.findViewById(R.id.ivPortada);
            tvTitulo   = v.findViewById(R.id.tvTitulo);
            tvPrecio   = v.findViewById(R.id.tvPrecio);
            tvCupos    = v.findViewById(R.id.tvCupos);
            tvServicios= v.findViewById(R.id.tvServicios);
            chipEstado = v.findViewById(R.id.chipEstado);
            btnMore    = v.findViewById(R.id.btnMore);
        }
    }
}
