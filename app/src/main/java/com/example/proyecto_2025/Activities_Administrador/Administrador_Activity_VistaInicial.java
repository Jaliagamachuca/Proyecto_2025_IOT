package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityAdministradorVistaInicialBinding;

public class Administrador_Activity_VistaInicial extends AppCompatActivity {

    private ActivityAdministradorVistaInicialBinding binding;

    // IDs de las pantallas (coinciden con los android:id de cada <include>)
    private static final int SCR_EMPRESA  = R.id.scrEmpresa;
    private static final int SCR_TOURS    = R.id.scrTours;
    private static final int SCR_GUIAS    = R.id.scrGuias;
    private static final int SCR_REPORTES = R.id.scrReportes;
    private static final int SCR_CHAT     = R.id.scrChat;
    private static final int SCR_PERFIL   = R.id.scrPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Bottom bar → cambio de vista
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Estado inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
        showScreen(SCR_EMPRESA);

        // Botón dentro de la vista Empresa → abre el formulario de prueba
        binding.scrEmpresa.getRoot().findViewById(R.id.btnIrFormulario)
                .setOnClickListener(v ->
                        startActivity(new Intent(this, EmpresaFormPruebaActivity.class)));
    }

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_empresa) {
            showScreen(SCR_EMPRESA);
            return true;
        } else if (id == R.id.nav_tours) {
            showScreen(SCR_TOURS);
            return true;
        } else if (id == R.id.nav_guias) {
            showScreen(SCR_GUIAS);
            return true;
        } else if (id == R.id.nav_reportes) {
            showScreen(SCR_REPORTES);
            return true;
        } else if (id == R.id.nav_chat) {
            showScreen(SCR_CHAT);
            return true;
        } else if (id == R.id.nav_perfil) {
            showScreen(SCR_PERFIL);
            return true;
        }
        return false;
    }

    private void showScreen(@IdRes int screenId) {
        // Referencias a las raíces de cada include
        View vEmpresa  = binding.scrEmpresa.getRoot();
        View vTours    = binding.scrTours.getRoot();
        View vGuias    = binding.scrGuias.getRoot();
        View vReportes = binding.scrReportes.getRoot();
        View vChat     = binding.scrChat.getRoot();
        View vPerfil   = binding.scrPerfil.getRoot();

        // Oculta todas
        vEmpresa.setVisibility(View.GONE);
        vTours.setVisibility(View.GONE);
        vGuias.setVisibility(View.GONE);
        vReportes.setVisibility(View.GONE);
        vChat.setVisibility(View.GONE);
        vPerfil.setVisibility(View.GONE);

        // Muestra la elegida
        View target =
                (screenId == SCR_EMPRESA)  ? vEmpresa  :
                        (screenId == SCR_TOURS)    ? vTours    :
                                (screenId == SCR_GUIAS)    ? vGuias    :
                                        (screenId == SCR_REPORTES) ? vReportes :
                                                (screenId == SCR_CHAT)     ? vChat     : vPerfil;

        target.setVisibility(View.VISIBLE);

        // FAB contextual
        if (screenId == SCR_TOURS) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this, TourFormActivity.class)));
        } else if (screenId == SCR_GUIAS) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_person_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this, GuiaFormActivity.class)));
        } else {
            binding.fab.setVisibility(View.GONE);
            binding.fab.setOnClickListener(null);
        }
    }
}
