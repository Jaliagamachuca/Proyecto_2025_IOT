package com.example.proyecto_2025.Activities_Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_2025.Activities_Chat.ChatRoomActivity;
import com.example.proyecto_2025.Activities_Guia.EditarPerfilActivityGuia;
import com.example.proyecto_2025.Activities_Superadmin.CambiarFotoActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.ConversationAdapter;
import com.example.proyecto_2025.data.auth.AuthRepository;
import com.example.proyecto_2025.data.repository.ChatRepository;
import com.example.proyecto_2025.databinding.ActivityUsuarioVistaInicialBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

import com.example.proyecto_2025.login.LoginActivity;
import com.example.proyecto_2025.model.Conversation;
import com.example.proyecto_2025.model.ConversationUI;
import com.example.proyecto_2025.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.example.proyecto_2025.adapter.EmpresasAdapter;
import com.example.proyecto_2025.Activities_Usuario.EmpresaTurismo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

public class Cliente_HomeActivity extends AppCompatActivity {

    private ActivityUsuarioVistaInicialBinding binding;
    private final ChatRepository chatRepo = new ChatRepository();
    private ListenerRegistration chatReg;

    private ConversationAdapter chatAdapter;
    private String myUid;

    private FirebaseFirestore db;

    // Raíces (ids de cada <include/>)
    private static final int SCR_DASHBOARD = R.id.scrDashboard;
    private static final int SCR_EXPLORAR  = R.id.scrExplorar;
    private static final int SCR_RESERVAS     = R.id.scrReservas;
    private static final int SCR_SEGUIMIENTO  = R.id.scrSeguimiento;

    private static final int SCR_CHAT = R.id.scrChat;

    private static final int SCR_PERFIL    = R.id.scrPerfil;




    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioVistaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        initChatSection();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 1) Notifs + Seed
        com.example.proyecto_2025.notif.NotificationHelper.ensureChannel(this);
        com.example.proyecto_2025.data.seed.SeedLoader.loadIfNeeded(this);

        // 2) Bottom bar
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);

        // 3) UI
        setupRecyclerViewEmpresas();           // mock visual (puedes dejarlo igual)
        setupRecyclerViewToursRecomendados();  // AHORA reserva real
        setupRecyclerViewReservas();           // lee local (simple)
        setupRecyclerViewItinerario();

        // 4) Atajos
        binding.scrDashboard.btnContactarSoporte.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_explorar);
            showScreen(SCR_EXPLORAR);
        });
        binding.scrDashboard.btnMisReservas.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_reservas);
            showScreen(SCR_RESERVAS);
        });

        // 5) Estado inicial + KPIs
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        showScreen(SCR_DASHBOARD);
        cargarKpisDesdeLocal();   // <--- NUEVO

        // Perfil (datos del usuario actual)
        cargarPerfilActual();
        configurarAccionesPerfil();
    }


    private String currentUserEmail() {
        return getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("current_user_email", "usuario@demo.com");
    }

    private void cargarKpisDesdeLocal() {
        // Lee de ReservaRepository y refresca KPIs del dashboard y la pantalla de Reservas
        new Thread(() -> {
            com.example.proyecto_2025.data.repository.ReservaRepository repo =
                    new com.example.proyecto_2025.data.repository.ReservaRepository(this);
            String email = currentUserEmail();

            int completadas = repo.countCompletadas(email);
            double total = repo.totalGastado(email);

            runOnUiThread(() -> {
                // KPIs del Dashboard (usa tus TextViews según binding del layout)
                binding.scrDashboard.txtTotalTours.setText(String.valueOf(completadas));
                binding.scrDashboard.txtTotalInvertido.setText(String.format("S/ %.2f", total));
            });
        }).start();
    }

    private boolean onBottomItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard)  { showScreen(SCR_DASHBOARD); return true; }
        else if (id == R.id.nav_explorar) { showScreen(SCR_EXPLORAR); return true; }
        else if (id == R.id.nav_reservas)  { showScreen(SCR_RESERVAS); return true; }
        else if (id == R.id.nav_seguimiento) { showScreen(SCR_SEGUIMIENTO); return true; }
        else if (id == R.id.nav_chat) { showScreen(SCR_CHAT); return true; }
        else if (id == R.id.nav_perfil) { showScreen(SCR_PERFIL); return true; }
        return false;
    }
    private void initChatSection() {

        if (chatAdapter == null) {
            binding.scrChat.rvConversaciones.setLayoutManager(new LinearLayoutManager(this));

            chatAdapter = new ConversationAdapter(this, c -> {
                Intent i = new Intent(this, ChatRoomActivity.class);
                i.putExtra("conversationId", c.id);
                i.putExtra("otherUserId", c.otherUserId);
                startActivity(i);
            });

            binding.scrChat.rvConversaciones.setAdapter(chatAdapter);

            binding.scrChat.etBuscarChat.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    chatAdapter.setQuery(String.valueOf(s));
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });

            binding.scrChat.chipTodos.setOnClickListener(v -> {
                chatAdapter.setRoleFilter("ALL");
                chatAdapter.setUnreadOnly(binding.scrChat.chipNoLeidos.isChecked());
            });

            binding.scrChat.chipGuias.setOnClickListener(v -> {
                chatAdapter.setRoleFilter("guia");
                chatAdapter.setUnreadOnly(binding.scrChat.chipNoLeidos.isChecked());
            });


            binding.scrChat.chipNoLeidos.setOnClickListener(v -> {
                chatAdapter.setUnreadOnly(binding.scrChat.chipNoLeidos.isChecked());
            });
        }

        if (myUid == null) return;

        if (chatReg != null) chatReg.remove();

        chatReg = chatRepo.listenMyConversations(myUid, new ChatRepository.ConversationsCb() {
            @Override
            public void onData(List<Conversation> list) {
                List<ConversationUI> ui = new ArrayList<>();

                for (Conversation c : list) {
                    if (c == null) continue;

                    String otherId = getOtherUserId(c, myUid);
                    String otherRole = (c.roles != null && otherId != null) ? c.roles.get(otherId) : null;

                    String otherName = (c.displayNames != null && otherId != null) ? c.displayNames.get(otherId) : null;
                    String otherPhoto = (c.photoUrls != null && otherId != null) ? c.photoUrls.get(otherId) : null;

                    boolean unread = (c.unread != null && c.unread.get(myUid) != null) && Boolean.TRUE.equals(c.unread.get(myUid));

                    ConversationUI row = new ConversationUI();
                    row.id = c.id;
                    row.otherUserId = otherId;
                    row.otherRole = otherRole;
                    row.otherPhotoUrl = otherPhoto != null ? otherPhoto : "";
                    row.title = buildTitle(otherRole, otherName, otherId);
                    row.lastMessage = c.lastMessage != null ? c.lastMessage : "";
                    row.time = ""; // luego lo formateamos con updatedAt
                    row.unread = unread;

                    ui.add(row);
                }

                chatAdapter.submit(ui);
                toggleEmptyChat();
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(binding.getRoot(), "Error chat: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        toggleEmptyChat();
    }

    private void toggleEmptyChat() {
        boolean empty = (chatAdapter == null || chatAdapter.getItemCount() == 0);
        binding.scrChat.emptyStateChat.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.scrChat.rvConversaciones.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private String getOtherUserId(Conversation c, String myUid) {
        if (c.participants == null) return null;
        for (String p : c.participants) {
            if (p != null && !p.equals(myUid)) return p;
        }
        return null;
    }

    private String buildTitle(String role, String name, String uid) {
        String who = (name != null && !name.trim().isEmpty()) ? name : (uid != null ? uid : "Usuario");
        if ("guia".equalsIgnoreCase(role)) return "Guía: " + who;
        if ("cliente".equalsIgnoreCase(role)) return "Cliente: " + who;
        return who;
    }


    private void showScreen(@IdRes int screenId) {
        View vDash     = binding.scrDashboard.getRoot();
        View vExplorar = binding.scrExplorar.getRoot();
        View vReservas = binding.scrReservas.getRoot();
        View vSeguimiento = binding.scrSeguimiento.getRoot();
        View vChat = binding.scrChat.getRoot();


        View vPerfil   = binding.scrPerfil.getRoot();

        vDash.setVisibility(View.GONE);
        vExplorar.setVisibility(View.GONE);
        vReservas.setVisibility(View.GONE);
        vSeguimiento.setVisibility(View.GONE);
        vChat.setVisibility(View.GONE);

        vPerfil.setVisibility(View.GONE);

        View target =
                (screenId == SCR_DASHBOARD) ? vDash :
                        (screenId == SCR_EXPLORAR)  ? vExplorar :
                                (screenId == SCR_RESERVAS)     ? vReservas :
                                        (screenId == SCR_SEGUIMIENTO)  ? vSeguimiento :
                                                (screenId == SCR_CHAT) ? vChat : vPerfil;


        target.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerViewEmpresas() {

        binding.scrExplorar.rvEmpresasTurismo
                .setLayoutManager(new LinearLayoutManager(this));

        db.collection("empresas")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(snaps -> {
                    List<EmpresaTurismo> empresas = new ArrayList<>();

                    for (DocumentSnapshot doc : snaps.getDocuments()) {

                        String nombre       = doc.getString("nombre");
                        String descripcion  = doc.getString("descripcionCorta");
                        String direccion    = doc.getString("direccion");
                        String adminId      = doc.getString("adminId"); // ✅ para chat luego

                        Double ratingDb     = doc.getDouble("ratingPromedio");
                        Double totalResDb   = doc.getDouble("totalReservas");
                        Double totalToursDb = doc.getDouble("totalToursActivos");

                        float rating        = ratingDb != null ? ratingDb.floatValue() : 0f;
                        int totalResenas    = totalResDb != null ? totalResDb.intValue() : 0;
                        int totalTours      = totalToursDb != null ? totalToursDb.intValue() : 0;

                        String empresaDocId = doc.getId();

                        // ✅ leer primera foto (downloadUrl) desde "fotos"
                        List<String> fotos = (List<String>) doc.get("fotos");
                        String logoUrl = (fotos != null && !fotos.isEmpty()) ? fotos.get(0) : null;

                        EmpresaTurismo e = new EmpresaTurismo(
                                empresaDocId,
                                nombre != null ? nombre : "Sin nombre",
                                descripcion != null ? descripcion : "",
                                rating,
                                totalResenas,
                                totalTours,
                                direccion != null ? direccion : "Sin dirección",
                                logoUrl,                     // ✅ NUEVO
                                adminId,                     // ✅ NUEVO (lo usaremos para botón mensaje)
                                R.drawable.ic_business_24    // fallback
                        );

                        empresas.add(e);
                    }

                    EmpresasAdapter adapter = new EmpresasAdapter(
                            empresas,
                            new EmpresasAdapter.OnEmpresaClickListener() {
                                @Override
                                public void onEmpresaClick(EmpresaTurismo empresa) {
                                    // TODO detalle empresa
                                }

                                @Override
                                public void onVerToursClick(EmpresaTurismo empresa) {

                                    if (empresa == null) {
                                        android.util.Log.e("VER_TOURS", "empresa == null");
                                        return;
                                    }

                                    android.util.Log.d("VER_TOURS", "empresa obj=" + empresa);
                                    android.util.Log.d("VER_TOURS", "empresa.getId()=" + empresa.getId());
                                    android.util.Log.d("VER_TOURS", "empresa.getNombre()=" + empresa.getNombre());
                                    android.util.Log.d("VER_TOURS", "empresa.getAdminId()=" + empresa.getAdminId()); // ✅

                                    Intent i = new Intent(Cliente_HomeActivity.this, ToursPorEmpresaActivity.class);
                                    i.putExtra("empresaDocId", empresa.getId());
                                    i.putExtra("empresaAdminId", empresa.getAdminId()); // ✅ CLAVE (fallback)
                                    i.putExtra("empresaNombre", empresa.getNombre());

                                    android.util.Log.d(
                                            "VER_TOURS",
                                            "intent extras empresaDocId=" + i.getStringExtra("empresaDocId")
                                                    + " empresaAdminId=" + i.getStringExtra("empresaAdminId")
                                                    + " empresaNombre=" + i.getStringExtra("empresaNombre")
                                    );

                                    startActivity(i);
                                }



                                // (el botón mensaje lo agregamos luego en el adapter)
                            }
                    );

                    binding.scrExplorar.rvEmpresasTurismo.setAdapter(adapter);
                })
                .addOnFailureListener(err -> {
                    Toast.makeText(this,
                            "Error cargando empresas: " + err.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatReg != null) chatReg.remove();
    }


    private void setupRecyclerViewToursRecomendados() {
        // Los ítems siguen siendo UI de ejemplo, pero al hacer click creamos una reserva REAL
        List<TourRecomendado> tours = crearToursRecomendados();

        ToursRecomendadosAdapter adapter = new ToursRecomendadosAdapter(tours, tour -> {
            // Para enlazar con nuestra DB local, elegimos un tour por nombre
            new Thread(() -> {
                var db = com.example.proyecto_2025.data.local.db.AppDatabase.get(this);
                // Busca el primero que tenga ese nombre en la semilla
                var list = db.tourDao().getAll();
                long tourId = -1;
                for (var t : list) if (t.nombre.equalsIgnoreCase(tour.getNombre())) { tourId = t.id; break; }
                if (tourId == -1 && !list.isEmpty()) tourId = list.get(0).id; // fallback

                var repo = new com.example.proyecto_2025.data.repository.ReservaRepository(this);
                long resId = repo.crearReserva(tourId, currentUserEmail());

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            resId > 0 ? "Reserva creada y notificada" : "No se pudo crear la reserva",
                            Toast.LENGTH_SHORT).show();
                    // Refresca KPIs y lista de reservas
                    cargarKpisDesdeLocal();
                    refrescarListaReservasProximas();
                });
            }).start();
        });

        binding.scrDashboard.rvToursRecomendados.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.scrDashboard.rvToursRecomendados.setAdapter(adapter);
    }

    private void refrescarListaReservasProximas() {
        new Thread(() -> {
            var repo = new com.example.proyecto_2025.data.repository.ReservaRepository(this);
            var proximas = repo.proximas(currentUserEmail());
            // Mapear a tu modelo visual Reserva (nombre, fecha, empresa, estado, imagen)
            List<Reserva> ui = new ArrayList<>();
            var db = com.example.proyecto_2025.data.local.db.AppDatabase.get(this);
            for (var r : proximas) {
                var t = db.tourDao().find(r.tourId);
                ui.add(new Reserva(t.nombre,
                        new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date(t.inicioUtc)),
                        "Empresa", "Próxima", // empresa real lo añadiremos luego con JOIN
                        R.drawable.macchupicchu // placeholder según portadaKey
                ));
            }
            runOnUiThread(() -> {
                binding.scrReservas.recyclerViewReservas.setLayoutManager(new LinearLayoutManager(this));
                binding.scrReservas.recyclerViewReservas.setAdapter(new ReservasAdapter(ui));
            });
        }).start();
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
            Intent i = new Intent(this, EditarPerfilActivityCliente.class);

            i.putExtra("nombre", binding.scrPerfil.tvNombre.getText().toString());
            i.putExtra("email", binding.scrPerfil.tvEmail.getText().toString());
            i.putExtra("telefono", binding.scrPerfil.tvTelefono.getText().toString());
            i.putExtra("dni", binding.scrPerfil.tvDni.getText().toString());
            i.putExtra("fechaNacimiento", binding.scrPerfil.tvFechaNacimiento.getText().toString());
            i.putExtra("domicilio", binding.scrPerfil.tvDomicilio.getText().toString());

            startActivity(i);
        });

        // 👉 SOLO CAMBIAR FOTO
        binding.scrPerfil.btnCambiarFoto.setOnClickListener(v -> {
            Intent i = new Intent(this, CambiarFotoActivityCliente.class);
            startActivity(i);
        });

        // Otros botones (editar perfil, cambiar foto, etc.) se pueden agregar luego.
    }

}
