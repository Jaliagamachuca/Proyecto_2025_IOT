package com.example.proyecto_2025.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Tour;

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

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_tour_card, p, false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i) {
        Tour t = data.get(i);
        h.tvTitulo.setText(t.titulo == null ? "(Sin título)" : t.titulo);
        h.tvPrecio.setText(t.precioTexto());
        if (h.chipEstado != null && t.estado != null) {
            h.chipEstado.setText(t.estado.name().replace("_", " "));

            // Cambiar color según el estado
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

        h.tvCupos.setText("Cupos: " + t.cupos);

        // portada simple: primera imagen si existe
        if (!t.imagenUris.isEmpty()) h.ivPortada.setImageURI(Uri.parse(t.imagenUris.get(0)));
        else h.ivPortada.setImageResource(R.drawable.ic_image_placeholder);

        h.itemView.setOnClickListener(v -> { if (onItemClick != null) onItemClick.onClick(t); });
        h.btnMore.setOnClickListener(v -> { if (onMoreClick != null) onMoreClick.onMore(t, h.btnMore); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPortada, btnMore;
        TextView tvTitulo, tvPrecio, tvEstado, tvCupos;
        com.google.android.material.chip.Chip chipEstado;

        VH(@NonNull View v){
            super(v);
            ivPortada = v.findViewById(R.id.ivPortada);
            tvTitulo  = v.findViewById(R.id.tvTitulo);
            tvPrecio  = v.findViewById(R.id.tvPrecio);
            chipEstado = v.findViewById(R.id.chipEstado);
            tvCupos   = v.findViewById(R.id.tvCupos);
            btnMore   = v.findViewById(R.id.btnMore);
        }
    }
}
