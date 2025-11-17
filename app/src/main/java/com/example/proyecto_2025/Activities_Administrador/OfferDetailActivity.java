package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.adapter.MessageAdapter;
import com.example.proyecto_2025.data.repository.GuideRepository;
import com.example.proyecto_2025.data.repository.OfferRepository;
import com.example.proyecto_2025.data.repository.TourRepository;
import com.example.proyecto_2025.model.Guide;
import com.example.proyecto_2025.model.Message;
import com.example.proyecto_2025.model.Offer;
import com.example.proyecto_2025.model.Tour;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OfferDetailActivity extends AppCompatActivity {

    private Offer offer;
    private MessageAdapter messageAdapter;
    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Obtener la oferta
        String offerId = getIntent().getStringExtra("offerId");
        if (offerId == null) {
            finish();
            return;
        }

        offer = OfferRepository.get().findById(offerId);
        if (offer == null) {
            finish();
            return;
        }

        // Configurar toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        TextView tvGuiaNombre = findViewById(R.id.tvGuiaNombre);
        TextView tvTourTitulo = findViewById(R.id.tvTourTitulo);
        TextView tvFechaInicio = findViewById(R.id.tvFechaInicio);
        TextView tvFechaFin = findViewById(R.id.tvFechaFin);
        TextView tvMonto = findViewById(R.id.tvMonto);
        Chip chipEstado = findViewById(R.id.chipEstado);
        rvMensajes = findViewById(R.id.rvMensajes);
        etMensaje = findViewById(R.id.etMensaje);
        FloatingActionButton btnEnviar = findViewById(R.id.btnEnviar);

        // Cargar datos de la oferta
        Guide guia = GuideRepository.get().byId(offer.getGuideId());
        Tour tour = new TourRepository(this).findById(offer.getTourId());

        tvGuiaNombre.setText(guia != null ? guia.getName() : "Guía desconocido");
        tvTourTitulo.setText(tour != null ? tour.titulo : "Tour desconocido");
        tvFechaInicio.setText(dateFormat.format(offer.getStartDate()));
        tvFechaFin.setText(dateFormat.format(offer.getEndDate()));

        String montoText = "S/ " + String.format(Locale.getDefault(), "%.2f", offer.getAmount())
                + " (" + (offer.getPayMode() == Offer.PayMode.FIJO ? "Fijo" : "Por hora") + ")";
        tvMonto.setText(montoText);

        // Estado con colores
        switch (offer.getStatus()) {
            case PENDIENTE:
                chipEstado.setText("⏳ Pendiente");
                chipEstado.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                break;
            case ACEPTADA:
                chipEstado.setText("✓ Aceptada");
                chipEstado.setChipBackgroundColorResource(android.R.color.holo_green_light);
                break;
            case RECHAZADA:
                chipEstado.setText("✗ Rechazada");
                chipEstado.setChipBackgroundColorResource(android.R.color.holo_red_light);
                break;
            case VENCIDA:
                chipEstado.setText("⏱ Vencida");
                chipEstado.setChipBackgroundColorResource(android.R.color.darker_gray);
                break;
        }

        // Configurar RecyclerView de mensajes
        messageAdapter = new MessageAdapter(offer.getMensajes());
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        rvMensajes.setAdapter(messageAdapter);

        // Scroll al último mensaje
        if (!offer.getMensajes().isEmpty()) {
            rvMensajes.scrollToPosition(offer.getMensajes().size() - 1);
        }

        // Crear algunos mensajes de ejemplo si está vacío (solo para demo)
        if (offer.getMensajes().isEmpty()) {
            crearMensajesDeEjemplo();
        }

        // Botón enviar mensaje
        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Escribe un mensaje", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Crear y agregar el mensaje
        Message mensaje = new Message(offer.getId(), Message.Sender.ADMIN, texto);
        offer.addMensaje(mensaje);

        // Actualizar en el repositorio
        OfferRepository.get().update(offer);

        // Actualizar UI
        messageAdapter.notifyItemInserted(offer.getMensajes().size() - 1);
        rvMensajes.scrollToPosition(offer.getMensajes().size() - 1);

        // Limpiar input
        etMensaje.setText("");

        Snackbar.make(findViewById(android.R.id.content),
                "Mensaje enviado ✓", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Crea mensajes de ejemplo para que se vea el chat funcionando
     * ELIMINAR ESTE MÉTODO cuando tengas datos reales
     */
    private void crearMensajesDeEjemplo() {
        Message m1 = new Message(offer.getId(), Message.Sender.GUIDE,
                "Hola, vi la oferta. ¿Podríamos negociar el precio?");
        Message m2 = new Message(offer.getId(), Message.Sender.ADMIN,
                "Claro, ¿cuánto propones?");
        Message m3 = new Message(offer.getId(), Message.Sender.GUIDE,
                "¿Qué tal S/ 180? Tengo experiencia en este tipo de tours.");

        offer.addMensaje(m1);
        offer.addMensaje(m2);
        offer.addMensaje(m3);

        OfferRepository.get().update(offer);
        messageAdapter.notifyDataSetChanged();
    }
}