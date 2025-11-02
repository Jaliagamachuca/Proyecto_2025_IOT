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

        // 🔹 Nombre
        holder.binding.textNombreTour.setText(tour.getNombreTour());

        // 🔹 Imagen
        if (tour.getFotoUrl() != null && !tour.getFotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(tour.getFotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(holder.binding.imageTour);
        } else {
            holder.binding.imageTour.setImageResource(R.drawable.ic_person);
        }

        // 🔹 Botón "Ver información"
        holder.binding.buttonVerInformacion.setOnClickListener(v -> {
            Intent intent = new Intent(context, Vista_Detalles_Tour.class);
            intent.putExtra("tour_seleccionado", tour);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // 🔹 Lógica según estado general
        String estadoGeneral = tour.getEstadoGeneral() != null ? tour.getEstadoGeneral().toLowerCase() : "";

        switch (estadoGeneral) {
            case "disponible":
                configurarBotonDisponible(holder, tour, position);
                break;

            case "pendiente":
                configurarBotonPendiente(holder, tour, position);
                break;

            case "finalizado":
                configurarBotonFinalizado(holder, tour);
                break;

            default:
                holder.binding.buttonAccionTour.setText("Desconocido");
                holder.binding.buttonAccionTour.setBackgroundTintList(
                        context.getResources().getColorStateList(android.R.color.darker_gray)
                );
                holder.binding.buttonAccionTour.setOnClickListener(null);
                break;
        }
    }

    // 🔹 Tours DISPONIBLES → Solicitar / Rechazar
    private void configurarBotonDisponible(@NonNull TourViewHolder holder, Tour tour, int position) {
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

    // 🔹 Tours PENDIENTES → En Proceso / Iniciar
    private void configurarBotonPendiente(@NonNull TourViewHolder holder, Tour tour, int position) {
        if ("iniciado".equalsIgnoreCase(tour.getSubEstado())) {
            holder.binding.buttonAccionTour.setText("En Proceso");
            holder.binding.buttonAccionTour.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_orange_dark)
            );
            holder.binding.buttonAccionTour.setOnClickListener(v ->
                    Toast.makeText(context, "El tour ya está en proceso", Toast.LENGTH_SHORT).show()
            );
        } else {
            holder.binding.buttonAccionTour.setText("Iniciar");
            holder.binding.buttonAccionTour.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_green_dark)
            );
            holder.binding.buttonAccionTour.setOnClickListener(v -> {
                // 🔹 Aquí abre la nueva lista o actividad que tú decidirás
                Intent intent = new Intent(context, Guia_Tour_en_Proceso.class);
                intent.putExtra("tour", tour);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                // 🔹 Opcional: actualizar estado local
                tour.setSubEstado("iniciado");
                notifyItemChanged(position);
            });
        }
    }

    // 🔹 Tours FINALIZADOS → Mostrar pago
    private void configurarBotonFinalizado(@NonNull TourViewHolder holder, Tour tour) {
        holder.binding.buttonAccionTour.setEnabled(false);
        holder.binding.buttonAccionTour.setText(String.format("Pago: S/ %.2f", tour.getPagoOfrecido()));
        holder.binding.buttonAccionTour.setBackgroundTintList(
                context.getResources().getColorStateList(android.R.color.holo_blue_dark)
        );
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
