package com.example.proyecto_2025.Activities_Usuario;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import java.util.List;

public class ItinerarioAdapter extends RecyclerView.Adapter<ItinerarioAdapter.ViewHolder> {

    private List<LugarItinerario> lugares;

    public ItinerarioAdapter(List<LugarItinerario> lugares) {
        this.lugares = lugares;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugar_itinerario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LugarItinerario lugar = lugares.get(position);

        holder.txtNombreLugar.setText(lugar.getNombreLugar());
        holder.txtHoraLugar.setText(lugar.getHora());

        // Cambiar el estado y color según el lugar
        if (lugar.isActual()) {
            holder.txtEstadoLugar.setText("Visitando ahora");
            holder.txtEstadoLugar.setTextColor(holder.itemView.getContext().getColor(R.color.primary_color));
            holder.indicadorEstado.setBackgroundResource(R.drawable.circle_background);
        } else if (lugar.isVisitado()) {
            holder.txtEstadoLugar.setText("Completado");
            holder.txtEstadoLugar.setTextColor(holder.itemView.getContext().getColor(R.color.success_color));
            holder.indicadorEstado.setBackgroundResource(R.drawable.circle_background);
        } else {
            holder.txtEstadoLugar.setText("Pendiente");
            holder.txtEstadoLugar.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            holder.indicadorEstado.setAlpha(0.3f);
        }
    }

    @Override
    public int getItemCount() {
        return lugares.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View indicadorEstado;
        TextView txtNombreLugar, txtEstadoLugar, txtHoraLugar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            indicadorEstado = itemView.findViewById(R.id.indicadorEstado);
            txtNombreLugar = itemView.findViewById(R.id.txtNombreLugar);
            txtEstadoLugar = itemView.findViewById(R.id.txtEstadoLugar);
            txtHoraLugar = itemView.findViewById(R.id.txtHoraLugar);
        }
    }
}