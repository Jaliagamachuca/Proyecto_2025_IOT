package com.example.proyecto_2025.Activities_Guia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ItemTourBinding;

import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private final List<Tour> tourList;
    private final Context context;

    public TourAdapter(Context context, List<Tour> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTourBinding binding = ItemTourBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TourViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);

        // 🔹 Mostrar nombre del tour
        holder.binding.textNombreTour.setText(tour.getNombreTour());

        // 🔹 Cargar imagen
        if (tour.getFotoUrl() != null && !tour.getFotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(tour.getFotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(holder.binding.imageTour);
        } else {
            holder.binding.imageTour.setImageResource(R.drawable.ic_person);
        }

        holder.binding.buttonVerInformacion.setOnClickListener(v -> {
            Intent intent = new Intent(context, Vista_Detalles_Tour.class);
            intent.putExtra("tour_seleccionado", tour); // usa esta clave
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // 🔹 Botón principal según subEstado
        if ("solicitado".equalsIgnoreCase(tour.getSubEstado())) {
            holder.binding.buttonAccionTour.setText("Rechazar");
            holder.binding.buttonAccionTour.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_red_dark)
            );
            holder.binding.buttonAccionTour.setOnClickListener(v -> {
                tour.setSubEstado("no solicitado");
                notifyItemChanged(position);
                Toast.makeText(context, "Has cancelado tu solicitud para: " + tour.getNombreTour(), Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.binding.buttonAccionTour.setText("Solicitar");
            holder.binding.buttonAccionTour.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            holder.binding.buttonAccionTour.setOnClickListener(v -> {
                tour.setSubEstado("solicitado");
                notifyItemChanged(position);
                Toast.makeText(context, "Has solicitado el tour: " + tour.getNombreTour(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        ItemTourBinding binding;

        public TourViewHolder(@NonNull ItemTourBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
