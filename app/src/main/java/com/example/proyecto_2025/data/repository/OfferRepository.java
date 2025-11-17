package com.example.proyecto_2025.data.repository;

import com.example.proyecto_2025.model.Offer;
import java.util.*;

public class OfferRepository {
    private static OfferRepository instance;
    private final List<Offer> offers = new ArrayList<>();

    public static OfferRepository get(){ if(instance==null) instance=new OfferRepository(); return instance; }

    public void add(Offer o){ offers.add(o); }


    public List<Offer> byStatus(Offer.Status s){
        List<Offer> res=new ArrayList<>();
        for(Offer o:offers) if(o.getStatus()==s) res.add(o);
        return res;
    }
    public Offer byId(String id){ for(Offer o:offers) if(o.getId().equals(id)) return o; return null; }

    public Offer findById(String id) {
        for (Offer o : offers) {
            if (o.getId().equals(id)) return o;
        }
        return null;
    }

    public void update(Offer offer) {
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).getId().equals(offer.getId())) {
                offers.set(i, offer);
                return;
            }
        }
    }

    public List<Offer> all(){ return new ArrayList<>(offers); }
}
