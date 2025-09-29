package com.example.proyecto_2025.Activities_Usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;

import java.util.List;

public class ToursRecomendadosAdapter extends RecyclerView.Adapter<ToursRecomendadosAdapter.TourViewHolder> {

    private List<TourRecomendado> tours;
    private OnTourClickListener listener;

    public interface OnTourClickListener {
        void onTourClick(TourRecomendado tour);
    }

    public ToursRecomendadosAdapter(List<TourRecomendado> tours, OnTourClickListener listener) {
        this.tours = tours;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour_recomendado, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        holder.bind(tours.get(position));
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    class TourViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImagenTour;
        private TextView txtNombreTour, txtEmpresaTour, txtRatingTour, txtDuracionTour, txtPrecioTour;

        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagenTour = itemView.findViewById(R.id.ivImagenTour);
            txtNombreTour = itemView.findViewById(R.id.txtNombreTour);
            txtEmpresaTour = itemView.findViewById(R.id.txtEmpresaTour);
            txtRatingTour = itemView.findViewById(R.id.txtRatingTour);
            txtDuracionTour = itemView.findViewById(R.id.txtDuracionTour);
            txtPrecioTour = itemView.findViewById(R.id.txtPrecioTour);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTourClick(tours.get(position));
                    }
                }
            });
        }

        public void bind(TourRecomendado tour) {
            txtNombreTour.setText(tour.getNombre());
            txtEmpresaTour.setText(tour.getEmpresa());
            txtRatingTour.setText(String.valueOf(tour.getRating()));
            txtDuracionTour.setText(tour.getDuracion());
            txtPrecioTour.setText(tour.getPrecio());
            ivImagenTour.setImageResource(tour.getImagenResId());
        }
    }
}