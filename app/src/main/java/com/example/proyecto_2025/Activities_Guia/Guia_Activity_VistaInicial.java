package com.example.proyecto_2025.Activities_Guia;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;

public class Guia_Activity_VistaInicial extends AppCompatActivity {

    private ActivityGuiaVistaInicialBinding binding;

    // Raíces (ids de cada <include/> del layout principal)
    private static final int SCR_DASHBOARD = R.id.scrDashboard;
    private static final int SCR_MISTOURS  = R.id.scrMisTours;   // Contiene el toggle interno
    private static final int SCR_CLIENTES  = R.id.scrClientes;
    private static final int SCR_REGISTROS = R.id.scrRegistros;
    private static final int SCR_PERFIL    = R.id.scrPerfil;

    // Subpantallas dentro de "Mis tours"
    private static final int SUB_SOLICITAR  = R.id.subSolicitar;
    private static final int SUB_PENDIENTES = R.id.subPendientes;
    private static final int SUB_HISTORIAL  = R.id.subHistorial;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Bottom bar → navegación (if/else para evitar "constant expression required")
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Toggle interno de "Mis tours": Solicitar / Pendientes / Historial
        binding.scrMisTours.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            showSubScreen(checkedId);
        });

        // Botones de atajo en Dashboard → abren "Mis tours" con la subpantalla correspondiente
        binding.scrDashboard.btnIrSolicitar.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_mistours);
            binding.scrMisTours.toggleGroup.check(R.id.btnSolicitar);
            showScreen(SCR_MISTOURS);
            showSubScreen(SUB_SOLICITAR);
        });
        binding.scrDashboard.btnIrPendientes.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_mistours);
            binding.scrMisTours.toggleGroup.check(R.id.btnPendientes);
            showScreen(SCR_MISTOURS);
            showSubScreen(SUB_PENDIENTES);
        });
        binding.scrDashboard.btnIrHistorial.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_mistours);
            binding.scrMisTours.toggleGroup.check(R.id.btnHistorial);
            showScreen(SCR_MISTOURS);
            showSubScreen(SUB_HISTORIAL);
        });

        // Estado inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        showScreen(SCR_DASHBOARD);
        binding.scrMisTours.toggleGroup.check(R.id.btnSolicitar);
        showSubScreen(SUB_SOLICITAR);
    }

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard) { showScreen(SCR_DASHBOARD); return true; }
        else if (id == R.id.nav_mistours){ showScreen(SCR_MISTOURS); return true; }
        else if (id == R.id.nav_clientes){ showScreen(SCR_CLIENTES); return true; }
        else if (id == R.id.nav_registros){ showScreen(SCR_REGISTROS); return true; }
        else if (id == R.id.nav_perfil){ showScreen(SCR_PERFIL); return true; }
        return false;
    }

    private void showScreen(@IdRes int screenId) {
        View vDash   = binding.scrDashboard.getRoot();
        View vTours  = binding.scrMisTours.getRoot();
        View vClis   = binding.scrClientes.getRoot();
        View vRegs   = binding.scrRegistros.getRoot();
        View vPerf   = binding.scrPerfil.getRoot();

        vDash.setVisibility(View.GONE);
        vTours.setVisibility(View.GONE);
        vClis.setVisibility(View.GONE);
        vRegs.setVisibility(View.GONE);
        vPerf.setVisibility(View.GONE);

        View target = (screenId == SCR_DASHBOARD) ? vDash :
                (screenId == SCR_MISTOURS)  ? vTours :
                        (screenId == SCR_CLIENTES)  ? vClis  :
                                (screenId == SCR_REGISTROS) ? vRegs  : vPerf;
        target.setVisibility(View.VISIBLE);
    }

    private void showSubScreen(@IdRes int subId) {
        View vSolicitar  = binding.scrMisTours.subSolicitar.getRoot();
        View vPendientes = binding.scrMisTours.subPendientes.getRoot();
        View vHistorial  = binding.scrMisTours.subHistorial.getRoot();

        vSolicitar.setVisibility(View.GONE);
        vPendientes.setVisibility(View.GONE);
        vHistorial.setVisibility(View.GONE);

        View target = (subId == SUB_SOLICITAR) ? vSolicitar :
                (subId == SUB_PENDIENTES) ? vPendientes : vHistorial;
        target.setVisibility(View.VISIBLE);
    }
}
