package com.example.proyecto_2025.Activities_Usuario;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityUsuarioVistaInicialBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.tabs.TabLayout;

/**
 * Activity principal del Cliente
 * Bottom bar: Dashboard, Explorar, Reservas, Seguimiento, Perfil
 * Navegación directa entre 5 pantallas principales
 */
public class Usuario_Activity_VistaInicial extends AppCompatActivity {

    private ActivityUsuarioVistaInicialBinding binding;

    // Raíces (ids de cada <include/>)
    private static final int SCR_DASHBOARD = R.id.scrDashboard;
    private static final int SCR_EXPLORAR  = R.id.scrExplorar;
    private static final int SCR_RESERVAS     = R.id.scrReservas;
    private static final int SCR_SEGUIMIENTO  = R.id.scrSeguimiento;
    private static final int SCR_PERFIL    = R.id.scrPerfil;




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

        setupRecyclerViewEmpresas();
        setupRecyclerViewToursRecomendados();
        setupRecyclerViewReservas();
        setupRecyclerViewItinerario();

        // Atajos desde Dashboard
        binding.scrDashboard.btnContactarSoporte.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_explorar);
            showScreen(SCR_EXPLORAR);
        });
        binding.scrDashboard.btnMisReservas.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_reservas);
            showScreen(SCR_RESERVAS);
        });

        // Estado inicial
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        showScreen(SCR_DASHBOARD);

    }

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard)  { showScreen(SCR_DASHBOARD); return true; }
        else if (id == R.id.nav_explorar) { showScreen(SCR_EXPLORAR); return true; }
        else if (id == R.id.nav_reservas)  { showScreen(SCR_RESERVAS); return true; }
        else if (id == R.id.nav_seguimiento) { showScreen(SCR_SEGUIMIENTO); return true; }
        else if (id == R.id.nav_perfil) { showScreen(SCR_PERFIL); return true; }
        return false;
    }

    private void showScreen(@IdRes int screenId) {
        View vDash     = binding.scrDashboard.getRoot();
        View vExplorar = binding.scrExplorar.getRoot();
        View vReservas = binding.scrReservas.getRoot();
        View vSeguimiento = binding.scrSeguimiento.getRoot();

        View vPerfil   = binding.scrPerfil.getRoot();

        vDash.setVisibility(View.GONE);
        vExplorar.setVisibility(View.GONE);
        vReservas.setVisibility(View.GONE);
        vSeguimiento.setVisibility(View.GONE);
        vPerfil.setVisibility(View.GONE);

        View target =
                (screenId == SCR_DASHBOARD) ? vDash :
                        (screenId == SCR_EXPLORAR)  ? vExplorar :
                                (screenId == SCR_RESERVAS)     ? vReservas :
                                        (screenId == SCR_SEGUIMIENTO)  ? vSeguimiento : vPerfil;

        target.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerViewEmpresas() {
        // Crear datos de prueba
        List<EmpresaTurismo> empresas = crearDatosPrueba();

        // Configurar el RecyclerView
        EmpresasAdapter adapter = new EmpresasAdapter(empresas, new EmpresasAdapter.OnEmpresaClickListener() {
            @Override
            public void onEmpresaClick(EmpresaTurismo empresa) {
                // Click en toda la card - mostrar detalles
                // TODO: Implementar navegación a detalles de empresa
            }

            @Override
            public void onVerToursClick(EmpresaTurismo empresa) {
                // Click en botón "Ver tours"
                // TODO: Implementar navegación a tours de la empresa
            }
        });

        binding.scrExplorar.rvEmpresasTurismo.setLayoutManager(new LinearLayoutManager(this));
        binding.scrExplorar.rvEmpresasTurismo.setAdapter(adapter);
    }

    private List<EmpresaTurismo> crearDatosPrueba() {
        List<EmpresaTurismo> empresas = new ArrayList<>();

        empresas.add(new EmpresaTurismo(
                "Inka Expeditions SAC",
                "Tours culturales y aventura",
                4.5f, 125, 8,
                "Cusco, Perú",
                R.drawable.ic_business_24
        ));

        empresas.add(new EmpresaTurismo(
                "Machu Picchu Travel",
                "Experiencias auténticas",
                4.2f, 89, 5,
                "Cusco, Perú",
                R.drawable.ic_business_24
        ));

        empresas.add(new EmpresaTurismo(
                "Arequipa Adventures",
                "Naturaleza y volcanes",
                4.7f, 156, 12,
                "Arequipa, Perú",
                R.drawable.ic_business_24
        ));

        return empresas;
    }


    private void setupRecyclerViewToursRecomendados() {
        List<TourRecomendado> tours = crearToursRecomendados();

        ToursRecomendadosAdapter adapter = new ToursRecomendadosAdapter(tours, tour -> {
            // Click en tour recomendado
            // TODO: Navegar a detalles del tour
        });

        binding.scrDashboard.rvToursRecomendados.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.scrDashboard.rvToursRecomendados.setAdapter(adapter);
    }

    private List<TourRecomendado> crearToursRecomendados() {
        List<TourRecomendado> tours = new ArrayList<>();

        tours.add(new TourRecomendado("Tour Machu Picchu", "Inka Expeditions",
                "S/ 350", 4.5f, "2 días", R.drawable.macchupicchu));
        tours.add(new TourRecomendado("Salkantay Trek", "Mountain Adventures",
                "S/ 280", 4.3f, "3 días", R.drawable.salcantay));
        tours.add(new TourRecomendado("Valle Sagrado", "Sacred Valley Tours",
                "S/ 120", 4.7f, "1 día", R.drawable.vallesagrado));

        return tours;
    }

    private void setupRecyclerViewReservas() {
        List<Reserva> todasLasReservas = crearReservasEstaticas();

        actualizarEstadisticas(todasLasReservas);

        // Inicializar con reservas próximas
        List<Reserva> reservasProximas = filtrarPorEstado(todasLasReservas, "Próxima");
        ReservasAdapter adapter = new ReservasAdapter(reservasProximas);

        binding.scrReservas.recyclerViewReservas.setLayoutManager(new LinearLayoutManager(this));
        binding.scrReservas.recyclerViewReservas.setAdapter(adapter);

        // Listener para las pestañas
        binding.scrReservas.tabLayoutReservas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                List<Reserva> reservasFiltradas;
                switch (tab.getPosition()) {
                    case 0: // Próximas
                        reservasFiltradas = filtrarPorEstado(todasLasReservas, "Próxima");
                        break;
                    case 1: // Completadas
                        reservasFiltradas = filtrarPorEstado(todasLasReservas, "Completada");
                        break;
                    case 2: // Canceladas
                        reservasFiltradas = filtrarPorEstado(todasLasReservas, "Cancelada");
                        break;
                    default:
                        reservasFiltradas = todasLasReservas;
                }
                ReservasAdapter nuevoAdapter = new ReservasAdapter(reservasFiltradas);
                binding.scrReservas.recyclerViewReservas.setAdapter(nuevoAdapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private List<Reserva> crearReservasEstaticas() {
        List<Reserva> reservas = new ArrayList<>();
        reservas.add(new Reserva("Tour Machu Picchu", "15 Oct 2025",
                "Inca Expeditions", "Próxima", R.drawable.macchupicchu));
        reservas.add(new Reserva("Salkantay Trek", "10 Nov 2025",
                "Mountain Adventures", "Próxima", R.drawable.salcantay));
        reservas.add(new Reserva("Valle Sagrado", "20 Sep 2025",
                "Peru Tours", "Completada", R.drawable.vallesagrado));
        reservas.add(new Reserva("Lago Titicaca", "05 Ago 2025",
                "Puno Trips", "Cancelada", R.drawable.lagotiticaca));
        return reservas;
    }

    private List<Reserva> filtrarPorEstado(List<Reserva> reservas, String estado) {
        List<Reserva> filtradas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getEstado().equals(estado)) {
                filtradas.add(reserva);
            }
        }
        return filtradas;
    }

    private void actualizarEstadisticas(List<Reserva> todasLasReservas) {
        int proximas = 0;
        int completadas = 0;
        int canceladas = 0;

        for (Reserva r : todasLasReservas) {
            if (r.getEstado().equals("Próxima")) proximas++;
            else if (r.getEstado().equals("Completada")) completadas++;
            else if (r.getEstado().equals("Cancelada")) canceladas++;
        }

        binding.scrReservas.txtReservasActivas.setText(String.valueOf(proximas));
        binding.scrReservas.txtReservasCompletadas.setText(String.valueOf(completadas));
        // El total gastado lo puedes dejar hardcodeado por ahora
    }


    private void setupRecyclerViewItinerario() {

        binding.scrSeguimiento.layoutSinTourActivo.setVisibility(View.GONE);
        binding.scrSeguimiento.bottomSheetContainer.setVisibility(View.VISIBLE);

        List<LugarItinerario> itinerario = crearItinerarioEstatico();

        ItinerarioAdapter adapter = new ItinerarioAdapter(itinerario);
        binding.scrSeguimiento.rvItinerario.setLayoutManager(new LinearLayoutManager(this));
        binding.scrSeguimiento.rvItinerario.setAdapter(adapter);
    }

    private List<LugarItinerario> crearItinerarioEstatico() {
        List<LugarItinerario> lugares = new ArrayList<>();
        lugares.add(new LugarItinerario("Qorikancha", "09:00", true, false));
        lugares.add(new LugarItinerario("Plaza de Armas", "10:30", false, true));
        lugares.add(new LugarItinerario("Sacsayhuamán", "12:00", false, false));
        lugares.add(new LugarItinerario("Q'enqo", "14:00", false, false));
        lugares.add(new LugarItinerario("Tambomachay", "15:30", false, false));
        return lugares;
    }

}
