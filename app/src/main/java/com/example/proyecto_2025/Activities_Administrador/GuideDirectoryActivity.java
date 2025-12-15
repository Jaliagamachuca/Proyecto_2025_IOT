package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.GuideAdapter;
import com.example.proyecto_2025.model.Guide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuideDirectoryActivity extends AppCompatActivity implements GuideAdapter.OnAction {

    private static final String TAG = "GuideDirectoryActivity";

    private final List<Guide> base = new ArrayList<>(); // full list from Firestore
    private final List<Guide> view = new ArrayList<>(); // filtered list for UI
    private GuideAdapter adapter;

    private EditText etSearch;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.guide_directory);

        RecyclerView rv = findViewById(R.id.rvGuides);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuideAdapter(this, view, this);
        rv.setAdapter(adapter);

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s == null ? "" : s.toString());
            }
        });

        loadGuidesFromFirestore();
    }

    private void loadGuidesFromFirestore() {
        db.collection("users")
                .whereEqualTo("role", "guia")
                .get()
                .addOnSuccessListener(snap -> {
                    base.clear();

                    for (var doc : snap.getDocuments()) {
                        Guide g = doc.toObject(Guide.class);
                        if (g == null) g = new Guide();

                        // IMPORTANT: document id is the uid
                        g.uid = doc.getId();

                        // Optional safety mapping in case Firestore field names differ
                        // (with your current Guide model they should match)
                        if (g.displayName == null) g.displayName = doc.getString("displayName");
                        if (g.phone == null) g.phone = doc.getString("phone");
                        if (g.photoUrl == null) g.photoUrl = doc.getString("photoUrl");

                        // ratingPromedio is the real field you showed
                        Double rp = doc.getDouble("ratingPromedio");
                        if (rp != null) g.ratingPromedio = rp.floatValue();

                        // totals (if present)
                        Long tv = doc.getLong("totalValoraciones");
                        if (tv != null) g.totalValoraciones = tv.intValue();

                        Long tr = doc.getLong("toursRealizados");
                        if (tr != null) g.toursRealizados = tr.intValue();

                        base.add(g);
                    }

                    // show all initially
                    view.clear();
                    view.addAll(base);
                    adapter.notifyDataSetChanged();

                    // re-apply filter if user already typed
                    String current = etSearch.getText() != null ? etSearch.getText().toString() : "";
                    applyFilter(current);
                })
                .addOnFailureListener(err -> {
                    Log.e(TAG, "Error loading guides", err);
                    Snackbar.make(findViewById(android.R.id.content),
                            "No se pudieron cargar guías: " + err.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                });
    }

    private void applyFilter(String raw) {
        String q = raw == null ? "" : raw.trim().toLowerCase(Locale.getDefault());

        view.clear();

        if (q.isEmpty()) {
            view.addAll(base);
        } else {
            for (Guide g : base) {
                String name = g.getName() != null ? g.getName() : "";
                String zone = g.getZone() != null ? g.getZone() : "";

                if (name.toLowerCase(Locale.getDefault()).contains(q) ||
                        zone.toLowerCase(Locale.getDefault()).contains(q)) {
                    view.add(g);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onProfile(Guide g) {
        Intent i = new Intent(this, GuideProfileActivity.class);
        i.putExtra("guide", g);
        startActivity(i);
    }

    @Override
    public void onOffer(Guide g) {
        // If you don't use offers anymore, comment this out or remove the menu option in GuideAdapter.
        Intent i = new Intent(this, OfferCreateActivity.class);
        i.putExtra("guide", g);
        startActivity(i);
    }
}
