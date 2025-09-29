package com.example.proyecto_2025.Activities_Administrador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.OfferRepository;
import com.example.proyecto_2025.model.Guide;
import com.example.proyecto_2025.model.Offer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class OfferCreateActivity extends AppCompatActivity {

    // Listas simples para el spinner (mock por ahora)
    private final ArrayList<String> tourIds    = new ArrayList<>();
    private final ArrayList<String> tourTitles = new ArrayList<>();

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.offer_create);

        Guide guide = (Guide) getIntent().getSerializableExtra("guide");
        if (guide == null) { finish(); return; }

        TextView tvGuide   = findViewById(R.id.tvGuide);
        TextView tvStart   = findViewById(R.id.tvStart);
        TextView tvEnd     = findViewById(R.id.tvEnd);
        RadioGroup rgPay   = findViewById(R.id.rgPay);
        EditText etAmount  = findViewById(R.id.etAmount);
        Button btnSend     = findViewById(R.id.btnSend);
        android.widget.Spinner spTour = findViewById(R.id.spTour);

        tvGuide.setText("Guía: " + guide.getName());

        // --------- CARGA DE TOURS (MOCK) ----------
        seedToursForDemo(); // quita esto cuando conectes tu repo real

        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, tourTitles);
        spTour.setAdapter(ad);
        // ------------------------------------------

        final Calendar start = Calendar.getInstance();
        final Calendar end   = Calendar.getInstance();

        tvStart.setOnClickListener(v -> pickDate(tvStart, start));
        tvEnd.setOnClickListener(v -> pickDate(tvEnd, end));

        btnSend.setOnClickListener(v -> {
            if (spTour.getSelectedItemPosition() == android.widget.AdapterView.INVALID_POSITION) {
                Toast.makeText(this, "Selecciona un tour", Toast.LENGTH_SHORT).show();
                return;
            }
            String tourId = tourIds.get(spTour.getSelectedItemPosition());

            Offer.PayMode payMode =
                    (rgPay.getCheckedRadioButtonId() == R.id.rbFijo)
                            ? Offer.PayMode.FIJO
                            : Offer.PayMode.POR_HORA;

            double amount;
            try {
                amount = Double.parseDouble(etAmount.getText().toString().trim());
            } catch (Exception e) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            Offer o = new Offer(
                    UUID.randomUUID().toString(),
                    guide.getId(),
                    tourId,
                    start.getTime(),
                    end.getTime(),
                    payMode,
                    amount,
                    Offer.Status.PENDIENTE
            );
            OfferRepository.get().add(o);

            Toast.makeText(this, "Oferta enviada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OfferInboxActivity.class));
            finish();
        });
    }

    private void pickDate(TextView tv, Calendar cal){
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tv.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Datos de ejemplo para que el flujo sea presentable */
    private void seedToursForDemo() {
        if (!tourIds.isEmpty()) return;
        tourIds.add("t1");  tourTitles.add("City Tour Cusco");
        tourIds.add("t2");  tourTitles.add("Valle Sagrado Full Day");
        // agrega más si quieres
    }
}
