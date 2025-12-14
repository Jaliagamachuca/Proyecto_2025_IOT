package com.example.proyecto_2025.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;

import java.util.List;

public class ImageUriAdapter extends RecyclerView.Adapter<ImageUriAdapter.Holder> {

    public interface OnRemove {
        void onRemove(int position);
    }

    private final List<Uri> data;
    private final OnRemove listener;

    public ImageUriAdapter(List<Uri> data, OnRemove listener) {
        this.data = data; this.listener = listener;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imagen_uri, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Uri u = data.get(pos);
        ImageView img = h.itemView.findViewById(R.id.img);
        View btnEliminar = h.itemView.findViewById(R.id.btnEliminar);

        try {
            img.setImageURI(u);

            // Si setImageURI no crashea pero igual no carga, al menos no mostramos null
            if (img.getDrawable() == null) {
                img.setImageResource(R.drawable.ic_image_placeholder);
            }

        } catch (SecurityException se) {
            img.setImageResource(R.drawable.ic_image_placeholder);
        } catch (Exception e) {
            img.setImageResource(R.drawable.ic_image_placeholder);
        }

        if (listener != null) {
            btnEliminar.setVisibility(View.VISIBLE);
            btnEliminar.setOnClickListener(
                    v -> listener.onRemove(h.getBindingAdapterPosition())
            );
        } else {
            btnEliminar.setVisibility(View.GONE);
            btnEliminar.setOnClickListener(null);
        }
    }

    @Override public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) { super(itemView); }
    }
}
