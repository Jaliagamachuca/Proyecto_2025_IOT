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

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ViewHolder> {

    private List<Reserva> reservas;

    public ReservasAdapter(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        holder.txtNombreTour.setText(reserva.getNombreTour());
        holder.txtEmpresa.setText(reserva.getEmpresa());
        holder.txtFecha.setText(reserva.getFecha());
        holder.txtEstado.setText(reserva.getEstado());
        holder.ivImagen.setImageResource(reserva.getImagenResId());

        // Cambiar color según estado
        int colorResId;
        if (reserva.getEstado().equals("Completada")) {
            colorResId = R.color.success_color;
        } else if (reserva.getEstado().equals("Cancelada")) {
            colorResId = R.color.error_color;
        } else {
            colorResId = R.color.primary_color;
        }
        holder.txtEstado.setTextColor(holder.itemView.getContext().getColor(colorResId));
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView txtNombreTour, txtEmpresa, txtFecha, txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagenReserva);
            txtNombreTour = itemView.findViewById(R.id.txtNombreTourReserva);
            txtEmpresa = itemView.findViewById(R.id.txtEmpresaReserva);
            txtFecha = itemView.findViewById(R.id.txtFechaReserva);
            txtEstado = itemView.findViewById(R.id.txtEstadoReserva);
        }
    }
}