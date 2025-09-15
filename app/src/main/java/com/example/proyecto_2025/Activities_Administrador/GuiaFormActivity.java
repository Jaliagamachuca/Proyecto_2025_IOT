package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;

public class GuiaFormActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(getLayoutInflater().inflate(R.layout.activity_admin_guia_form, null));
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Registrar Guía");
    }
}
