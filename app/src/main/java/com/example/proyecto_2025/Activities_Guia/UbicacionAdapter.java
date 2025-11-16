package com.example.proyecto_2025.Activities_Guia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;

import java.util.List;

public class UbicacionAdapter extends RecyclerView.Adapter<UbicacionAdapter.ViewHolder> {

    private List<Ubicacion> lista;
    private OnUbicacionClickListener listener;

    public interface OnUbicacionClickListener {
        void onRegistrarClick(Ubicacion u);
    }

    public UbicacionAdapter(List<Ubicacion> lista, OnUbicacionClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ubicacion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ubicacion u = lista.get(position);

        // Nombre de la ubicación
        holder.txtNombre.setText(u.getNombre());

        // Coordenadas
        holder.txtCoord.setText("Lat: " + u.getLatitud() + " | Lng: " + u.getLongitud());

        // Imagen aleatoria estilo usuario, pero para ubicaciones
        Glide.with(holder.itemView.getContext())
                .load("https://picsum.photos/seed/location" + position + "/200")
                .placeholder(R.drawable.ic_person)   // icono por defecto
                .into(holder.imgUbicacion);

        // Acción del botón
        holder.btnRegistrar.setOnClickListener(v -> {
            listener.onRegistrarClick(u);

            // Cambiar a modo "registrado"
            holder.btnRegistrar.setText("Registrado ✔");
            holder.btnRegistrar.setEnabled(false);
            holder.btnRegistrar.setBackgroundTintList(
                    holder.itemView.getContext().getColorStateList(R.color.accent_color)
            );
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCoord;
        Button btnRegistrar;

        ImageView imgUbicacion;   // <-- AQUÍ VA

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreUbicacion);
            txtCoord = itemView.findViewById(R.id.txtCoordenadas);
            btnRegistrar = itemView.findViewById(R.id.btnRegistrarUbicacion);
            imgUbicacion = itemView.findViewById(R.id.imgUbicacion);  // <-- Y AQUI
        }
    }
}

