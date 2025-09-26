package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * HomeView Superadmin (sin fragments):
 * Bottom bar fija: Dashboard, Admins, Guías, Clientes, Registros, Perfil
 * FAB contextual sólo en Admins (Registrar Admin) y Guías (Registrar Guías).
 */
public class Superadmin_Activity_VistaInicial extends AppCompatActivity {

    private ActivitySuperadminVistaInicialBinding binding;

    // IDs de raíces (coinciden con los android:id de cada <include/>)
    private static final int SCR_DASHBOARD = R.id.scrDashboard;
    private static final int SCR_ADMINS    = R.id.scrAdmins;
    private static final int SCR_GUIAS     = R.id.scrGuias;
    private static final int SCR_CLIENTES  = R.id.scrClientes;
    private static final int SCR_REGISTROS = R.id.scrRegistros;
    private static final int SCR_PERFIL    = R.id.scrPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Bottom bar -> navegación (if/else para evitar "constant expression required")
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Estado inicial: Dashboard
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        showScreen(SCR_DASHBOARD);
    }

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard) {
            showScreen(SCR_DASHBOARD); return true;
        } else if (id == R.id.nav_admins) {
            showScreen(SCR_ADMINS);    return true;
        } else if (id == R.id.nav_guias) {
            showScreen(SCR_GUIAS);     return true;
        } else if (id == R.id.nav_clientes) {
            showScreen(SCR_CLIENTES);  return true;
        } else if (id == R.id.nav_registros) {
            showScreen(SCR_REGISTROS); return true;
        } else if (id == R.id.nav_perfil) {
            showScreen(SCR_PERFIL);    return true;
        }
        return false;
    }

    private void showScreen(@IdRes int screenId) {
        // Raíces de cada include (con ViewBinding -> usar getRoot())
        View vDash     = binding.scrDashboard.getRoot();
        View vAdmins   = binding.scrAdmins.getRoot();
        View vGuias    = binding.scrGuias.getRoot();
        View vClientes = binding.scrClientes.getRoot();
        View vReg      = binding.scrRegistros.getRoot();
        View vPerfil   = binding.scrPerfil.getRoot();

        // Ocultar todas
        vDash.setVisibility(View.GONE);
        vAdmins.setVisibility(View.GONE);
        vGuias.setVisibility(View.GONE);
        vClientes.setVisibility(View.GONE);
        vReg.setVisibility(View.GONE);
        vPerfil.setVisibility(View.GONE);

        // Mostrar la elegida
        View target =
                (screenId == SCR_DASHBOARD) ? vDash :
                        (screenId == SCR_ADMINS)    ? vAdmins :
                                (screenId == SCR_GUIAS)     ? vGuias :
                                        (screenId == SCR_CLIENTES)  ? vClientes :
                                                (screenId == SCR_REGISTROS) ? vReg : vPerfil;
        target.setVisibility(View.VISIBLE);

        // FAB contextual
        if (screenId == SCR_ADMINS) {

            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Registrar_Administrador.class)));
            // ⚡ Aquí configuras el botón dentro del layout de Admins
            binding.scrAdmins.InfoAdmin1.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Administrador.class)));
            binding.scrAdmins.InfoAdmin2.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Administrador.class)));
            binding.scrAdmins.btn1.setOnClickListener(view ->
                    activarAdministrador());

            binding.scrAdmins.btn2.setOnClickListener(view ->
                    desactivarAdministrador());
            binding.scrAdmins.btnRegistrarAdministrador.setOnClickListener(v -> {
                // Creamos un Intent para ir a OtraActivity
                Intent intent = new Intent(this, Superadmin_Registrar_Administrador.class);
                startActivity(intent);
            });

        } else if (screenId == SCR_GUIAS) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_person_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Registrar_Guias_Turismo.class)));
            // ⚡ Aquí configuras el botón dentro del layout de Admins
            binding.scrGuias.InfoGuia1.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Guia_Turismo.class)));
            binding.scrGuias.InfoGuia2.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Guia_Turismo.class)));
            binding.scrGuias.btn1.setOnClickListener(view ->
                    activarGuia());

            binding.scrGuias.btn2.setOnClickListener(view ->
                    desactivarGuia());
            binding.scrGuias.btnRegistrarGuia.setOnClickListener(v -> {
                // Creamos un Intent para ir a OtraActivity
                Intent intent = new Intent(this, Superadmin_Registrar_Guias_Turismo.class);
                startActivity(intent);
            });
        } else if (screenId == SCR_CLIENTES) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_person_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente.class)));
            // ⚡ Aquí configuras el botón dentro del layout de Admins
            binding.scrClientes.InfoCliente1.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente.class)));
            binding.scrClientes.InfoCliente2.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente.class)));
            binding.scrClientes.btn1.setOnClickListener(view ->
                    activarCliente());

            binding.scrClientes.btn2.setOnClickListener(view ->
                    desactivarCliente());
        } else {
            binding.fab.setVisibility(View.GONE);
            binding.fab.setOnClickListener(null);
        }
    }
    public void activarAdministrador() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Administrador");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void desactivarAdministrador() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Desactivar Administrador");
        dialogBuilder.setMessage("¿Está seguro de desactivar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void activarGuia() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Guía de Turismo");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void desactivarGuia() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Desactivar Guía de Turismo");
        dialogBuilder.setMessage("¿Está seguro de desactivar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void activarCliente() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Activar Cliente");
        dialogBuilder.setMessage("¿Está seguro de activar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void desactivarCliente() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Desactivar Cliente");
        dialogBuilder.setMessage("¿Está seguro de desactivar este usuario?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}
