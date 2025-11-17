package com.example.proyecto_2025.data.repository;

import android.content.Context;
import com.example.proyecto_2025.model.Guide;
import java.util.*;

public class GuideRepository {
    private static GuideRepository instance;
    private final List<Guide> guides = new ArrayList<>();

    private GuideRepository(){}

    public static GuideRepository get(){
        if (instance == null) instance = new GuideRepository();
        return instance;
    }

    /** Carga data de demo si no hay guías aún */
    public void seedIfEmpty(Context ctx){
        if (!guides.isEmpty()) return;
        guides.add(new Guide("g1","María Quispe",
                Arrays.asList(Guide.Language.ES, Guide.Language.EN),
                "https://picsum.photos/seed/guide1/400",
                "Guía certificada con 5 años en Cusco y Valle Sagrado.",4.8f,"Cusco"));
        guides.add(new Guide("g2","José Huamán",
                Arrays.asList(Guide.Language.ES, Guide.Language.FR),
                "https://picsum.photos/seed/guide2/400",
                "Especialista en turismo vivencial y trekking.",4.6f,"Valle Sagrado"));
        guides.add(new Guide("g3","Ana Poma",
                Arrays.asList(Guide.Language.ES, Guide.Language.EN, Guide.Language.DE),
                "https://picsum.photos/seed/guide3/400",
                "Arqueóloga y guía con enfoque histórico.",4.9f,"Machu Picchu"));
        guides.add(new Guide("g4","Luis Ccapa",
                Arrays.asList(Guide.Language.ES, Guide.Language.PT),
                "https://picsum.photos/seed/guide4/400",
                "Conductor-guía para tours full day.",4.5f,"Montaña de 7 Colores"));
        guides.add(new Guide("g5","Rosa Ñusta",
                Arrays.asList(Guide.Language.ES, Guide.Language.EN),
                "https://picsum.photos/seed/guide5/400",
                "Experta en gastronomía y cultura local.",4.7f,"Cusco Centro"));
    }

    public List<Guide> all(){ return new ArrayList<>(guides); }
    public Guide byId(String id){ for (Guide g: guides) if (g.getId().equals(id)) return g; return null; }
}
