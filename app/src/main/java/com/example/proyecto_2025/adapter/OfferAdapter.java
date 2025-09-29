package com.example.proyecto_2025.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.GuideRepository;
import com.example.proyecto_2025.data.TourRepository;
import com.example.proyecto_2025.model.Offer;
import java.text.SimpleDateFormat;
import java.util.*;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.VH> {
    public interface OnAction { void onAssign(Offer o); void onDetail(Offer o); }
    private final List<Offer> data; private final Context ctx; private final OnAction listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());

    public OfferAdapter(Context c, List<Offer> d, OnAction l){ctx=c; data=d; listener=l;}

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_offer_card, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i) {
        Offer o = data.get(i);
        String gName = GuideRepository.get().byId(o.getGuideId()).getName();
        String tTitle = o.getTourId();        h.title.setText(tTitle);
        h.subtitle.setText(gName+" • "+o.getPayMode()+" • S/"+String.format(Locale.getDefault(),"%.2f", o.getAmount()));
        h.dates.setText(sdf.format(o.getStartDate())+" — "+sdf.format(o.getEndDate()));
        h.status.setText(o.getStatus().name());

        h.btnDetail.setOnClickListener(v->listener.onDetail(o));
        h.btnAssign.setVisibility(o.getStatus()== Offer.Status.ACEPTADA ? View.VISIBLE : View.GONE);
        h.btnAssign.setOnClickListener(v->listener.onAssign(o));
    }

    @Override public int getItemCount(){ return data.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView title, subtitle, dates, status; Button btnDetail, btnAssign;
        VH(@NonNull View v){
            super(v);
            title=v.findViewById(R.id.tvOfferTitle);
            subtitle=v.findViewById(R.id.tvOfferSub);
            dates=v.findViewById(R.id.tvOfferDates);
            status=v.findViewById(R.id.tvOfferStatus);
            btnDetail=v.findViewById(R.id.btnOfferDetail);
            btnAssign=v.findViewById(R.id.btnOfferAssign);
        }
    }
}
