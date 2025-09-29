package com.example.proyecto_2025.Activities_Administrador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.OfferRepository;
import com.example.proyecto_2025.data.TourRepository;
import com.example.proyecto_2025.model.*;
import java.util.*;

public class OfferCreateActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.offer_create);

        Guide guide = (Guide) getIntent().getSerializableExtra("guide");

        TextView tvGuide = findViewById(R.id.tvGuide);
        Spinner spTour = findViewById(R.id.spTour);
        TextView tvStart = findViewById(R.id.tvStart);
        TextView tvEnd = findViewById(R.id.tvEnd);
        RadioGroup rgPay = findViewById(R.id.rgPay);
        EditText etAmount = findViewById(R.id.etAmount);
        Button btnSend = findViewById(R.id.btnSend);

        tvGuide.setText("Guía: "+guide.getName());

        List<Tour> tours = TourRepository.get().withoutGuide();
        ArrayList<String> titles = new ArrayList<>();
        for(Tour t:tours) titles.add(t.getTitle());
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titles);
        spTour.setAdapter(ad);

        final Calendar start = Calendar.getInstance();
        final Calendar end   = Calendar.getInstance();

        tvStart.setOnClickListener(v-> pickDate(tvStart, start));
        tvEnd.setOnClickListener(v-> pickDate(tvEnd, end));

        btnSend.setOnClickListener(v->{
            if(spTour.getSelectedItem()==null){ Toast.makeText(this,"Selecciona un tour",Toast.LENGTH_SHORT).show(); return; }
            String tourId = tours.get(spTour.getSelectedItemPosition()).getId();
            Offer.PayMode payMode = (rgPay.getCheckedRadioButtonId()==R.id.rbFijo) ? Offer.PayMode.FIJO : Offer.PayMode.POR_HORA;
            double amount;
            try { amount = Double.parseDouble(etAmount.getText().toString()); }
            catch (Exception e){ Toast.makeText(this,"Monto inválido",Toast.LENGTH_SHORT).show(); return; }

            Offer o = new Offer(UUID.randomUUID().toString(), guide.getId(), tourId,
                    start.getTime(), end.getTime(), payMode, amount, Offer.Status.PENDIENTE);
            OfferRepository.get().add(o);

            Toast.makeText(this,"Oferta enviada",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OfferInboxActivity.class));
            finish();
        });
    }

    private void pickDate(TextView tv, Calendar cal){
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year); cal.set(Calendar.MONTH, month); cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tv.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month+1, year));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
