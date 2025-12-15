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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.PuntoRuta;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.VH> {

    public interface OnItemClick { void onClick(Tour t); }
    public interface OnMoreClick { void onMore(Tour t, View anchor); }

    private final List<Tour> data;
    private final OnItemClick onItemClick;
    private final OnMoreClick onMoreClick;

    public TourAdapter(List<Tour> data, OnItemClick onItemClick, OnMoreClick onMoreClick) {
        this.data = data != null ? data : new ArrayList<>();
        this.onItemClick = onItemClick;
        this.onMoreClick = onMoreClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_tour_card, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Tour t = data.get(i);

        // ===== Título y precio =====
        String titulo = (t.titulo == null || t.titulo.isEmpty())
                ? "(Sin título)"
                : t.titulo;
        h.tvTitulo.setText(titulo);

        h.tvPrecio.setText(t.precioTexto());

        // ===== Cupos =====
        h.tvCupos.setText("Cupos: " + t.cupos);

        // ===== Estado (chip) =====
        if (h.chipEstado != null && t.estado != null) {
            TourEstado estado = t.estado;
            h.chipEstado.setText(estado.name().replace("_", " "));

            int colorRes;
            switch (estado) {
                case PUBLICADO:
                    colorRes = R.color.estado_publicado;
                    break;
                case EN_CURSO:
                    colorRes = R.color.estado_en_curso;
                    break;
                case BORRADOR:
                    colorRes = R.color.estado_borrador;
                    break;
                case FINALIZADO:
                    colorRes = R.color.estado_finalizado;
                    break;
                default:
                    colorRes = R.color.estado_pendiente;
                    break;
            }
            h.chipEstado.setChipBackgroundColorResource(colorRes);

            // Chip EN VIVO solo cuando está en curso
            if (h.chipEnVivo != null) {
                h.chipEnVivo.setVisibility(
                        estado == TourEstado.EN_CURSO ? View.VISIBLE : View.GONE
                );
            }
        }

        // ===== Portada (usar Glide para URLs remotas) =====
        String url = null;
        if (t.imagenUris != null && !t.imagenUris.isEmpty()) {
            url = t.imagenUris.get(0);
            if (url != null) url = url.trim();
        }

        Glide.with(h.itemView)
                .load((url == null || url.isEmpty()) ? null : url)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(h.ivPortada);

        // ===== Fechas =====
        if (t.fechaInicioUtc > 0 && t.fechaFinUtc > 0) {
            Date dIni = new Date(t.fechaInicioUtc);
            Date dFin = new Date(t.fechaFinUtc);
            SimpleDateFormat fDiaMes = new SimpleDateFormat("dd MMM", Locale.getDefault());
            SimpleDateFormat fAnio   = new SimpleDateFormat("yyyy", Locale.getDefault());

            String textoFechas = "📅 " + fDiaMes.format(dIni) + " - "
                    + fDiaMes.format(dFin) + " " + fAnio.format(dFin);
            h.tvFechas.setText(textoFechas);
        } else {
            h.tvFechas.setText("📅 Fechas por definir");
        }

        // ===== Duración REAL usando fechaInicioUtc y fechaFinUtc =====
        if (t.fechaInicioUtc > 0 && t.fechaFinUtc > 0 && t.fechaFinUtc > t.fechaInicioUtc) {

            long diffMs = t.fechaFinUtc - t.fechaInicioUtc;
            long diffMin = diffMs / (1000 * 60);

            long dias = diffMin / (60 * 24);
            long horas = (diffMin % (60 * 24)) / 60;
            long mins = diffMin % 60;

            String duracion;

            if (dias > 0) {
                duracion = dias + " día" + (dias > 1 ? "s " : " ");
                if (horas > 0) duracion += horas + " h ";
                if (mins > 0) duracion += mins + " min";
            } else if (horas > 0) {
                duracion = horas + " h " + (mins > 0 ? mins + " min" : "");
            } else {
                duracion = mins + " min";
            }

            h.tvDuracion.setText("⏱️ " + duracion.trim());

        } else {
            h.tvDuracion.setText("⏱️ Duración no definida");
        }

        // ===== Servicios adicionales =====
        List<String> extras = new ArrayList<>();
        if (Boolean.TRUE.equals(t.isIncluyeDesayuno())) extras.add("desayuno");
        if (Boolean.TRUE.equals(t.isIncluyeAlmuerzo())) extras.add("almuerzo");
        if (Boolean.TRUE.equals(t.isIncluyeCena())) extras.add("cena");

        String serviciosText = extras.isEmpty()
                ? "Sin servicios adicionales"
                : "Incluye: " + TextUtils.join(", ", extras);
        h.tvServicios.setText(serviciosText);

        // ===== Estado del guía =====
        if (t.guiaId == null || t.guiaId.isEmpty()) {
            h.tvGuiaEstado.setText("👤 Sin guía");
        } else {
            h.tvGuiaEstado.setText("👤 Guía asignado");
        }

        // ===== Listeners =====
        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(t);
        });

        h.btnMore.setOnClickListener(v -> {
            if (onMoreClick != null) onMoreClick.onMore(t, h.btnMore);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPortada, btnMore;
        TextView tvTitulo, tvPrecio, tvCupos, tvServicios, tvDuracion, tvFechas, tvGuiaEstado;
        Chip chipEstado, chipEnVivo;

        VH(@NonNull View v){
            super(v);
            ivPortada   = v.findViewById(R.id.ivPortada);
            btnMore     = v.findViewById(R.id.btnMore);
            tvTitulo    = v.findViewById(R.id.tvTitulo);
            tvPrecio    = v.findViewById(R.id.tvPrecio);
            tvCupos     = v.findViewById(R.id.tvCupos);
            tvServicios = v.findViewById(R.id.tvServicios);
            tvDuracion  = v.findViewById(R.id.tvDuracion);
            tvFechas    = v.findViewById(R.id.tvFechas);
            tvGuiaEstado= v.findViewById(R.id.tvGuiaEstado);
            chipEstado  = v.findViewById(R.id.chipEstado);
            chipEnVivo  = v.findViewById(R.id.chipEnVivo);
        }
    }

    public void replaceData(List<Tour> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }
}
