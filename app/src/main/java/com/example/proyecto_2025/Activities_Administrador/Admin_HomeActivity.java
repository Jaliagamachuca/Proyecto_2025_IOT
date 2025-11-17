package com.example.proyecto_2025.Activities_Administrador;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.ImageUriAdapter;
import com.example.proyecto_2025.adapter.GuideAdapter;
import com.example.proyecto_2025.adapter.OfferAdapter;
import com.example.proyecto_2025.adapter.TourAdapter;
import com.example.proyecto_2025.data.EmpresaRepository;
import com.example.proyecto_2025.data.TourRepository;
import com.example.proyecto_2025.data.OfferRepository;
import com.example.proyecto_2025.data.GuideRepository;
import com.example.proyecto_2025.data.AdminRepository;
import com.example.proyecto_2025.databinding.ActivityAdminHomeBinding;
import com.example.proyecto_2025.model.Empresa;
import com.example.proyecto_2025.model.Guide;
import com.example.proyecto_2025.model.Offer;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.example.proyecto_2025.model.Admin;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class Admin_HomeActivity extends AppCompatActivity {

    // ===================== BINDING & CONSTANTS =====================
    private ActivityAdminHomeBinding binding;

    private static final int SCR_EMPRESA  = R.id.scrEmpresa;
    private static final int SCR_TOURS    = R.id.scrTours;
    private static final int SCR_GUIAS    = R.id.scrGuias;
    private static final int SCR_REPORTES = R.id.scrReportes;
    private static final int SCR_PERFIL   = R.id.scrPerfil;

    // ===================== REPOSITORIES =====================
    private EmpresaRepository empresaRepo;
    private AdminRepository adminRepo;

    // ===================== DATA MODELS =====================
    private Empresa empresa;
    private Admin admin;
    private final List<Uri> galeriaUris = new ArrayList<>();
    private final List<Guide> sugeridos = new ArrayList<>();
    private GuideAdapter guiasAdapter;

    // ===================== LAUNCHERS =====================
    private ActivityResultLauncher<Intent> pickImagesLauncher;
    private ActivityResultLauncher<Intent> pickLocationLauncher;

    // ===================== LIFECYCLE =====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar toolbar
        setupToolbar();

        // Inicializar repositorios y datos
        initializeRepositories();

        // Configurar launchers
        setupLaunchers();

        // Configurar bottom navigation
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Inicializar todas las secciones
        initializeAllSections();

        // Pantalla inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
        showScreen(SCR_EMPRESA);

        // Datos de prueba (eliminar en producci├│n)
        crearDatosDePruebaSiEsNecesario();
    }

    // ===================== SETUP METHODS =====================

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initializeRepositories() {
        GuideRepository.get().seedIfEmpty(this);
        empresaRepo = new EmpresaRepository(this);
        empresa = empresaRepo.load();
        adminRepo = new AdminRepository(this);
        admin = adminRepo.load();
    }

    private void setupLaunchers() {
        // Launcher para seleccionar im├ígenes
        pickImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                    Intent data = result.getData();

                    try {
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri u = data.getClipData().getItemAt(i).getUri();
                                getContentResolver().takePersistableUriPermission(
                                        u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                galeriaUris.add(u);
                            }
                        } else if (data.getData() != null) {
                            Uri u = data.getData();
                            getContentResolver().takePersistableUriPermission(
                                    u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            galeriaUris.add(u);
                        }
                    } catch (SecurityException ignore) {
                        if (data.getData() != null) galeriaUris.add(data.getData());
                    }

                    if (binding.scrEmpresa.rvGaleria.getAdapter() != null) {
                        binding.scrEmpresa.rvGaleria.getAdapter().notifyDataSetChanged();
                    }
                    actualizarChecklistYEstado();
                });

        // Launcher para seleccionar ubicaci├│n
        pickLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                    Intent d = result.getData();

                    empresa.direccion = d.getStringExtra("direccion");
                    empresa.lat = d.getDoubleExtra("lat", 0d);
                    empresa.lon = d.getDoubleExtra("lon", 0d);

                    binding.scrEmpresa.tvDireccion.setText(
                            (empresa.direccion == null || empresa.direccion.isEmpty())
                                    ? "Sin direcci├│n"
                                    : empresa.direccion
                    );

                    updateMapPreview();
                    actualizarChecklistYEstado();
                });
    }

    private void initializeAllSections() {
        initEmpresaSection();
        initToursSection();
        initGuiasSection();
        initReportesSection();
        initPerfilSection();
    }

    // ===================== NAVIGATION =====================

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_empresa) { showScreen(SCR_EMPRESA); return true; }
        else if (id == R.id.nav_tours) { showScreen(SCR_TOURS); return true; }
        else if (id == R.id.nav_guias) { showScreen(SCR_GUIAS); return true; }
        else if (id == R.id.nav_reportes) { showScreen(SCR_REPORTES); return true; }
        else if (id == R.id.nav_perfil) { showScreen(SCR_PERFIL); return true; }
        return false;
    }

    private void showScreen(@IdRes int screenId) {
        // Ocultar todas las pantallas
        binding.scrEmpresa.getRoot().setVisibility(View.GONE);
        binding.scrTours.getRoot().setVisibility(View.GONE);
        binding.scrGuias.getRoot().setVisibility(View.GONE);
        binding.scrReportes.getRoot().setVisibility(View.GONE);
        binding.scrPerfil.getRoot().setVisibility(View.GONE);

        // Mostrar pantalla seleccionada
        View target =
                (screenId == SCR_EMPRESA)  ? binding.scrEmpresa.getRoot() :
                        (screenId == SCR_TOURS)    ? binding.scrTours.getRoot() :
                                (screenId == SCR_GUIAS)    ? binding.scrGuias.getRoot() :
                                        (screenId == SCR_REPORTES) ? binding.scrReportes.getRoot() : binding.scrPerfil.getRoot();

        target.setVisibility(View.VISIBLE);

        // FAB visible solo en ciertas pantallas
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(null);

        // Actualizar datos si es necesario
        if (screenId == SCR_GUIAS) {
            refreshGuiasDashboard();
        }
    }

    // ===================== SECTION 1: EMPRESA =====================

    private void initEmpresaSection() {
        setupMapPreview();
        updateMapPreview();

        // Cargar datos previos
        binding.scrEmpresa.etNombre.setText(empresa.nombre);
        binding.scrEmpresa.etCorreo.setText(empresa.correo);
        binding.scrEmpresa.etTelefono.setText(empresa.telefono);
        binding.scrEmpresa.etWeb.setText(empresa.web);
        binding.scrEmpresa.etDescripcion.setText(empresa.descripcion);

        if (empresa.direccion != null && !empresa.direccion.isEmpty()) {
            binding.scrEmpresa.tvDireccion.setText(empresa.direccion);
        }

        // Configurar galer├¡a
        for (String s : empresa.imagenUris) galeriaUris.add(Uri.parse(s));
        binding.scrEmpresa.rvGaleria.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.scrEmpresa.rvGaleria.setAdapter(new ImageUriAdapter(galeriaUris, pos -> {
            galeriaUris.remove(pos);
            binding.scrEmpresa.rvGaleria.getAdapter().notifyItemRemoved(pos);
            actualizarChecklistYEstado();
        }));

        // Botones
        binding.scrEmpresa.btnAgregarFotos.setOnClickListener(v -> abrirPickerImagenes());
        binding.scrEmpresa.btnElegirUbicacion.setOnClickListener(v -> abrirPickerUbicacion());
        binding.scrEmpresa.btnGuardar.setOnClickListener(v -> guardarEmpresa(false));
        binding.scrEmpresa.btnPublicar.setOnClickListener(v -> guardarEmpresa(true));

        actualizarChecklistYEstado();
    }

    private void setupMapPreview() {
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(getPackageName());
        binding.scrEmpresa.mapPreview.setMultiTouchControls(false);
        binding.scrEmpresa.mapPreview.setTileSource(
                org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        binding.scrEmpresa.mapPreview.getController().setZoom(4.0);
        binding.scrEmpresa.mapPreview.getController().setCenter(
                new org.osmdroid.util.GeoPoint(-9.19, -75.02)); // Per├║
    }

    private void updateMapPreview() {
        boolean hasLoc = empresa != null && empresa.lat != 0 && empresa.lon != 0
                && empresa.direccion != null && !empresa.direccion.isEmpty();

        if (!hasLoc) {
            binding.scrEmpresa.tvDireccion.setText("Sin dirección");
            binding.scrEmpresa.mapPreview.getOverlays().clear();
            binding.scrEmpresa.mapPreview.getController().setZoom(4.0);
            binding.scrEmpresa.mapPreview.getController().setCenter(
                    new org.osmdroid.util.GeoPoint(-9.19, -75.02));
            binding.scrEmpresa.mapPreview.invalidate();
            return;
        }

        org.osmdroid.util.GeoPoint p = new org.osmdroid.util.GeoPoint(empresa.lat, empresa.lon);
        binding.scrEmpresa.mapPreview.getOverlays().clear();

        org.osmdroid.views.overlay.Marker m =
                new org.osmdroid.views.overlay.Marker(binding.scrEmpresa.mapPreview);
        m.setPosition(p);
        m.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
        m.setTitle("Ubicación seleccionada");
        binding.scrEmpresa.mapPreview.getOverlays().add(m);

        binding.scrEmpresa.mapPreview.getController().setZoom(15.0);
        binding.scrEmpresa.mapPreview.getController().setCenter(p);
        binding.scrEmpresa.tvDireccion.setText(empresa.direccion);
        binding.scrEmpresa.mapPreview.invalidate();
    }

    private void abrirPickerImagenes() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImagesLauncher.launch(Intent.createChooser(i, "Selecciona imágenes"));
    }

    private void abrirPickerUbicacion() {
        pickLocationLauncher.launch(new Intent(this, MapPickerActivity.class));
    }

    private void guardarEmpresa(boolean publicar) {
        if (!capturarFormulario(publicar)) return;

        if (publicar && !empresa.esCompleta()) {
            Snackbar.make(binding.getRoot(),
                    "Aún faltan requisitos para publicar.", Snackbar.LENGTH_LONG).show();
            return;
        }

        persistir();
        String msg = publicar ? "Perfil publicado" : "Empresa guardada";
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
    }

    private boolean capturarFormulario(boolean validarTodo) {
        EditText etNombre = binding.scrEmpresa.etNombre;
        EditText etCorreo = binding.scrEmpresa.etCorreo;
        EditText etTelefono = binding.scrEmpresa.etTelefono;
        EditText etDescripcion = binding.scrEmpresa.etDescripcion;

        empresa.nombre = etNombre.getText().toString().trim();
        empresa.correo = etCorreo.getText().toString().trim();
        empresa.telefono = etTelefono.getText().toString().trim();
        empresa.web = binding.scrEmpresa.etWeb.getText().toString().trim();
        empresa.descripcion = etDescripcion.getText().toString().trim();

        // Validaciones
        if (empresa.nombre.isEmpty()) {
            etNombre.setError("Requerido");
            if (validarTodo) return false;
        }

        if (empresa.correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(empresa.correo).matches()) {
            etCorreo.setError("Correo inválido");
            if (validarTodo) return false;
        }

        if (empresa.telefono.isEmpty()) {
            etTelefono.setError("Requerido");
            if (validarTodo) return false;
        }

        if (validarTodo && empresa.descripcion.isEmpty()) {
            etDescripcion.setError("Requerida");
            return false;
        }

        // Guardar URIs de galer├¡a
        empresa.imagenUris.clear();
        for (Uri u : galeriaUris) empresa.imagenUris.add(u.toString());

        return true;
    }

    private void persistir() {
        empresaRepo.save(empresa);
        actualizarChecklistYEstado();
    }

    private void actualizarChecklistYEstado() {
        setCheck(binding.scrEmpresa.chkContacto,
                !empresa.correo.isEmpty() && !empresa.telefono.isEmpty());
        setCheck(binding.scrEmpresa.chkUbicacion,
                empresa.direccion != null && !empresa.direccion.isEmpty());
        setCheck(binding.scrEmpresa.chkFotos, galeriaUris.size() >= 2);
        setCheck(binding.scrEmpresa.chkDescripcion, !empresa.descripcion.isEmpty());

        TextView tvEstado = binding.scrEmpresa.tvEstado;
        boolean completa = empresa.esCompleta();
        tvEstado.setText(completa ? "Perfil completo" : "Perfil incompleto");

        Button btnPublicar = binding.scrEmpresa.btnPublicar;
        btnPublicar.setEnabled(completa);
    }

    private void setCheck(TextView tv, boolean ok) {
        // Guarda el texto base la primera vez en el tag
        if (tv.getTag() == null) {
            // quita posible prefijo viejo tipo "✔ " o "• "
            String base = tv.getText().toString().replaceFirst("^[✔•]\\s*", "");
            tv.setTag(base);
        }
        String base = tv.getTag().toString();
        tv.setText((ok ? "✔ " : "• ") + base);
    }


    // ===================== SECTION 2: TOURS =====================

    private void initToursSection() {
        TourRepository repo = new TourRepository(this);
        List<Tour> ultimos = repo.findLastN(3);
        List<Tour> todosTours = repo.findAll();

        // Configurar RecyclerView horizontal
        LinearLayoutManager lm = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.scrTours.rvUltimos.setLayoutManager(lm);

        TourAdapter adapter = new TourAdapter(
                ultimos,
                t -> {
                    Intent i = new Intent(this, TourDetalleActivity.class);
                    i.putExtra("id", t.id);
                    startActivity(i);
                },
                (t, anchor) -> {}
        );
        binding.scrTours.rvUltimos.setAdapter(adapter);

        // Actualizar KPIs
        actualizarKPIsTours(todosTours);

        // Botones
        binding.scrTours.btnVerTodosUltimos.setOnClickListener(v ->
                startActivity(new Intent(this, TourListActivity.class)));

        binding.scrTours.fabNuevoTour.setVisibility(View.VISIBLE);
        binding.scrTours.fabNuevoTour.setOnClickListener(v -> {
            if (empresa == null || !empresa.esCompleta()) {
                Snackbar.make(binding.getRoot(),
                        "Primero completa el perfil de tu empresa",
                        Snackbar.LENGTH_LONG).show();
                binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
                return;
            }
            startActivity(new Intent(this, TourFormActivity.class));
        });
    }

    private void actualizarKPIsTours(List<Tour> tours) {
        int publicados = 0, pendientes = 0, borradores = 0;

        for (Tour tour : tours) {
            if (tour.estado == null) continue;
            switch (tour.estado) {
                case PUBLICADO:
                    publicados++;
                    break;
                case PENDIENTE_GUIA:
                    pendientes++;
                    break;
                case BORRADOR:
                    borradores++;
                    break;
            }
        }

        binding.scrTours.tvCountPublicados.setText(String.valueOf(publicados));
        binding.scrTours.tvCountPendientes.setText(String.valueOf(pendientes));
        binding.scrTours.tvCountBorradores.setText(String.valueOf(borradores));
    }

    // ===================== SECTION 3: GU├ìAS =====================

    private void initGuiasSection() {

        // Botones
        binding.scrGuias.btnExplorarGuias.setOnClickListener(v ->
                startActivity(new Intent(this, GuideDirectoryActivity.class)));

        binding.scrGuias.btnOfertasGuias.setText("📨 Ver solicitudes de guías");
        binding.scrGuias.btnOfertasGuias.setOnClickListener(v ->
                startActivity(new Intent(this, OfferInboxActivity.class)));

        // KPI: Guías activos
        binding.scrGuias.kpiGuiasActivos.setText(
                String.valueOf(GuideRepository.get().all().size())
        );

        // KPI: Solicitudes pendientes
        binding.scrGuias.kpiOfertasPendientes.setText(
                String.valueOf(OfferRepository.get().byStatus(Offer.Status.PENDIENTE).size())
        );

        // KPI: Guías con tours asignados (ofertas aceptadas)
        binding.scrGuias.kpiOfertasAceptadas.setText(
                String.valueOf(OfferRepository.get().byStatus(Offer.Status.ACEPTADA).size())
        );

        // === SOLICITUDES RECIENTES ===
        loadSolicitudesRecientes();

        // === CARRUSEL DE GUÍAS SUGERIDOS ===
        RecyclerView rv = binding.scrGuias.rvGuiasSugeridos;
        rv.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        guiasAdapter = new GuideAdapter(this, sugeridos, new GuideAdapter.OnAction() {
            @Override
            public void onProfile(Guide g) {
                Intent i = new Intent(Admin_HomeActivity.this, GuideProfileActivity.class);
                i.putExtra("guide", g);
                startActivity(i);
            }

            @Override
            public void onOffer(Guide g) {
                // En este flujo ya no envías oferta desde aquí,
                // si quisieras podrías abrir detalles del guía o dejarlo vacío.
            }
        });

        rv.setAdapter(guiasAdapter);

        // Cargar datos iniciales del carrusel
        loadGuiasSugeridos();
    }



    private void refreshGuiasDashboard() {
        // Actualizar KPIs
        int pend = OfferRepository.get().byStatus(Offer.Status.PENDIENTE).size();
        int acep = OfferRepository.get().byStatus(Offer.Status.ACEPTADA).size();
        int activos = GuideRepository.get().all().size();

        binding.scrGuias.kpiOfertasPendientes.setText(String.valueOf(pend));
        binding.scrGuias.kpiOfertasAceptadas.setText(String.valueOf(acep));
        binding.scrGuias.kpiGuiasActivos.setText(String.valueOf(activos));

        // Actualizar gu├¡as sugeridos
        sugeridos.clear();
        List<Guide> all = GuideRepository.get().all();
        for (int i = 0; i < Math.min(5, all.size()); i++) {
            sugeridos.add(all.get(i));
        }
        guiasAdapter.notifyDataSetChanged();

        binding.scrGuias.emptyStateGuias.setVisibility(
                sugeridos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // ===================== SECTION 4: REPORTES =====================

    private void initReportesSection() {
        // Filtros de per├¡odo
        binding.scrReportes.chipSemana.setOnClickListener(v -> cargarReportes("SEMANA"));
        binding.scrReportes.chipMes.setOnClickListener(v -> cargarReportes("MES"));
        binding.scrReportes.chipAnio.setOnClickListener(v -> cargarReportes("ANIO"));

        // Ver todas las transacciones
        binding.scrReportes.btnVerTodasTransacciones.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Funcionalidad en desarrollo", Snackbar.LENGTH_SHORT).show();
        });

        // Cargar datos iniciales (mes)
        cargarReportes("MES");
    }

    private void cargarReportes(String periodo) {
        // KPIs principales (datos de ejemplo - reemplazar con BD real)
        switch (periodo) {
            case "SEMANA":
                binding.scrReportes.tvIngresosTotales.setText("S/ 2,850");
                binding.scrReportes.tvIngresosCambio.setText("+12% vs anterior");
                binding.scrReportes.tvReservasTotales.setText("12");
                binding.scrReportes.tvReservasCambio.setText("Ôåæ +3 vs anterior");
                break;
            case "MES":
                binding.scrReportes.tvIngresosTotales.setText("S/ 12,450");
                binding.scrReportes.tvIngresosCambio.setText("+15% vs anterior");
                binding.scrReportes.tvReservasTotales.setText("48");
                binding.scrReportes.tvReservasCambio.setText("+8 vs anterior");
                break;
            case "ANIO":
                binding.scrReportes.tvIngresosTotales.setText("S/ 148,900");
                binding.scrReportes.tvIngresosCambio.setText("+23% vs anterior");
                binding.scrReportes.tvReservasTotales.setText("567");
                binding.scrReportes.tvReservasCambio.setText("+89 vs anterior");
                break;
        }

        // KPIs secundarios
        TourRepository tourRepo = new TourRepository(this);
        int toursActivos = 0;
        for (Tour t : tourRepo.findAll()) {
            if (t.estado == TourEstado.PUBLICADO) toursActivos++;
        }

        binding.scrReportes.tvToursActivos.setText(String.valueOf(toursActivos));
        binding.scrReportes.tvGuiasContratados.setText(
                String.valueOf(GuideRepository.get().all().size()));
        binding.scrReportes.tvSatisfaccion.setText("4.7");
    }

    // ===================== SECTION 5: PERFIL =====================

    private void initPerfilSection() {
        // Cargar datos del admin
        binding.scrPerfil.tvNombre.setText(admin.getNombre());
        binding.scrPerfil.tvEmail.setText(admin.getEmail());
        binding.scrPerfil.tvTelefono.setText("+51 " + admin.getTelefono());

        // Datos de empresa
        binding.scrPerfil.tvEmpresaNombre.setText(empresa.nombre);
        binding.scrPerfil.tvRuc.setText("20123456789");

        // Botones
        binding.scrPerfil.btnEditarPerfil.setOnClickListener(v -> mostrarDialogEditarPerfil());

        binding.scrPerfil.btnCambiarFoto.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Funcionalidad en desarrollo", Snackbar.LENGTH_SHORT).show();
        });

        binding.scrPerfil.btnVerEmpresa.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
        });

        // Switches
        binding.scrPerfil.switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas";
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
        });

        binding.scrPerfil.switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Modo oscuro activado" : "Modo oscuro desactivado";
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
        });

        binding.scrPerfil.btnIdioma.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Funcionalidad en desarrollo", Snackbar.LENGTH_SHORT).show();
        });

        // Cerrar sesi├│n
        binding.scrPerfil.btnCerrarSesion.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("┬┐Est├ís seguro de que deseas cerrar sesi├│n?")
                    .setPositiveButton("S├¡", (dialog, which) -> {
                        adminRepo.clear();
                        Snackbar.make(binding.getRoot(),
                                "Sesi├│n cerrada", Snackbar.LENGTH_LONG).show();
                        // TODO: Redirigir al login cuando est├® implementado
                        // startActivity(new Intent(this, LoginActivity.class));
                        // finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void mostrarDialogEditarPerfil() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);

        com.google.android.material.textfield.TextInputEditText etNombre =
                dialogView.findViewById(R.id.etNombre);
        com.google.android.material.textfield.TextInputEditText etEmail =
                dialogView.findViewById(R.id.etEmail);
        com.google.android.material.textfield.TextInputEditText etTelefono =
                dialogView.findViewById(R.id.etTelefono);

        // Llenar con datos actuales
        etNombre.setText(admin.getNombre());
        etEmail.setText(admin.getEmail());
        etTelefono.setText(admin.getTelefono());

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        // Bot├│n cancelar
        dialogView.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialog.dismiss());

        // Bot├│n guardar
        dialogView.findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            // Validaciones
            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es requerido");
                return;
            }

            if (telefono.isEmpty() || telefono.length() < 9) {
                etTelefono.setError("Teléfono inválido (mínimo 9 dígitos)");
                return;
            }

            // Actualizar admin (email NO cambia)
            admin.setNombre(nombre);
            admin.setTelefono(telefono);
            adminRepo.save(admin);

            // Actualizar UI
            binding.scrPerfil.tvNombre.setText(admin.getNombre());
            binding.scrPerfil.tvTelefono.setText("+51 " + admin.getTelefono());

            dialog.dismiss();
            Snackbar.make(binding.getRoot(),
                    "Perfil actualizado Ô£ô", Snackbar.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void loadSolicitudesRecientes() {
        List<Offer> pendientes = OfferRepository.get().byStatus(Offer.Status.PENDIENTE);

        binding.scrGuias.tvContadorSolicitudes.setText(pendientes.size() + " solicitudes");

        if (pendientes.isEmpty()) {
            binding.scrGuias.emptyStateSolicitudes.setVisibility(View.VISIBLE);
            binding.scrGuias.rvSolicitudesGuias.setVisibility(View.GONE);
            return;
        }

        binding.scrGuias.emptyStateSolicitudes.setVisibility(View.GONE);
        binding.scrGuias.rvSolicitudesGuias.setVisibility(View.VISIBLE);

        OfferAdapter adapter = new OfferAdapter(
                this,
                pendientes,
                new OfferAdapter.OnAction() {
                    @Override
                    public void onAssign(Offer o) {
                        // Si quieres permitir asignar guía desde aquí:
                        Intent i = new Intent(Admin_HomeActivity.this, AssignGuideActivity.class);
                        i.putExtra("offerId", o.getId());   // o.id si tu modelo no tiene getId()
                        startActivity(i);
                    }

                    @Override
                    public void onDetail(Offer o) {
                        Intent i = new Intent(Admin_HomeActivity.this, OfferDetailActivity.class);
                        i.putExtra("offerId", o.getId());   // o.id si tu modelo no tiene getId()
                        startActivity(i);
                    }
                }
        );

        binding.scrGuias.rvSolicitudesGuias.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.scrGuias.rvSolicitudesGuias.setAdapter(adapter);
    }



    private void loadGuiasSugeridos() {
        sugeridos.clear();
        List<Guide> all = GuideRepository.get().all();
        for (int i = 0; i < Math.min(5, all.size()); i++) {
            sugeridos.add(all.get(i));
        }
        guiasAdapter.notifyDataSetChanged();

        binding.scrGuias.emptyStateGuias.setVisibility(
                sugeridos.isEmpty() ? View.VISIBLE : View.GONE
        );
    }


    // ===================== DATA SEEDING (DEVELOPMENT ONLY) =====================

    /**
     * Crea tours de prueba si la BD est├í vac├¡a
     * ELIMINAR en producci├│n
     */
    private void crearDatosDePruebaSiEsNecesario() {
        TourRepository repo = new TourRepository(this);
        if (!repo.findAll().isEmpty()) return;

        // Tour 1
        Tour t1 = new Tour();
        t1.id = "1";
        t1.titulo = "City Tour Lima Centro";
        t1.descripcionCorta = "Conoce el centro histórico de Lima";
        t1.precioPorPersona = 50.0;
        t1.cupos = 15;
        t1.estado = TourEstado.PUBLICADO;
        repo.upsert(t1);

        // Tour 2
        Tour t2 = new Tour();
        t2.id = "2";
        t2.titulo = "Tour Gastronómico Miraflores";
        t2.descripcionCorta = "Degusta los mejores platos peruanos";
        t2.precioPorPersona = 80.0;
        t2.cupos = 10;
        t2.estado = TourEstado.PUBLICADO;
        repo.upsert(t2);

        // Tour 3
        Tour t3 = new Tour();
        t3.id = "3";
        t3.titulo = "Huacachina Aventura";
        t3.descripcionCorta = "Sandboarding en el desierto";
        t3.precioPorPersona = 120.0;
        t3.cupos = 8;
        t3.estado = TourEstado.EN_CURSO;
        repo.upsert(t3);
    }
}
