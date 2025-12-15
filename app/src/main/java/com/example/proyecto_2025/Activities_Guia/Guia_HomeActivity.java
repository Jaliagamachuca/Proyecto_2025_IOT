package com.example.proyecto_2025.Activities_Guia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.Activities_Superadmin.CambiarFotoActivity;
import com.example.proyecto_2025.Activities_Superadmin.EditarPerfilActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.example.proyecto_2025.databinding.ActivityGuiaVistaInicialBinding;

import com.example.proyecto_2025.login.LoginActivity;
import com.example.proyecto_2025.model.TourEstado;
import com.example.proyecto_2025.model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.proyecto_2025.model.Tour;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;

public class Guia_HomeActivity extends AppCompatActivity {

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


    private TourAdapter tourAdapter;
    private final List<Tour> tourList = new ArrayList<>();
    private final List<Tour> tourListOriginal = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuiaVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Ocultar los botones "Clientes" y "Registros" del menú inferior
        binding.bottomNav.getMenu().findItem(R.id.nav_clientes).setVisible(false);
        binding.bottomNav.getMenu().findItem(R.id.nav_registros).setVisible(false);

        // 🔹 Cargar imágenes bonitas para las tarjetas del Dashboard del Guía
        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/1828/1828919.png") // 📅 Solicitar nuevo tour
                .into(binding.scrDashboard.imgSolicitarTour);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/3209/3209265.png") // ⏳ Tours pendientes
                .into(binding.scrDashboard.imgPendientes);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/1484/1484569.png") // 📖 Historial de tours
                .into(binding.scrDashboard.imgHistorial);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Bottom bar → navegación
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

        // 🔹 Configurar RecyclerView en “Solicitar Tour”
        configurarRecyclerToursDisponibles();

        // 🔹 Configurar RecyclerView en “Pendietes Tour”
        configurarRecyclerToursPendientes();

        // 🔹 Configurar RecyclerView en “Historial Tour”
        configurarRecyclerToursHistorial();


        // 🔸 Tours Pendientes
        /*
        binding.scrMisTours.subPendientes.btn1.setOnClickListener(v -> {
            Intent intent = new Intent(this, Guia_Tour_en_Proceso.class);
            startActivity(intent);
        });

        binding.scrMisTours.subPendientes.btn2.setOnClickListener(view -> iniciarTour());

        binding.scrMisTours.subPendientes.InfoTour1.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.scrMisTours.subPendientes.InfoTour2.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });
        */

        /*
        // 🔸 Historial de Tours
        binding.scrMisTours.subHistorial.InfoTour1.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });

        binding.scrMisTours.subHistorial.InfoTour2.setOnClickListener(v -> {
            Intent intent = new Intent(this, Vista_Detalles_Tour_Sin_Botones.class);
            startActivity(intent);
        });
        */
        binding.scrMisTours.subHistorial.btnDescargarPDF.setOnClickListener(view -> descargarTour());

        // 🔸 Botones de atajo en Dashboard → abren "Mis tours"
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

        //configurarGraficoDisponibles(binding.scrDashboard.chartDisponibles);
        //configurarGraficoPendientes(binding.scrDashboard.chartPendientes);
        //configurarGraficoFinalizados(binding.scrDashboard.chartFinalizados);

        // Perfil (datos del usuario actual)
        cargarPerfilActual();
        configurarAccionesPerfil();
    }

    // 🔹 Configurar gráfico principal del Dashboard (tours por estado)
    /*private void configurarGraficoDisponibles(PieChart chart) {
        TourRepository repo = TourRepository.get();
        repo.seedIfEmpty(this);

        int solicitados = 0, noSolicitados = 0;
        for (Tour t : repo.byEstado("disponible")) {
            if ("solicitado".equalsIgnoreCase(t.getSubEstado())) solicitados++;
            else noSolicitados++;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (solicitados > 0) entries.add(new PieEntry(solicitados, "Solicitados"));
        if (noSolicitados > 0) entries.add(new PieEntry(noSolicitados, "No solicitados"));

        PieDataSet dataSet = new PieDataSet(entries, "Tours Disponibles");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.setUsePercentValues(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(35f);
        chart.getDescription().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }
    private void configurarGraficoPendientes(PieChart chart) {
        TourRepository repo = TourRepository.get();
        repo.seedIfEmpty(this);

        int iniciados = 0, noIniciados = 0;
        for (Tour t : repo.byEstado("pendiente")) {
            if ("iniciado".equalsIgnoreCase(t.getSubEstado())) iniciados++;
            else noIniciados++;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (iniciados > 0) entries.add(new PieEntry(iniciados, "Iniciados"));
        if (noIniciados > 0) entries.add(new PieEntry(noIniciados, "No iniciados"));

        PieDataSet dataSet = new PieDataSet(entries, "Tours Pendientes");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(35f);
        chart.getDescription().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }
    private void configurarGraficoFinalizados(PieChart chart) {
        TourRepository repo = TourRepository.get();
        repo.seedIfEmpty(this);

        int mas200 = 0, menosIgual200 = 0;
        for (Tour t : repo.byEstado("finalizado")) {
            if (t.getPagoOfrecido() > 200) mas200++;
            else menosIgual200++;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (mas200 > 0) entries.add(new PieEntry(mas200, "> 200 soles"));
        if (menosIgual200 > 0) entries.add(new PieEntry(menosIgual200, "≤ 200 soles"));

        PieDataSet dataSet = new PieDataSet(entries, "Tours Finalizados");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(35f);
        chart.getDescription().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }*/




    // 🔸 Configurar RecyclerView de Tours Disponibles
    private void configurarRecyclerToursDisponibles() {

        binding.scrMisTours.subSolicitar.recyclerViewToursDisponibles
                .setLayoutManager(new LinearLayoutManager(this));

        tourAdapter = new TourAdapter(this, tourList);
        binding.scrMisTours.subSolicitar.recyclerViewToursDisponibles
                .setAdapter(tourAdapter);

        db.collection("tours")
                .whereEqualTo("estado", TourEstado.PENDIENTE_GUIA.name())
                .orderBy("fechaInicioUtc", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    tourList.clear();
                    tourListOriginal.clear();

                    for (var doc : snap.getDocuments()) {
                        Tour t = doc.toObject(Tour.class);
                        if (t == null) continue;
                        t.id = doc.getId(); // si tu Tour tiene campo id público
                        tourList.add(t);
                    }

                    tourListOriginal.addAll(tourList);
                    tourAdapter.notifyDataSetChanged();
                });
    }


    // 🔸 Configurar RecyclerView de Tours Pendientes
    private void configurarRecyclerToursPendientes() {

        binding.scrMisTours.subPendientes.recyclerViewToursPendientes
                .setLayoutManager(new LinearLayoutManager(this));

        // si quieres usar otro adapter/lista distinta, crea otra lista + adapter
        // por simplicidad aquí reutilizo la misma lista/adapter cambiando la query al entrar
        TourAdapter adapterPendientes = new TourAdapter(this, new ArrayList<>());
        binding.scrMisTours.subPendientes.recyclerViewToursPendientes.setAdapter(adapterPendientes);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) return;

        db.collection("tours")
                .whereEqualTo("guiaId", uid)
                .whereIn("estado", java.util.Arrays.asList(
                        TourEstado.SOLICITADO.name(),
                        TourEstado.PENDIENTE_GUIA.name()
                ))
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    List<Tour> lista = new ArrayList<>();
                    for (var doc : snap.getDocuments()) {
                        Tour t = doc.toObject(Tour.class);
                        if (t == null) continue;
                        t.id = doc.getId();
                        lista.add(t);
                    }
                    adapterPendientes.updateData(lista);
                });
    }


    // 🔸 Configurar RecyclerView de Tours Pendientes
    private void configurarRecyclerToursHistorial() {

        binding.scrMisTours.subHistorial.recyclerViewToursHistorial
                .setLayoutManager(new LinearLayoutManager(this));

        TourAdapter adapterHistorial = new TourAdapter(this, new ArrayList<>());
        binding.scrMisTours.subHistorial.recyclerViewToursHistorial.setAdapter(adapterHistorial);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) return;

        db.collection("tours")
                .whereEqualTo("guiaId", uid)
                .whereEqualTo("estado", TourEstado.FINALIZADO.name())
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    List<Tour> lista = new ArrayList<>();
                    for (var doc : snap.getDocuments()) {
                        Tour t = doc.toObject(Tour.class);
                        if (t == null) continue;
                        t.id = doc.getId();
                        lista.add(t);
                    }
                    adapterHistorial.updateData(lista);
                });
    }

    // 🔹 Filtrar tours por texto
    private void filtrarTours(String texto) {
        tourList.clear();

        if (texto == null) texto = "";
        String q = texto.trim().toLowerCase();

        if (q.isEmpty()) {
            tourList.addAll(tourListOriginal);
        } else {
            for (Tour t : tourListOriginal) {
                String titulo = t.titulo != null ? t.titulo.toLowerCase() : "";
                String desc   = t.descripcionCorta != null ? t.descripcionCorta.toLowerCase() : "";
                if (titulo.contains(q) || desc.contains(q)) {
                    tourList.add(t);
                }
            }
        }
        tourAdapter.notifyDataSetChanged();
    }


    // 🔹 Mostrar pantallas principales
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
            target = vSolicitar;
        }
        target.setVisibility(View.VISIBLE);
    }


    public void descargarTour() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("PDF");
        dialogBuilder.setMessage("¿Está seguro de descargar la informacion en formato PDF?");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    // ================== PERFIL (SCR_PERFIL) ==================

    /** Carga los datos del usuario logueado y los muestra en el screen Perfil */
    private void cargarPerfilActual() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    User u = doc.toObject(User.class);
                    if (u == null) return;

                    // Actualizar la UI en tiempo real
                    binding.scrPerfil.tvNombre.setText(
                            u.getDisplayName() != null ? u.getDisplayName() : "-");
                    binding.scrPerfil.tvEmail.setText(
                            u.getEmail() != null ? u.getEmail() : "-");
                    binding.scrPerfil.tvTelefono.setText(
                            u.getPhone() != null ? u.getPhone() : "-");
                    binding.scrPerfil.tvDni.setText(
                            u.getDni() != null ? u.getDni() : "-");
                    binding.scrPerfil.tvFechaNacimiento.setText(
                            u.getFechaNacimiento() != null ? u.getFechaNacimiento() : "-");
                    binding.scrPerfil.tvDomicilio.setText(
                            u.getDomicilio() != null ? u.getDomicilio() : "-");

                    String company = u.getCompanyId() != null ? u.getCompanyId() : "Sin empresa";
                    binding.scrPerfil.tvEmpresaNombre.setText(company);

                    binding.scrPerfil.tvRuc.setText("—");

                    String photoUrl = u.getPhotoUrl();
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(this)
                                .load(photoUrl)
                                .placeholder(R.drawable.ic_user_placeholder)
                                .error(R.drawable.ic_user_placeholder)
                                .into(binding.scrPerfil.imgFotoPerfil);
                    }
                });
    }

    /** Listeners básicos del screen Perfil (cerrar sesión, etc.) */
    private void configurarAccionesPerfil() {
        // Cerrar sesión
        binding.scrPerfil.btnCerrarSesion.setOnClickListener(v -> {
            new AuthRepository().signOut();

            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        binding.scrPerfil.btnEditarPerfil.setOnClickListener(v -> {
            Intent i = new Intent(this, EditarPerfilActivityGuia.class);

            i.putExtra("nombre", binding.scrPerfil.tvNombre.getText().toString());
            i.putExtra("email", binding.scrPerfil.tvEmail.getText().toString());
            i.putExtra("telefono", binding.scrPerfil.tvTelefono.getText().toString());
            i.putExtra("empresa", binding.scrPerfil.tvEmpresaNombre.getText().toString());
            i.putExtra("dni", binding.scrPerfil.tvDni.getText().toString());
            i.putExtra("fechaNacimiento", binding.scrPerfil.tvFechaNacimiento.getText().toString());
            i.putExtra("domicilio", binding.scrPerfil.tvDomicilio.getText().toString());

            startActivity(i);
        });

        // 👉 SOLO CAMBIAR FOTO
        binding.scrPerfil.btnCambiarFoto.setOnClickListener(v -> {
            Intent i = new Intent(this, CambiarFotoActivityGuia.class);
            startActivity(i);
        });

        // Otros botones (editar perfil, cambiar foto, etc.) se pueden agregar luego.
    }
}
