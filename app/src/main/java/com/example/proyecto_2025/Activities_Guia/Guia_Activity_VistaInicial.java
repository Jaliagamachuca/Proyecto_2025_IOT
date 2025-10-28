package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        // 🔹 Ocultar los botones "Clientes" y "Registros" del menú inferior
        binding.bottomNav.getMenu().findItem(R.id.nav_clientes).setVisible(false);
        binding.bottomNav.getMenu().findItem(R.id.nav_registros).setVisible(false);

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

            if (checkedId == R.id.btnSolicitar) {
                showSubScreen(SUB_SOLICITAR);
            } else if (checkedId == R.id.btnPendientes) {
                showSubScreen(SUB_PENDIENTES);
            } else if (checkedId == R.id.btnHistorial) {
                showSubScreen(SUB_HISTORIAL);
            }
        });

        // Solicitar Tour
        binding.scrMisTours.subSolicitar.btn1.setOnClickListener(view ->
                solicitarTour());

        binding.scrMisTours.subSolicitar.btn2.setOnClickListener(view ->
                rechazarTour());

        binding.scrMisTours.subSolicitar.btn3.setOnClickListener(view ->
                errorSolicitar());

        binding.scrMisTours.subSolicitar.btn4.setOnClickListener(view ->
                errorCancelar());

        binding.scrMisTours.subSolicitar.InfoTour1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        binding.scrMisTours.subSolicitar.InfoTour2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        binding.scrMisTours.subSolicitar.InfoTour3.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        binding.scrMisTours.subSolicitar.InfoTour4.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour.class);
            startActivity(intent);
        });

        // Tours Pendientes
        binding.scrMisTours.subPendientes.btn1.setOnClickListener(v -> {
            Intent intent = new Intent(this, Guia_Tour_en_Proceso.class);
            startActivity(intent);
        });

        binding.scrMisTours.subPendientes.btn2.setOnClickListener(view ->
                iniciarTour());

        binding.scrMisTours.subPendientes.InfoTour1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.scrMisTours.subPendientes.InfoTour2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        // Historial de Tours
        binding.scrMisTours.subHistorial.InfoTour1.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.scrMisTours.subHistorial.InfoTour2.setOnClickListener(v -> {
            // Creamos un Intent para ir a OtraActivity
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.scrMisTours.subHistorial.btnDescargarPDF.setOnClickListener(view ->
                descargarTour());

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

        View target;
        if (subId == SUB_SOLICITAR) {
            target = vSolicitar;
        } else if (subId == SUB_PENDIENTES) {
            target = vPendientes;
        } else if (subId == SUB_HISTORIAL) {
            target = vHistorial;
        } else {
            // fallback para que nunca quede en blanco
            target = vSolicitar;
        }
        target.setVisibility(View.VISIBLE);
    }

    public void solicitarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Solicitar Tour");
        dialogBuilder.setMessage("¿Está seguro de solicitar este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
    public void rechazarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Cancelar Tour");
        dialogBuilder.setMessage("¿Está seguro cancelar la solicitud de este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void errorSolicitar() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("ERROR");
        dialogBuilder.setMessage("Usted ya tiene otro tour solicitado con la misma fecha, cancele la solicitud anterior y vuelva a intentarlo ");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void errorCancelar() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("ERROR");
        dialogBuilder.setMessage("Este Tour ya ha sido publicado por el Administrador de la Empresa, ya no puede cancelar su solicitud");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void iniciarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Iniciar Tour");
        dialogBuilder.setMessage("¿Está seguro de iniciar este tour?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void descargarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("PDF");
        dialogBuilder.setMessage("¿Está seguro de descargar la informacion en formato PDF?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}
