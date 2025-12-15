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

    }

    public List<Guide> all(){ return new ArrayList<>(guides); }
    public Guide byId(String id){ for (Guide g: guides) if (g.getId().equals(id)) return g; return null; }
}
