package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.UserAdapter;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.example.proyecto_2025.data.repository.UserRepository;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;
import com.example.proyecto_2025.login.LoginActivity;
import com.example.proyecto_2025.model.User;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeView Superadmin (sin fragments):
 * Bottom bar fija: Dashboard, Admins, Guías, Clientes, Registros, Perfil
 * FAB contextual sólo en Admins (Registrar Admin) y Guías (Registrar Guías).
 */
public class Superadmin_HomeActivity extends AppCompatActivity {

    private ActivitySuperadminVistaInicialBinding binding;

    // IDs de raíces (coinciden con los android:id de cada <include/>)
    private static final int SCR_DASHBOARD = R.id.scrDashboard;
    private static final int SCR_ADMINS    = R.id.scrAdmins;
    private static final int SCR_GUIAS     = R.id.scrGuias;
    private static final int SCR_CLIENTES  = R.id.scrClientes;
    private static final int SCR_REGISTROS = R.id.scrRegistros;
    private static final int SCR_PERFIL    = R.id.scrPerfil;

    // Repo Firebase
    private UserRepository userRepo;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Adapters y listas completas para filtros
    private UserAdapter adapterAdmins;
    private UserAdapter adapterGuias;
    private UserAdapter adapterClientes;

    private List<User> listaAdminsFull   = new ArrayList<>();
    private List<User> listaGuiasFull    = new ArrayList<>();
    private List<User> listaClientesFull = new ArrayList<>();

    // Flags para no agregar múltiples TextWatchers
    private boolean buscadorAdminsInit = false;
    private boolean buscadorGuiasInit  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepo = UserRepository.get();

        // Ocultamos opción de registros por ahora
        binding.bottomNav.getMenu().findItem(R.id.nav_registros).setVisible(false);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Bottom bar -> navegación
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // Estado inicial: Dashboard
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        showScreen(SCR_DASHBOARD);

        // Botones rápidos en Dashboard
        binding.scrDashboard.btnRegistrarAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, Superadmin_Registrar_Administrador.class)));

        binding.scrDashboard.btnRegistrarGuias.setOnClickListener(v ->
                startActivity(new Intent(this, Superadmin_Registrar_Guias_Turismo.class)));

        // Imágenes del dashboard
        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/190/190411.png") // Admin
                .into(binding.scrDashboard.imgRegistrarAdmin);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/2922/2922510.png") // Guía
                .into(binding.scrDashboard.imgRegistrarGuias);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/29/29302.png") // CSV
                .into(binding.scrDashboard.imgDescargarCSV);

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/337/337946.png") // PDF
                .into(binding.scrDashboard.imgDescargarPDF);

        // Cargar datos reales desde Firestore
        cargarDatosAdmins();
        cargarDatosGuias();
        cargarDatosClientes();

        // Perfil (datos del usuario actual)
        cargarPerfilActual();
        configurarAccionesPerfil();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔁 Cada vez que regreses a esta Activity, recarga las listas
        cargarDatosAdmins();
        cargarDatosGuias();
        cargarDatosClientes();
    }

    // ================== NAV BOTTOM ==================
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
        // Raíces de cada include
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
            binding.fab.setImageResource(R.drawable.ic_person_add_24);

            binding.fab.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.teal_700));
            binding.fab.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));

            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            Superadmin_Registrar_Administrador.class)));

        } else if (screenId == SCR_GUIAS) {

            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_person_add_24);
            binding.fab.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.teal_700));
            binding.fab.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));

            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            Superadmin_Registrar_Guias_Turismo.class)));

        } else {
            binding.fab.setVisibility(View.GONE);
            binding.fab.setOnClickListener(null);
        }
    }

    // ================== CARGA DE DATOS DESDE FIRESTORE ==================

    private void cargarDatosAdmins() {
        userRepo.allAdmins(new UserRepository.Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                listaAdminsFull = data;

                // Gráfico en dashboard
                configurarGrafico(binding.scrDashboard.chartAdmins, "Administradores", data);

                // RecyclerView de admins
                if (adapterAdmins == null) {
                    adapterAdmins = new UserAdapter();
                    adapterAdmins.setContext(Superadmin_HomeActivity.this);
                    binding.scrAdmins.recyclerView.setLayoutManager(
                            new LinearLayoutManager(Superadmin_HomeActivity.this));
                    binding.scrAdmins.recyclerView.setAdapter(adapterAdmins);
                }
                adapterAdmins.setListaEmpleados(data);

                // Buscador de admins (solo una vez)
                if (!buscadorAdminsInit) {
                    configurarBuscadorAdmins();
                    buscadorAdminsInit = true;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("superadmin-admins", "Error cargando admins", e);
            }
        });
    }

    private void cargarDatosGuias() {
        userRepo.allGuias(new UserRepository.Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {

                // 🔹 FILTRAR SOLO GUIAS APROBADOS (status = active)
                List<User> soloAprobados = new ArrayList<>();
                for (User u : data) {
                    if (u.getStatus() != null &&
                            "active".equalsIgnoreCase(u.getStatus())) {
                        soloAprobados.add(u);
                    }
                }

                listaGuiasFull = soloAprobados;

                // Gráfico en dashboard SOLO con aprobados
                configurarGrafico(binding.scrDashboard.chartGuias, "Guías", soloAprobados);

                // RecyclerView de guías
                if (adapterGuias == null) {
                    adapterGuias = new UserAdapter();
                    adapterGuias.setContext(Superadmin_HomeActivity.this);
                    binding.scrGuias.recyclerView.setLayoutManager(
                            new LinearLayoutManager(Superadmin_HomeActivity.this));
                    binding.scrGuias.recyclerView.setAdapter(adapterGuias);
                }
                adapterGuias.setListaEmpleados(soloAprobados);

                // Buscador de guías (solo una vez)
                if (!buscadorGuiasInit) {
                    configurarBuscadorGuias();
                    buscadorGuiasInit = true;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("superadmin-guias", "Error cargando guías", e);
            }
        });
    }

    private void cargarDatosClientes() {
        userRepo.allClientes(new UserRepository.Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                listaClientesFull = data;
                configurarGrafico(binding.scrDashboard.chartClientes, "Clientes", data);

                if (adapterClientes == null) {
                    adapterClientes = new UserAdapter();
                    adapterClientes.setContext(Superadmin_HomeActivity.this);
                    binding.scrClientes.recyclerView.setLayoutManager(
                            new LinearLayoutManager(Superadmin_HomeActivity.this));
                    binding.scrClientes.recyclerView.setAdapter(adapterClientes);
                }
                adapterClientes.setListaEmpleados(data);
            }

            @Override
            public void onError(Exception e) {
                Log.e("superadmin-clientes", "Error cargando clientes", e);
            }
        });
    }

    // ================== BUSCADORES ==================

    private void configurarBuscadorAdmins() {
        binding.scrAdmins.etBuscarAdmin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString().trim().toLowerCase();
                List<User> filtrados = new ArrayList<>();

                for (User u : listaAdminsFull) {
                    String nombre = u.getNombreCompleto() != null ? u.getNombreCompleto().toLowerCase() : "";
                    String email  = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                    String dni    = u.getDni()   != null ? u.getDni().toLowerCase()   : "";

                    if (nombre.contains(q) || email.contains(q) || dni.contains(q)) {
                        filtrados.add(u);
                    }
                }

                adapterAdmins.setListaEmpleados(filtrados);
            }
        });
    }

    private void configurarBuscadorGuias() {
        binding.scrGuias.etBuscarGuia.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString().trim().toLowerCase();
                List<User> filtrados = new ArrayList<>();

                for (User u : listaGuiasFull) {
                    String nombre = u.getNombreCompleto() != null ? u.getNombreCompleto().toLowerCase() : "";
                    String email  = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                    String dni    = u.getDni()   != null ? u.getDni().toLowerCase()   : "";

                    if (nombre.contains(q) || email.contains(q) || dni.contains(q)) {
                        filtrados.add(u);
                    }
                }

                adapterGuias.setListaEmpleados(filtrados);
            }
        });
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
            Intent i = new Intent(this, EditarPerfilActivity.class);

            i.putExtra("nombre", binding.scrPerfil.tvNombre.getText().toString());
            i.putExtra("email", binding.scrPerfil.tvEmail.getText().toString());
            i.putExtra("telefono", binding.scrPerfil.tvTelefono.getText().toString());
            i.putExtra("empresa", binding.scrPerfil.tvEmpresaNombre.getText().toString());
            i.putExtra("dni", binding.scrPerfil.tvDni.getText().toString());

            startActivity(i);
        });

        // 👉 SOLO CAMBIAR FOTO
        binding.scrPerfil.btnCambiarFoto.setOnClickListener(v -> {
            Intent i = new Intent(this, CambiarFotoActivity.class);
            startActivity(i);
        });

        // Otros botones (editar perfil, cambiar foto, etc.) se pueden agregar luego.
    }

    // ================== GRÁFICOS ==================
    private void configurarGrafico(com.github.mikephil.charting.charts.PieChart chart,
                                   String titulo, List<User> lista) {

        long activos = lista.stream().filter(User::isActivo).count();
        long inactivos = lista.size() - activos;

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(activos, "Activos"));
        entries.add(new PieEntry(inactivos, "Inactivos"));

        PieDataSet dataSet = new PieDataSet(entries, titulo);
        dataSet.setColors(
                ContextCompat.getColor(this, R.color.teal_700),
                ContextCompat.getColor(this, R.color.gray)
        );

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(ContextCompat.getColor(this, R.color.black));

        chart.setData(data);
        chart.setCenterText(titulo);
        chart.setCenterTextSize(16f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.animateY(1000);
        chart.invalidate();
    }
}
