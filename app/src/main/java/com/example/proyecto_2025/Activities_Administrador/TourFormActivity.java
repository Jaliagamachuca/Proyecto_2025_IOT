package com.example.proyecto_2025.Activities_Administrador;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.R;
//import com.example.proyecto_2025.data.repository.TourRemoteDataSource; // ⭐ NUEVO
import com.example.proyecto_2025.data.repository.TourRepository;
import com.example.proyecto_2025.databinding.ActivityTourFormBinding;
import com.example.proyecto_2025.model.PuntoRuta;
import com.example.proyecto_2025.model.Tour;
import com.example.proyecto_2025.model.TourEstado;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;                      // ⭐ NUEVO
import com.google.firebase.auth.FirebaseUser;                      // ⭐ NUEVO

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TourFormActivity extends AppCompatActivity {

    private ActivityTourFormBinding binding;
    private int step = 1;
    private final Tour tour = new Tour();
    private final ArrayList<String> imagenes = new ArrayList<>();
    private final ArrayList<PuntoRuta> ruta = new ArrayList<>();
    private TourRepository repo;

    private MaterialCheckBox chkDesayuno, chkAlmuerzo, chkCena;

    private MaterialCheckBox chkIdiomaEspanol, chkIdiomaIngles, chkIdiomaPortugues,
            chkIdiomaFrances, chkIdiomaAleman, chkIdiomaItaliano, chkIdiomaJapones;

    private final Calendar calInicio = Calendar.getInstance();
    private final Calendar calFin    = Calendar.getInstance();
    private ActivityResultLauncher<Intent> pickImagesLauncher;
    private ActivityResultLauncher<Intent> pickPointLauncher;

    // ⭐ NUEVO: remote (Firestore)
    //private TourRemoteDataSource tourRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chkDesayuno = findViewById(R.id.chkDesayuno);
        chkAlmuerzo = findViewById(R.id.chkAlmuerzo);
        chkCena     = findViewById(R.id.chkCena);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nuevo tour");
        }

        chkIdiomaEspanol   = findViewById(R.id.chkIdiomaEspanol);
        chkIdiomaIngles    = findViewById(R.id.chkIdiomaIngles);
        chkIdiomaPortugues = findViewById(R.id.chkIdiomaPortugues);
        chkIdiomaFrances   = findViewById(R.id.chkIdiomaFrances);
        chkIdiomaAleman    = findViewById(R.id.chkIdiomaAleman);
        chkIdiomaItaliano  = findViewById(R.id.chkIdiomaItaliano);
        chkIdiomaJapones   = findViewById(R.id.chkIdiomaJapones);


        repo = new TourRepository(this);
        //tourRemote = new TourRemoteDataSource();     // ⭐ NUEVO

        // ================== HORA INICIO ==================
        binding.tvHoraInicio.setOnClickListener(v -> {
            int h = calInicio.get(Calendar.HOUR_OF_DAY);
            int m = calInicio.get(Calendar.MINUTE);

            new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        calInicio.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calInicio.set(Calendar.MINUTE, minute);
                        calInicio.set(Calendar.SECOND, 0);
                        binding.tvHoraInicio.setText(
                                String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                        );
                    },
                    h, m, true
            ).show();
        });

        // ================== HORA FIN ==================
        binding.tvHoraFin.setOnClickListener(v -> {
            int h = calFin.get(Calendar.HOUR_OF_DAY);
            int m = calFin.get(Calendar.MINUTE);

            new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        calFin.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calFin.set(Calendar.MINUTE, minute);
                        calFin.set(Calendar.SECOND, 0);
                        binding.tvHoraFin.setText(
                                String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                        );
                    },
                    h, m, true
            ).show();
        });

        // Selector de imágenes
        pickImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                    Intent data = result.getData();
                    try {
                        if (data.getClipData() != null) {
                            int c = data.getClipData().getItemCount();
                            for (int i = 0; i < c; i++) {
                                Uri u = data.getClipData().getItemAt(i).getUri();
                                getContentResolver().takePersistableUriPermission(
                                        u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                imagenes.add(u.toString());
                            }
                        } else if (data.getData() != null) {
                            Uri u = data.getData();
                            getContentResolver().takePersistableUriPermission(
                                    u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            imagenes.add(u.toString());
                        }
                    } catch (SecurityException ignore) {
                        if (data.getData() != null) imagenes.add(data.getData().toString());
                    }
                    binding.tvImgsCount.setText(imagenes.size() + " imágenes");
                });

        // Selector de punto en mapa
        pickPointLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                    Intent d = result.getData();
                    String nombre = d.getStringExtra("direccion");
                    double lat = d.getDoubleExtra("lat", 0d);
                    double lon = d.getDoubleExtra("lon", 0d);
                    PuntoRuta p = new PuntoRuta(nombre, lat, lon, "Actividad", 30);
                    ruta.add(p);
                    binding.tvPuntos.setText(ruta.size() + " puntos en ruta");
                });

        // Botones navegación wizard
        binding.btnNext.setOnClickListener(v -> next());
        binding.btnBack.setOnClickListener(v -> back());

        // Acciones
        binding.btnPickImgs.setOnClickListener(v -> abrirPickerImagenes());
        binding.btnAddPoint.setOnClickListener(v -> abrirPickerPunto());
        binding.btnGuardar.setOnClickListener(v -> save());

        updateStep();
    }

    private void abrirPickerImagenes() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImagesLauncher.launch(Intent.createChooser(i, "Selecciona imágenes"));
    }

    private void abrirPickerPunto() {
        // NOTA: ya usas ActivityResultLauncher (pickPointLauncher)
        pickPointLauncher.launch(new Intent(this, MapPickerActivity.class));
    }

    private void next() { if (step < 3) { step++; updateStep(); } }
    private void back() { if (step > 1) { step--; updateStep(); } }

    private void updateStep() {
        binding.step1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        binding.step2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.step3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        binding.btnBack.setVisibility(step == 1 ? View.INVISIBLE : View.VISIBLE);
        binding.btnNext.setVisibility(step == 3 ? View.GONE : View.VISIBLE);
        binding.btnGuardar.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        binding.tvPaso.setText("Paso " + step + " de 3");
    }

    private void save() {
        final String TAG = "TOUR_SAVE";

        android.util.Log.d(TAG, "==== CLICK GUARDAR ====");

        // Paso 1: básicos
        tour.titulo = binding.etTitulo.getText().toString().trim();
        tour.descripcionCorta = binding.etDescripcion.getText().toString().trim();

        String precioStr = binding.etPrecio.getText().toString().trim();
        String cuposStr  = binding.etCupos.getText().toString().trim();

        android.util.Log.d(TAG, "inputs: titulo='" + tour.titulo + "', descLen=" + (tour.descripcionCorta != null ? tour.descripcionCorta.length() : 0)
                + ", precioStr='" + precioStr + "', cuposStr='" + cuposStr + "'");

        try {
            tour.precioPorPersona = TextUtils.isEmpty(precioStr) ? 0 : Double.parseDouble(precioStr);
        } catch (Exception e) {
            android.util.Log.e(TAG, "parse precio FAIL: '" + precioStr + "'", e);
            Snackbar.make(binding.getRoot(), "Precio inválido", Snackbar.LENGTH_LONG).show();
            step = 1; updateStep(); return;
        }

        try {
            tour.cupos = TextUtils.isEmpty(cuposStr) ? 0 : Integer.parseInt(cuposStr);
        } catch (Exception e) {
            android.util.Log.e(TAG, "parse cupos FAIL: '" + cuposStr + "'", e);
            Snackbar.make(binding.getRoot(), "Cupos inválidos", Snackbar.LENGTH_LONG).show();
            step = 1; updateStep(); return;
        }

        tour.setIncluyeDesayuno(chkDesayuno.isChecked());
        tour.setIncluyeAlmuerzo(chkAlmuerzo.isChecked());
        tour.setIncluyeCena(chkCena.isChecked());

        android.util.Log.d(TAG, "servicios: desayuno=" + chkDesayuno.isChecked()
                + ", almuerzo=" + chkAlmuerzo.isChecked()
                + ", cena=" + chkCena.isChecked());

        tour.imagenUris.clear();
        tour.imagenUris.addAll(imagenes);
        android.util.Log.d(TAG, "imagenes count=" + tour.imagenUris.size());

        // ===== Idiomas seleccionados =====
        tour.idiomas.clear();
        if (chkIdiomaEspanol.isChecked())   tour.idiomas.add("Español");
        if (chkIdiomaIngles.isChecked())    tour.idiomas.add("Inglés");
        if (chkIdiomaPortugues.isChecked()) tour.idiomas.add("Portugués");
        if (chkIdiomaFrances.isChecked())   tour.idiomas.add("Francés");
        if (chkIdiomaAleman.isChecked())    tour.idiomas.add("Alemán");
        if (chkIdiomaItaliano.isChecked())  tour.idiomas.add("Italiano");
        if (chkIdiomaJapones.isChecked())   tour.idiomas.add("Japonés");

        android.util.Log.d(TAG, "idiomas=" + tour.idiomas);

        // Paso 2: ruta y fechas
        tour.ruta.clear();
        tour.ruta.addAll(ruta);
        android.util.Log.d(TAG, "ruta puntos=" + tour.ruta.size());

        // ===== FECHA + HORA DE INICIO =====
        int hIni = calInicio.get(Calendar.HOUR_OF_DAY);
        int mIni = calInicio.get(Calendar.MINUTE);

        android.util.Log.d(TAG, "horaInicio UI=" + String.format(java.util.Locale.getDefault(), "%02d:%02d", hIni, mIni)
                + " dpInicio=" + binding.dpInicio.getYear() + "-" + (binding.dpInicio.getMonth() + 1) + "-" + binding.dpInicio.getDayOfMonth());

        calInicio.set(
                binding.dpInicio.getYear(),
                binding.dpInicio.getMonth(),
                binding.dpInicio.getDayOfMonth(),
                hIni,
                mIni,
                0
        );
        tour.fechaInicioUtc = calInicio.getTimeInMillis();

        // ===== FECHA + HORA DE FIN =====
        int hFin = calFin.get(Calendar.HOUR_OF_DAY);
        int mFin = calFin.get(Calendar.MINUTE);

        android.util.Log.d(TAG, "horaFin UI=" + String.format(java.util.Locale.getDefault(), "%02d:%02d", hFin, mFin)
                + " dpFin=" + binding.dpFin.getYear() + "-" + (binding.dpFin.getMonth() + 1) + "-" + binding.dpFin.getDayOfMonth());

        calFin.set(
                binding.dpFin.getYear(),
                binding.dpFin.getMonth(),
                binding.dpFin.getDayOfMonth(),
                hFin,
                mFin,
                0
        );
        tour.fechaFinUtc = calFin.getTimeInMillis();

        android.util.Log.d(TAG, "fechas ms: ini=" + tour.fechaInicioUtc + " (" + new java.util.Date(tour.fechaInicioUtc) + ")"
                + " fin=" + tour.fechaFinUtc + " (" + new java.util.Date(tour.fechaFinUtc) + ")");

        // Paso 3: guía (por ahora solo propuesta, sin asignar guía)
        String propStr = binding.etPropuesta.getText().toString().trim();
        android.util.Log.d(TAG, "propStr='" + propStr + "', swPorcentaje=" + binding.swPorcentaje.isChecked());

        try {
            tour.propuestaPagoGuia = TextUtils.isEmpty(propStr) ? 0 : Double.parseDouble(propStr);
        } catch (Exception e) {
            android.util.Log.e(TAG, "parse propuesta FAIL: '" + propStr + "'", e);
            Snackbar.make(binding.getRoot(), "Propuesta de pago inválida", Snackbar.LENGTH_LONG).show();
            step = 3; updateStep(); return;
        }

        tour.pagoEsPorcentaje = binding.swPorcentaje.isChecked();

        // 🔗 Ligar tour con la empresa del admin (usamos UID del usuario como empresaId)
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        android.util.Log.d(TAG, "firebaseUser=" + (fbUser != null ? fbUser.getUid() : "null"));

        if (fbUser == null) {
            Snackbar.make(binding.getRoot(),
                    "No se pudo obtener tu sesión. Vuelve a iniciar sesión.",
                    Snackbar.LENGTH_LONG).show();
            android.util.Log.e(TAG, "ABORT: fbUser null");
            return;
        }
        tour.empresaId = fbUser.getUid();

        // ✅ Asegurar ID antes de upsert (para evitar fallos silenciosos / NPE)
        if (tour.id == null || tour.id.trim().isEmpty()) {
            tour.id = java.util.UUID.randomUUID().toString();
            android.util.Log.w(TAG, "tour.id estaba null/vacio -> generado: " + tour.id);
        } else {
            android.util.Log.d(TAG, "tour.id existente: " + tour.id);
        }

        // De inicio siempre BORRADOR. Luego desde Detalle se pasará a PENDIENTE_GUIA
        tour.guiaId = null;
        tour.estado = TourEstado.BORRADOR;

        android.util.Log.d(TAG, "final model: id=" + tour.id
                + ", empresaId=" + tour.empresaId
                + ", estado=" + (tour.estado != null ? tour.estado.name() : "null")
                + ", imgs=" + (tour.imagenUris != null ? tour.imagenUris.size() : -1)
                + ", ruta=" + (tour.ruta != null ? tour.ruta.size() : -1));

        // ==== Validaciones mínimas ====
        if (TextUtils.isEmpty(tour.titulo)) {
            android.util.Log.w(TAG, "VALIDATION FAIL: titulo vacío");
            Snackbar.make(binding.getRoot(), "Falta título", Snackbar.LENGTH_LONG).show();
            step = 1; updateStep(); return;
        }
        if (tour.imagenUris.size() < 2) {
            android.util.Log.w(TAG, "VALIDATION FAIL: imagenes < 2 (" + tour.imagenUris.size() + ")");
            Snackbar.make(binding.getRoot(), "Mínimo 2 imágenes", Snackbar.LENGTH_LONG).show();
            step = 1; updateStep(); return;
        }
        if (tour.ruta.size() < 2) {
            android.util.Log.w(TAG, "VALIDATION FAIL: ruta < 2 (" + tour.ruta.size() + ")");
            Snackbar.make(binding.getRoot(), "Agrega al menos 2 puntos en la ruta", Snackbar.LENGTH_LONG).show();
            step = 2; updateStep(); return;
        }
        if (tour.fechaFinUtc <= tour.fechaInicioUtc) {
            android.util.Log.w(TAG, "VALIDATION FAIL: fin <= inicio (" + tour.fechaFinUtc + " <= " + tour.fechaInicioUtc + ")");
            Snackbar.make(binding.getRoot(), "La fecha fin debe ser posterior a inicio", Snackbar.LENGTH_LONG).show();
            step = 2; updateStep(); return;
        }

        // Guardar (local + Firestore)
        try {
            android.util.Log.d(TAG, "calling repo.upsert(...) id=" + tour.id);
            repo.upsert(tour);
            android.util.Log.d(TAG, "repo.upsert OK (local). Firestore sync depende del repo.");
        } catch (Exception e) {
            android.util.Log.e(TAG, "repo.upsert FAIL", e);
            Snackbar.make(binding.getRoot(), "Error al guardar: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            return;
        }

        Snackbar.make(binding.getRoot(), "Tour guardado como borrador", Snackbar.LENGTH_LONG).show();
        android.util.Log.d(TAG, "DONE -> finish()");
        finish();
    }


}
