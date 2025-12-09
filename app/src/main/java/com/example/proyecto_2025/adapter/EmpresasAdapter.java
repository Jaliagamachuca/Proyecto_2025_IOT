package com.example.proyecto_2025.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.Activities_Usuario.EmpresaTurismo;
import com.example.proyecto_2025.R;

import java.util.List;

public class EmpresasAdapter extends RecyclerView.Adapter<EmpresasAdapter.EmpresaViewHolder> {

    private List<EmpresaTurismo> empresas;
    private OnEmpresaClickListener listener;

    // Interface para manejar clicks
    public interface OnEmpresaClickListener {
        void onEmpresaClick(EmpresaTurismo empresa);
        void onVerToursClick(EmpresaTurismo empresa);
    }

    // Constructor
    public EmpresasAdapter(List<EmpresaTurismo> empresas, OnEmpresaClickListener listener) {
        this.empresas = empresas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmpresaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empresa_turismo, parent, false);
        return new EmpresaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpresaViewHolder holder, int position) {
        EmpresaTurismo empresa = empresas.get(position);
        holder.bind(empresa);
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    // ViewHolder
    class EmpresaViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivLogoEmpresa;
        private TextView txtNombreEmpresa;
        private TextView txtUbicacionEmpresa;
        private RatingBar ratingEmpresa;
        private TextView txtRatingNumerico;
        private TextView txtNumeroResenias;
        private TextView txtToursDisponibles;
        private TextView txtDescripcionCorta;
        private Button btnVerTours;

        public EmpresaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar vistas
            ivLogoEmpresa = itemView.findViewById(R.id.ivLogoEmpresa);
            txtNombreEmpresa = itemView.findViewById(R.id.txtNombreEmpresa);
            txtUbicacionEmpresa = itemView.findViewById(R.id.txtUbicacionEmpresa);
            ratingEmpresa = itemView.findViewById(R.id.ratingEmpresa);
            txtRatingNumerico = itemView.findViewById(R.id.txtRatingNumerico);
            txtNumeroResenias = itemView.findViewById(R.id.txtNumeroResenias);
            txtToursDisponibles = itemView.findViewById(R.id.txtToursDisponibles);
            txtDescripcionCorta = itemView.findViewById(R.id.txtDescripcionCorta);
            btnVerTours = itemView.findViewById(R.id.btnVerTours);

            // Click en toda la card
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEmpresaClick(empresas.get(position));
                    }
                }
            });

            // Click en botón "Ver tours"
            btnVerTours.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onVerToursClick(empresas.get(position));
                    }
                }
            });
        }

        public void bind(EmpresaTurismo empresa) {
            // Asignar datos a las vistas
            txtNombreEmpresa.setText(empresa.getNombre());
            txtUbicacionEmpresa.setText(empresa.getUbicacion());
            txtDescripcionCorta.setText(empresa.getDescripcion());

            // Rating
            ratingEmpresa.setRating(empresa.getRating());
            txtRatingNumerico.setText(String.valueOf(empresa.getRating()));
            txtNumeroResenias.setText("(" + empresa.getNumeroResenias() + " reseñas)");

            // Tours disponibles
            txtToursDisponibles.setText(empresa.getToursDisponibles() + " tours disponibles");

            // Logo (usar el resource ID)
            ivLogoEmpresa.setImageResource(empresa.getLogoResId());
        }
    }

    // Método para actualizar la lista
    public void updateEmpresas(List<EmpresaTurismo> nuevasEmpresas) {
        this.empresas = nuevasEmpresas;
        notifyDataSetChanged();
    }
}