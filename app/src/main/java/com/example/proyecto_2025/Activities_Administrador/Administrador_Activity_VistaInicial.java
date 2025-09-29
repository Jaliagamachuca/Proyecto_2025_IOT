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

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.ImageUriAdapter;
import com.example.proyecto_2025.data.EmpresaRepository;
import com.example.proyecto_2025.data.TourRepository;
import com.example.proyecto_2025.databinding.ActivityAdministradorVistaInicialBinding;
import com.example.proyecto_2025.model.Empresa;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Administrador_Activity_VistaInicial extends AppCompatActivity {

    private ActivityAdministradorVistaInicialBinding binding;

    private static final int SCR_EMPRESA  = R.id.scrEmpresa;
    private static final int SCR_TOURS    = R.id.scrTours;
    private static final int SCR_GUIAS    = R.id.scrGuias;
    private static final int SCR_REPORTES = R.id.scrReportes;
    private static final int SCR_CHAT     = R.id.scrChat;
    private static final int SCR_PERFIL   = R.id.scrPerfil;

    private EmpresaRepository empresaRepo;
    private Empresa empresa;
    private final List<Uri> galeriaUris = new ArrayList<>();

    private ActivityResultLauncher<Intent> pickImagesLauncher;
    private ActivityResultLauncher<Intent> pickLocationLauncher;

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

        empresaRepo = new EmpresaRepository(this);
        empresa = empresaRepo.load();

        // Launchers
        pickImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                    Intent data = result.getData();

                    try {
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri u = data.getClipData().getItemAt(i).getUri();
                                // Mantén acceso persistente a las imágenes elegidas
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
                        // Si no se pudo persistir el permiso, igual añadimos la URI para esta sesión.
                        if (data.getData() != null) galeriaUris.add(data.getData());
                    }

                    if (binding.scrEmpresa.rvGaleria.getAdapter() != null) {
                        binding.scrEmpresa.rvGaleria.getAdapter().notifyDataSetChanged();
                    }
                    actualizarChecklistYEstado();
                });

        pickLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                    Intent d = result.getData();

                    empresa.direccion = d.getStringExtra("direccion");
                    empresa.lat = d.getDoubleExtra("lat", 0d);
                    empresa.lon = d.getDoubleExtra("lon", 0d);

                    // Solo texto de dirección en la UI (las coords van a la pre-vista de mapa)
                    binding.scrEmpresa.tvDireccion.setText(
                            (empresa.direccion == null || empresa.direccion.isEmpty())
                                    ? "Sin dirección"
                                    : empresa.direccion
                    );

                    // 🔄 Actualiza la tarjeta de pre-vista del mapa
                    updateMapPreview();

                    actualizarChecklistYEstado();
                });


        // Bottom bar
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Estado inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
        showScreen(SCR_EMPRESA);

        // Inicializa la sección Empresa
        initEmpresaSection();
        initToursSection();
    }
    private void initToursSection() {
        TourRepository repo = new TourRepository(this);
        java.util.List<com.example.proyecto_2025.model.Tour> ultimos = repo.findLastN(3);

        androidx.recyclerview.widget.LinearLayoutManager lm =
                new androidx.recyclerview.widget.LinearLayoutManager(this,
                        androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false);
        binding.scrTours.rvUltimos.setLayoutManager(lm);

        com.example.proyecto_2025.adapter.TourAdapter adapter =
                new com.example.proyecto_2025.adapter.TourAdapter(
                        ultimos,
                        t -> {
                            Intent i = new Intent(this, TourDetalleActivity.class);
                            i.putExtra("id", t.id);
                            startActivity(i);
                        },
                        (t, anchor) -> {}
                );
        binding.scrTours.rvUltimos.setAdapter(adapter);

        binding.scrTours.btnVerTodos.setOnClickListener(v ->
                startActivity(new Intent(this, TourListActivity.class)));
    }

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

    private void showScreen(@IdRes int screenId) {
        View vEmpresa  = binding.scrEmpresa.getRoot();
        View vTours    = binding.scrTours.getRoot();
        View vGuias    = binding.scrGuias.getRoot();
        View vReportes = binding.scrReportes.getRoot();
        View vChat     = binding.scrChat.getRoot();
        View vPerfil   = binding.scrPerfil.getRoot();

        vEmpresa.setVisibility(View.GONE);
        vTours.setVisibility(View.GONE);
        vGuias.setVisibility(View.GONE);
        vReportes.setVisibility(View.GONE);
        vChat.setVisibility(View.GONE);
        vPerfil.setVisibility(View.GONE);

        View target =
                (screenId == SCR_EMPRESA)  ? vEmpresa  :
                        (screenId == SCR_TOURS)    ? vTours    :
                                (screenId == SCR_GUIAS)    ? vGuias    :
                                        (screenId == SCR_REPORTES) ? vReportes :
                                                (screenId == SCR_CHAT)     ? vChat     : vPerfil;

        target.setVisibility(View.VISIBLE);

        // FAB contextual (Tours/Guías)
        if (screenId == SCR_TOURS) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_add_24);
            binding.fab.setOnClickListener(v -> {
                // Guard de completitud de Empresa
                if (!empresa.esCompleta()) {
                    Snackbar.make(binding.getRoot(),
                            "Completa tu Empresa (contacto, ubicación y 2 fotos) antes de crear tours.",
                            Snackbar.LENGTH_LONG).show();
                    showScreen(SCR_EMPRESA);
                    binding.bottomNav.setSelectedItemId(R.id.nav_empresa);
                    return;
                }
                startActivity(new Intent(this, TourFormActivity.class));
            });
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
    // 1) Inicializa el MapView de la tarjeta (una sola vez)
    private void setupMapPreview() {
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(getPackageName());
        binding.scrEmpresa.mapPreview.setMultiTouchControls(false); // pre-vista (no interactivo)
        binding.scrEmpresa.mapPreview.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        // Zoom moderado por defecto (si aún no hay ubicación guardada)
        binding.scrEmpresa.mapPreview.getController().setZoom(4.0);
        binding.scrEmpresa.mapPreview.getController().setCenter(new org.osmdroid.util.GeoPoint(-9.19, -75.02)); // Perú
    }

    // 2) Actualiza centro, zoom y pin con la dirección elegida
    private void updateMapPreview() {
        boolean hasLoc = empresa != null && empresa.lat != 0 && empresa.lon != 0 && empresa.direccion != null && !empresa.direccion.isEmpty();

        if (!hasLoc) {
            binding.scrEmpresa.tvDireccion.setText("Sin dirección");
            binding.scrEmpresa.mapPreview.getOverlays().clear();
            binding.scrEmpresa.mapPreview.getController().setZoom(4.0);
            binding.scrEmpresa.mapPreview.getController().setCenter(new org.osmdroid.util.GeoPoint(-9.19, -75.02));
            binding.scrEmpresa.mapPreview.invalidate();
            return;
        }

        org.osmdroid.util.GeoPoint p = new org.osmdroid.util.GeoPoint(empresa.lat, empresa.lon);
        binding.scrEmpresa.mapPreview.getOverlays().clear();

        org.osmdroid.views.overlay.Marker m = new org.osmdroid.views.overlay.Marker(binding.scrEmpresa.mapPreview);
        m.setPosition(p);
        m.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
        m.setTitle("Ubicación seleccionada");
        binding.scrEmpresa.mapPreview.getOverlays().add(m);

        binding.scrEmpresa.mapPreview.getController().setZoom(15.0);
        binding.scrEmpresa.mapPreview.getController().setCenter(p);
        binding.scrEmpresa.tvDireccion.setText(empresa.direccion);
        binding.scrEmpresa.mapPreview.invalidate();
    }


    // ===================== EMPRESA SECTION =====================

    private void initEmpresaSection() {

        setupMapPreview();
        updateMapPreview();

        // Carga datos previos
        binding.scrEmpresa.etNombre.setText(empresa.nombre);
        binding.scrEmpresa.etCorreo.setText(empresa.correo);
        binding.scrEmpresa.etTelefono.setText(empresa.telefono);
        binding.scrEmpresa.etWeb.setText(empresa.web);
        binding.scrEmpresa.etDescripcion.setText(empresa.descripcion);
        if (empresa.direccion != null && !empresa.direccion.isEmpty()) {
            binding.scrEmpresa.tvDireccion.setText(
                    empresa.direccion + " (" + empresa.lat + ", " + empresa.lon + ")");
        }
        // Galería
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
        binding.scrEmpresa.btnGuardar.setOnClickListener(v -> {
            if (!capturarFormulario(false)) return;
            persistir();
            Snackbar.make(binding.getRoot(), "Empresa guardada", Snackbar.LENGTH_SHORT).show();
        });
        binding.scrEmpresa.btnPublicar.setOnClickListener(v -> {
            if (!capturarFormulario(true)) return;
            if (!empresa.esCompleta()) {
                Snackbar.make(binding.getRoot(),
                        "Aún faltan requisitos para publicar.", Snackbar.LENGTH_LONG).show();
                return;
            }
            persistir();
            Snackbar.make(binding.getRoot(), "Perfil publicado ✅", Snackbar.LENGTH_LONG).show();
        });

        actualizarChecklistYEstado();
    }

    private void abrirPickerImagenes() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImagesLauncher.launch(Intent.createChooser(i, "Selecciona imágenes"));
    }

    private void abrirPickerUbicacion() {
        pickLocationLauncher.launch(new Intent(this, MapPickerActivity.class));
    }

    private boolean capturarFormulario(boolean validarTodo) {
        EditText etNombre = binding.scrEmpresa.etNombre;
        EditText etCorreo = binding.scrEmpresa.etCorreo;
        EditText etTelefono = binding.scrEmpresa.etTelefono;
        EditText etWeb = binding.scrEmpresa.etWeb;
        EditText etDescripcion = binding.scrEmpresa.etDescripcion;

        empresa.nombre = etNombre.getText().toString().trim();
        empresa.correo = etCorreo.getText().toString().trim();
        empresa.telefono = etTelefono.getText().toString().trim();
        empresa.web = etWeb.getText().toString().trim();
        empresa.descripcion = etDescripcion.getText().toString().trim();

        // Validaciones mínimas
        if (empresa.nombre.isEmpty()) { etNombre.setError("Requerido"); if (validarTodo) return false; }
        if (empresa.correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(empresa.correo).matches()) {
            etCorreo.setError("Correo inválido"); if (validarTodo) return false;
        }
        if (empresa.telefono.isEmpty()) { etTelefono.setError("Requerido"); if (validarTodo) return false; }

        // Sin validar web/descr si no hacen publicar
        if (validarTodo && empresa.descripcion.isEmpty()) { etDescripcion.setError("Requerida"); return false; }

        // Galería -> modelo
        empresa.imagenUris.clear();
        for (Uri u : galeriaUris) empresa.imagenUris.add(u.toString());
        return true;
    }

    private void persistir() {
        empresaRepo.save(empresa);
        actualizarChecklistYEstado();
    }

    private void actualizarChecklistYEstado() {
        // Checklist visual
        setCheck(binding.scrEmpresa.chkContacto, !empresa.correo.isEmpty() && !empresa.telefono.isEmpty());
        setCheck(binding.scrEmpresa.chkUbicacion, empresa.direccion != null && !empresa.direccion.isEmpty());
        setCheck(binding.scrEmpresa.chkFotos, galeriaUris.size() >= 2);
        setCheck(binding.scrEmpresa.chkDescripcion, !empresa.descripcion.isEmpty());

        TextView tvEstado = binding.scrEmpresa.tvEstado;
        boolean completa = empresa.esCompleta();
        tvEstado.setText(completa ? "Estado: Completo" : "Estado: Borrador");

        Button btnPublicar = binding.scrEmpresa.btnPublicar;
        btnPublicar.setEnabled(completa);
    }

    private void setCheck(TextView tv, boolean ok) {
        tv.setText((ok ? "✓ " : "• ") + tv.getText().toString().replaceFirst("^[✓•]\\s*", ""));
    }
}
