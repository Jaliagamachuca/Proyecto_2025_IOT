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

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivitySuperadminVistaInicialBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
public class Superadmin_Activity_VistaInicial extends AppCompatActivity {

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
        employeeService.obtenerLista().enqueue(new Callback<EmployeeDto>() {
            @Override
            public void onResponse(Call<EmployeeDto> call, Response<EmployeeDto> response) {
                if (response.isSuccessful()) {
                    EmployeeDto body = response.body();
                    List<Employee> employeeList = body.getLista();

                    // 🔹 Dividir la lista en 3 sublistas según salario
                    List<Employee> listaAdmins = new ArrayList<>();
                    List<Employee> listaGuias = new ArrayList<>();
                    List<Employee> listaClientes = new ArrayList<>();

                    for (Employee e : employeeList) {
                        double salario = e.getSalary();
                        if (salario <= 8000) {
                            listaAdmins.add(e);
                        } else if (salario <= 10000) {
                            listaGuias.add(e);
                        } else {
                            listaClientes.add(e);
                        }
                    }

                    // 🔹 Adapter para Admins
                    EmployeeAdapter adapterAdmins = new EmployeeAdapter();
                    adapterAdmins.setListaEmpleados(listaAdmins);
                    adapterAdmins.setContext(Superadmin_Activity_VistaInicial.this);
                    binding.scrAdmins.recyclerView.setAdapter(adapterAdmins);
                    binding.scrAdmins.recyclerView.setLayoutManager(
                            new LinearLayoutManager(Superadmin_Activity_VistaInicial.this)
                    );

                    // 🔹 Adapter para Guias
                    EmployeeAdapter adapterGuias = new EmployeeAdapter();
                    adapterGuias.setListaEmpleados(listaGuias);
                    adapterGuias.setContext(Superadmin_Activity_VistaInicial.this);
                    binding.scrGuias.recyclerView.setAdapter(adapterGuias);
                    binding.scrGuias.recyclerView.setLayoutManager(
                            new LinearLayoutManager(Superadmin_Activity_VistaInicial.this)
                    );

                    // 🔹 Adapter para Clientes
                    EmployeeAdapter adapterClientes = new EmployeeAdapter();
                    adapterClientes.setListaEmpleados(listaClientes);
                    adapterClientes.setContext(Superadmin_Activity_VistaInicial.this);
                    binding.scrClientes.recyclerView.setAdapter(adapterClientes);
                    binding.scrClientes.recyclerView.setLayoutManager(
                            new LinearLayoutManager(Superadmin_Activity_VistaInicial.this)
                    );

                } else {
                    Log.d("msg-superadmin", "response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<EmployeeDto> call, Throwable t) {
                Log.e("msg-superadmin", "error: " + t.getMessage());
            }
        });
    }
}
