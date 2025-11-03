package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.GuideRepository;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;
import com.example.proyecto_2025.model.Guide;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HomeView Superadmin (sin fragments):
 * Bottom bar fija: Dashboard, Admins, Guías, Clientes, Registros, Perfil
 * FAB contextual sólo en Admins (Registrar Admin) y Guías (Registrar Guías).
 */
public class Superadmin_HomeActivity extends AppCompatActivity {

    private ActivitySuperadminVistaInicialBinding binding;

    private EmployeeService employeeService;  // 🔹 servicio retrofit

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

        binding.bottomNav.getMenu().findItem(R.id.nav_registros).setVisible(false);

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

        // 🔹 Acción del botón "Registrar Admin" en el Dashboard
        binding.scrDashboard.btnRegistrarAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Superadmin_Registrar_Administrador.class);
            startActivity(intent);
        });
        // 🔹 Acción del botón "Registrar Admin" en el Dashboard
        binding.scrDashboard.btnRegistrarGuias.setOnClickListener(v -> {
            Intent intent = new Intent(this, Superadmin_Registrar_Guias_Turismo.class);
            startActivity(intent);
        });

        // 🔹 Cargar imágenes bonitas para las tarjetas del Dashboard
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

        // ⚡️ Llamamos aquí siempre, así la lista llega a Admins, Guias y Clientes
        createRetrofitService();
        cargarListaWebService();
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
            binding.fab.setImageResource(R.drawable.ic_person_add_24);

            // 🔹 Hace que el fondo del botón sea más oscuro (por ejemplo, azul oscuro)
            binding.fab.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.teal_700));

            // 🔹 Cambia el color del ícono (por ejemplo, blanco para que resalte)
            binding.fab.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));

            // 🔹 Acción del botón
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Registrar_Administrador.class)));
            /*
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
            });*/

        } else if (screenId == SCR_GUIAS) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_person_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Registrar_Guias_Turismo.class)));
            /*
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
            }); */
        } else if (screenId == SCR_CLIENTES) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setImageResource(R.drawable.ic_person_add_24);
            binding.fab.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente.class)));
            /*
            binding.scrClientes.InfoCliente1.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente.class)));
            binding.scrClientes.InfoCliente2.setOnClickListener(v ->
                    startActivity(new Intent(this,
                            com.example.proyecto_2025.Activities_Superadmin.Superadmin_Ver_Cliente.class)));
            binding.scrClientes.btn1.setOnClickListener(view ->
                    activarCliente());

            binding.scrClientes.btn2.setOnClickListener(view ->
                    desactivarCliente()); */
        } else {
            binding.fab.setVisibility(View.GONE);
            binding.fab.setOnClickListener(null);
        }

    }

    // ================== Retrofit ==================
    public void createRetrofitService() {
        employeeService = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080") // ⚡ cambia según tu backend
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EmployeeService.class);
    }

    public void cargarListaWebService() {
        // 1️⃣ Obtener data local del nuevo repositorio
        UserRepository repo = UserRepository.get();
        repo.seedIfEmpty(this); // Carga los datos de ejemplo si están vacíos

        // 2️⃣ Obtener listas separadas por rol
        List<User> listaAdmins = repo.allAdmins();
        List<User> listaGuias = repo.allGuias();
        List<User> listaClientes = repo.allClientes();

        // ================== GRÁFICOS CIRCULARES ==================
        configurarGrafico(binding.scrDashboard.chartAdmins, "Administradores", listaAdmins);
        configurarGrafico(binding.scrDashboard.chartGuias, "Guías", listaGuias);
        configurarGrafico(binding.scrDashboard.chartClientes, "Clientes", listaClientes);

        // 3️⃣ Cargar adapters para cada tipo de usuario
        EmployeeAdapter adapterAdmins = new EmployeeAdapter();
        adapterAdmins.setListaEmpleados(listaAdmins);
        adapterAdmins.setContext(this);
        binding.scrAdmins.recyclerView.setAdapter(adapterAdmins);
        binding.scrAdmins.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EmployeeAdapter adapterGuias = new EmployeeAdapter();
        adapterGuias.setListaEmpleados(listaGuias);
        adapterGuias.setContext(this);
        binding.scrGuias.recyclerView.setAdapter(adapterGuias);
        binding.scrGuias.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EmployeeAdapter adapterClientes = new EmployeeAdapter();
        adapterClientes.setListaEmpleados(listaClientes);
        adapterClientes.setContext(this);
        binding.scrClientes.recyclerView.setAdapter(adapterClientes);
        binding.scrClientes.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d("msg-superadmin", "Lista cargada desde UserRepository: " +
                (listaAdmins.size() + listaGuias.size() + listaClientes.size()));
    }
    private void configurarGrafico(com.github.mikephil.charting.charts.PieChart chart,
                                   String titulo, List<User> lista) {

        // Contar activos e inactivos
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
        chart.invalidate(); // refrescar
    }
}
