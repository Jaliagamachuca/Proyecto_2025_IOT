package com.example.proyecto_2025.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.model.Guide;
import java.util.*;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.VH> {
    public interface OnAction {
        void onProfile(Guide g);
        void onOffer(Guide g);
    }
    private final Context ctx;
    private final List<Guide> data;
    private final OnAction listener;

    public GuideAdapter(Context ctx, List<Guide> data, OnAction l){
        this.ctx=ctx; this.data=data; this.listener=l;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_guide_card, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i) {
        Guide g = data.get(i);
        h.name.setText(g.getName());
        h.zone.setText(g.getZone());
        h.rating.setText(String.format(Locale.getDefault(),"%.1f ★", g.getRating()));
        h.langs.setText(g.getLanguages().toString().replace("[","").replace("]","").replace(","," · "));
        Glide.with(ctx).load(g.getPhotoUrl()).into(h.photo);
        h.btnProfile.setOnClickListener(v-> listener.onProfile(g));
        h.btnOffer.setOnClickListener(v-> listener.onOffer(g));
    }

    @Override public int getItemCount(){ return data.size(); }

    static class VH extends RecyclerView.ViewHolder{
        ImageView photo; TextView name, zone, rating, langs; Button btnProfile, btnOffer;
        VH(@NonNull View v){
            super(v);
            photo=v.findViewById(R.id.imgGuide);
            name=v.findViewById(R.id.tvName);
            zone=v.findViewById(R.id.tvZone);
            rating=v.findViewById(R.id.tvRating);
            langs=v.findViewById(R.id.tvLangs);
            //btnProfile=v.findViewById(R.id.btnProfile);
            btnOffer=v.findViewById(R.id.btnOffer);
        }
    }
}
