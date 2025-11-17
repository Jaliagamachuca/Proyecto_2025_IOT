package com.example.proyecto_2025.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proyecto_2025.model.Tour;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TourRepository {
    private static final String PREF = "tour_repo";
    private static final String KEY  = "tour_list";
    private final SharedPreferences sp;
    private final Gson gson = new Gson();

    public TourRepository(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public synchronized List<Tour> findAll() {
        String json = sp.getString(KEY, "");
        if (json == null || json.isEmpty()) return new ArrayList<>();
        Type type = new TypeToken<List<Tour>>(){}.getType();
        try { return gson.fromJson(json, type); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private synchronized void saveAll(List<Tour> tours) {
        sp.edit().putString(KEY, gson.toJson(tours)).apply();
    }

    public synchronized void upsert(Tour t) {
        List<Tour> list = findAll();
        int idx = -1;
        for (int i = 0; i < list.size(); i++) if (list.get(i).id.equals(t.id)) { idx = i; break; }
        if (idx >= 0) list.set(idx, t); else list.add(t);
        saveAll(list);
    }

    public synchronized void delete(String id) {
        List<Tour> list = findAll();
        list.removeIf(t -> t.id.equals(id));
        saveAll(list);
    }

    public synchronized Tour findById(String id) {
        for (Tour t : findAll()) if (t.id.equals(id)) return t;
        return null;
    }

    public synchronized List<Tour> findLastN(int n) {
        List<Tour> list = findAll();
        Collections.reverse(list);
        return list.subList(0, Math.min(n, list.size()));
    }
}
