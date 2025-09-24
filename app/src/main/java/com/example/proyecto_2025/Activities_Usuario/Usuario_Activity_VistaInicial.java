package com.example.proyecto_2025.Activities_Usuario;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityUsuarioVistaInicialBinding;

/**
 * HomeView Cliente (sin fragments):
 * Bottom bar: Dashboard, Empresas (chat), Guías, Clientes, Registros, Perfil.
 * No usa FAB global; acciones son contextuales en cada vista.
 */
public class Usuario_Activity_VistaInicial extends AppCompatActivity {

    private ActivityUsuarioVistaInicialBinding binding;

    // Raíces (ids de cada <include/>)
    private static final int SCR_DASHBOARD = R.id.scrDashboard;
    private static final int SCR_EMPRESAS  = R.id.scrEmpresas;
    private static final int SCR_GUIAS     = R.id.scrGuias;
    private static final int SCR_CLIENTES  = R.id.scrClientes;
    private static final int SCR_REGISTROS = R.id.scrRegistros;
    private static final int SCR_PERFIL    = R.id.scrPerfil;

    // Subpantallas dentro de "Clientes": Mis reservas / Pasajeros
    private static final int SUB_RESERVAS  = R.id.subReservas;
    private static final int SUB_PASAJEROS = R.id.subPasajeros;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Bottom bar → navegación
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Toggle interno de "Clientes": Mis reservas / Pasajeros
        binding.scrClientes.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            showSubScreen(checkedId);
        });

        // Atajos desde Dashboard
        binding.scrDashboard.btnIrSoporte.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_empresas);
            showScreen(SCR_EMPRESAS);
        });
        binding.scrDashboard.btnIrReservas.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_clientes);
            showScreen(SCR_CLIENTES);
            binding.scrClientes.toggleGroup.check(R.id.btnReservas);
            showSubScreen(SUB_RESERVAS);
        });

        // Estado inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        showScreen(SCR_DASHBOARD);
        binding.scrClientes.toggleGroup.check(R.id.btnReservas);
        showSubScreen(SUB_RESERVAS);
    }

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard)  { showScreen(SCR_DASHBOARD); return true; }
        else if (id == R.id.nav_empresas) { showScreen(SCR_EMPRESAS); return true; }
        else if (id == R.id.nav_guias)  { showScreen(SCR_GUIAS); return true; }
        else if (id == R.id.nav_clientes) { showScreen(SCR_CLIENTES); return true; }
        else if (id == R.id.nav_registros) { showScreen(SCR_REGISTROS); return true; }
        else if (id == R.id.nav_perfil) { showScreen(SCR_PERFIL); return true; }
        return false;
    }

    private void showScreen(@IdRes int screenId) {
        View vDash     = binding.scrDashboard.getRoot();
        View vEmpresas = binding.scrEmpresas.getRoot();
        View vGuias    = binding.scrGuias.getRoot();
        View vClientes = binding.scrClientes.getRoot();
        View vReg      = binding.scrRegistros.getRoot();
        View vPerfil   = binding.scrPerfil.getRoot();

        vDash.setVisibility(View.GONE);
        vEmpresas.setVisibility(View.GONE);
        vGuias.setVisibility(View.GONE);
        vClientes.setVisibility(View.GONE);
        vReg.setVisibility(View.GONE);
        vPerfil.setVisibility(View.GONE);

        View target =
                (screenId == SCR_DASHBOARD) ? vDash :
                        (screenId == SCR_EMPRESAS)  ? vEmpresas :
                                (screenId == SCR_GUIAS)     ? vGuias :
                                        (screenId == SCR_CLIENTES)  ? vClientes :
                                                (screenId == SCR_REGISTROS) ? vReg : vPerfil;

        target.setVisibility(View.VISIBLE);
    }

    private void showSubScreen(@IdRes int subId) {
        View vReservas  = binding.scrClientes.subReservas.getRoot();
        View vPasajeros = binding.scrClientes.subPasajeros.getRoot();

        vReservas.setVisibility(View.GONE);
        vPasajeros.setVisibility(View.GONE);

        View target = (subId == SUB_RESERVAS) ? vReservas : vPasajeros;
        target.setVisibility(View.VISIBLE);
    }
}
