package com.example.proyecto_2025.Activities_Administrador;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.Activities_Guia.EditarPerfilActivityGuia;
import com.example.proyecto_2025.Activities_Superadmin.CambiarFotoActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.ImageUriAdapter;
import com.example.proyecto_2025.adapter.GuideAdapter;
import com.example.proyecto_2025.adapter.OfferAdapter;
import com.example.proyecto_2025.adapter.TourAdapter;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.example.proyecto_2025.data.repository.EmpresaRepository;
import com.example.proyecto_2025.data.repository.TourRepository;
import com.example.proyecto_2025.data.repository.OfferRepository;
import com.example.proyecto_2025.data.repository.GuideRepository;
import com.example.proyecto_2025.data.repository.AdminRepository;
import com.example.proyecto_2025.databinding.ActivityAdminHomeBinding;
import com.example.proyecto_2025.login.LoginActivity;
import com.example.proyecto_2025.model.Empresa;
import com.example.proyecto_2025.model.Guide;
import com.example.proyecto_2025.model.Offer;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.example.proyecto_2025.model.Admin;
import com.example.proyecto_2025.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;


public class Admin_HomeActivity extends AppCompatActivity {

    // ===================== BINDING & CONSTANTS =====================
    private ActivityAdminHomeBinding binding;

    private static final int SCR_EMPRESA  = R.id.scrEmpresa;
    private static final int SCR_TOURS    = R.id.scrTours;
    private static final int SCR_GUIAS    = R.id.scrGuias;
    private static final int SCR_REPORTES = R.id.scrReportes;
    private static final int SCR_CHAT = R.id.scrChat;

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

    // ===================== TOURS (ADMIN) =====================
    private TourRepository tourRepo;
    private TourAdapter tourAdapter;
    private final List<Tour> allTours = new ArrayList<>();
    private String empresaId;

    // Para extraer el guia actual y poner sus datos en el perfil
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        syncEmpresaFromRemote();

        // Pantalla inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
        showScreen(SCR_EMPRESA);

        // Datos de prueba (eliminar en producci├│n)
        //crearDatosDePruebaSiEsNecesario();
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

        // TOURS
        tourRepo = new TourRepository(this);

        // Usamos el UID del usuario logueado como empresaId (coincide con TourFormActivity)
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            empresaId = fbUser.getUid();
        }
    }
    private void syncEmpresaFromRemote() {
        empresaRepo.loadFromRemote(remoteEmpresa -> {
            if (remoteEmpresa == null) return;

            empresa = remoteEmpresa;

            runOnUiThread(() -> {
                // === Recargar UI de empresa con la data remota ===
                binding.scrEmpresa.etNombre.setText(empresa.nombre);
                binding.scrEmpresa.etCorreo.setText(empresa.correo);
                binding.scrEmpresa.etTelefono.setText(empresa.telefono);
                binding.scrEmpresa.etWeb.setText(empresa.web);
                binding.scrEmpresa.etDescripcion.setText(empresa.descripcion);

                if (empresa.direccion != null && !empresa.direccion.isEmpty()) {
                    binding.scrEmpresa.tvDireccion.setText(empresa.direccion);
                } else {
                    binding.scrEmpresa.tvDireccion.setText("Sin dirección");
                }

                // Galería
                galeriaUris.clear();
                for (String s : empresa.imagenUris) {
                    try {
                        galeriaUris.add(Uri.parse(s));
                    } catch (Exception ignore) {}
                }
                if (binding.scrEmpresa.rvGaleria.getAdapter() != null) {
                    binding.scrEmpresa.rvGaleria.getAdapter().notifyDataSetChanged();
                }

                updateMapPreview();
                actualizarChecklistYEstado();
            });
        });
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
        initChatSection();
        //initPerfilSection();
        cargarPerfilActual();
        configurarAccionesPerfil();
    }

    // ===================== NAVIGATION =====================

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_empresa) { showScreen(SCR_EMPRESA); return true; }
        else if (id == R.id.nav_tours) { showScreen(SCR_TOURS); return true; }
        else if (id == R.id.nav_guias) { showScreen(SCR_GUIAS); return true; }
        else if (id == R.id.nav_reportes) { showScreen(SCR_REPORTES); return true; }
        else if (id == R.id.nav_chat) { showScreen(SCR_CHAT); return true; }
        else if (id == R.id.nav_perfil) { showScreen(SCR_PERFIL); return true; }
        return false;
    }

    // ===================== SECTION X: CHAT =====================
    private void initChatSection() {
        // Por ahora solo dejamos el screen listo.
        // Luego aquí conectamos Firestore + Adapter + filtros.
        if (binding == null || binding.scrChat == null) return;

        // Si tu screen tiene emptyState:
        // binding.scrChat.emptyStateChat.setVisibility(View.VISIBLE);
        // binding.scrChat.rvConversaciones.setVisibility(View.GONE);
    }

    private void showScreen(@IdRes int screenId) {
        // Ocultar todas las pantallas
        binding.scrEmpresa.getRoot().setVisibility(View.GONE);
        binding.scrTours.getRoot().setVisibility(View.GONE);
        binding.scrGuias.getRoot().setVisibility(View.GONE);
        binding.scrReportes.getRoot().setVisibility(View.GONE);
        binding.scrChat.getRoot().setVisibility(View.GONE);
        binding.scrPerfil.getRoot().setVisibility(View.GONE);

        // Mostrar pantalla seleccionada
        View target =
                (screenId == SCR_EMPRESA) ? binding.scrEmpresa.getRoot() :
                        (screenId == SCR_TOURS) ? binding.scrTours.getRoot() :
                                (screenId == SCR_GUIAS) ? binding.scrGuias.getRoot() :
                                        (screenId == SCR_REPORTES) ? binding.scrReportes.getRoot() :
                                                (screenId == SCR_CHAT) ? binding.scrChat.getRoot() :
                                                        binding.scrPerfil.getRoot();

        target.setVisibility(View.VISIBLE);

        // FAB global apagado
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(null);

        if (screenId == SCR_GUIAS) refreshGuiasDashboard();
        if (screenId == SCR_TOURS) loadToursEmpresa();

        if (screenId == SCR_CHAT) initChatSection();  // o refreshChat();
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

        // Definir estado lógico según acción
        empresa.status = publicar ? "active" : "pending";
        Log.d("EMPRESA_DEBUG", "guardarEmpresa(publicar=" + publicar + ")");
        if (!capturarFormulario(publicar)) return;
        empresa.status = publicar ? "active" : "pending";
        persistir();

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

        // Configurar RecyclerView (vertical, últimos tours)
        LinearLayoutManager lm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        binding.scrTours.rvUltimos.setLayoutManager(lm);

        tourAdapter = new TourAdapter(
                new ArrayList<>(),
                t -> {
                    // Ir al detalle del tour
                    Intent i = new Intent(this, TourDetalleActivity.class);
                    i.putExtra("id", t.id);
                    startActivity(i);
                },
                (t, anchor) -> {
                    // menú "more" si quieres más adelante
                }
        );
        binding.scrTours.rvUltimos.setAdapter(tourAdapter);

        // Botón "Ver todos"
        binding.scrTours.btnVerTodosUltimos.setOnClickListener(v ->
                startActivity(new Intent(this, TourListActivity.class)));

        // FAB "Nuevo tour"
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

        // Filtros (chips)
        binding.scrTours.chipTodos.setOnClickListener(v -> applyTourFilter());
        binding.scrTours.chipFiltroPublicados.setOnClickListener(v -> applyTourFilter());
        binding.scrTours.chipFiltroPendientes.setOnClickListener(v -> applyTourFilter());

        // Cargar datos iniciales
        loadToursEmpresa();
    }

    private void loadToursEmpresa() {
        if (empresaId == null || empresaId.isEmpty()) {
            // Si por alguna razón no hay sesión, no hacemos nada
            return;
        }

        tourRepo.fetchToursByEmpresa(empresaId, new TourRepository.ToursCallback() {
            @Override
            public void onSuccess(List<Tour> tours) {
                allTours.clear();
                allTours.addAll(tours);

                // Actualizar KPIs y filtros
                actualizarKPIsTours(allTours);
                applyTourFilter();
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(binding.getRoot(),
                        "Error al cargar tours: " + e.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
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

    private void applyTourFilter() {
        List<Tour> filtered = new ArrayList<>();

        boolean filterPublicados = binding.scrTours.chipFiltroPublicados.isChecked();
        boolean filterPendientes = binding.scrTours.chipFiltroPendientes.isChecked();
        boolean filterTodos      = binding.scrTours.chipTodos.isChecked()
                || (!filterPublicados && !filterPendientes);

        for (Tour t : allTours) {
            if (t.estado == null) continue;

            if (filterTodos) {
                filtered.add(t);
            } else if (filterPublicados && t.estado == TourEstado.PUBLICADO) {
                filtered.add(t);
            } else if (filterPendientes && t.estado == TourEstado.PENDIENTE_GUIA) {
                filtered.add(t);
            }
        }

        tourAdapter.replaceData(filtered);

        // Empty state vs lista
        if (filtered.isEmpty()) {
            binding.scrTours.emptyState.setVisibility(View.VISIBLE);
            binding.scrTours.rvUltimos.setVisibility(View.GONE);
        } else {
            binding.scrTours.emptyState.setVisibility(View.GONE);
            binding.scrTours.rvUltimos.setVisibility(View.VISIBLE);
        }
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
            Intent i = new Intent(this, EditarPerfilActivityAdmin.class);

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
            Intent i = new Intent(this, CambiarFotoActivityAdmin.class);
            startActivity(i);
        });

        // Otros botones (editar perfil, cambiar foto, etc.) se pueden agregar luego.
    }
    /*
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
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        // 1) Cerrar sesión en Firebase
                        FirebaseAuth.getInstance().signOut();

                        // 2) Limpiar repos locales
                        adminRepo.clear();
                        // si no tienes este método aún, créalo en EmpresaRepository
                        empresaRepo.clear();

                        // 3) Ir al login y limpiar historial
                        Intent i = new Intent(this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    } */

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



}
