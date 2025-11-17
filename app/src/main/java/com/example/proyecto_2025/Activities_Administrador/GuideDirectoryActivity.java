package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.GuideAdapter;
import com.example.proyecto_2025.data.repository.GuideRepository;
import com.example.proyecto_2025.model.Guide;
import java.util.*;
import java.util.stream.Collectors;

public class GuideDirectoryActivity extends AppCompatActivity implements GuideAdapter.OnAction {
    private List<Guide> base = new ArrayList<>();
    private List<Guide> view = new ArrayList<>();
    private GuideAdapter adapter;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.guide_directory);

        base = GuideRepository.get().all();
        view.addAll(base);

        RecyclerView rv = findViewById(R.id.rvGuides);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuideAdapter(this, view, this);
        rv.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new android.text.TextWatcher(){
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                String q = s.toString().toLowerCase(Locale.getDefault());
                view.clear();
                view.addAll(base.stream().filter(g ->
                        g.getName().toLowerCase(Locale.getDefault()).contains(q) ||
                                g.getZone().toLowerCase(Locale.getDefault()).contains(q)
                ).collect(Collectors.toList()));
                adapter.notifyDataSetChanged();
            }
            public void afterTextChanged(android.text.Editable s){}
        });
    }

    @Override public void onProfile(Guide g) {
        Intent i = new Intent(this, GuideProfileActivity.class);
        i.putExtra("guide", g);
        startActivity(i);
    }

    @Override public void onOffer(Guide g) {
        Intent i = new Intent(this, OfferCreateActivity.class);
        i.putExtra("guide", g);
        startActivity(i);
    }
}
