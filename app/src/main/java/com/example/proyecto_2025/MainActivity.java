package com.example.proyecto_2025;

import static com.example.proyecto_2025.R.*;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_2025.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registrar();

        cleanForm();

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                binding.textViewSb.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.textViewSb2.setText(String.valueOf(i - 20));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.floatingActionButton.setOnClickListener(view ->
                mostrarDialogoConMaterial());

        binding.floatingActionButton2.setOnClickListener(view ->
                mostrarDialogo());
    }

    public String cadenaRegistro() {
        String nombre = binding.editTextNombre.getText().toString();
        String apellido = binding.editTextApellido.getText().toString();
        String edadStr = binding.textFieldEdad.getEditText().getText().toString();
        //String progressSeekBar = String.valueOf(binding.seekBar.getProgress());
        String progressSeekBar = String.valueOf(binding.sliderDemo.getValue());
        String carrera = binding.radioButton.isChecked()?
                "Telecom":binding.radioButton2.isChecked()?
                "Electrónico":"";
        String curso = binding.checkBox.isChecked()?
                "Gtics":binding.checkBox2.isChecked()?
                "iweb":binding.checkBox3.isChecked()?
                "appsiot":binding.checkBox4.isChecked()?
                "T.Avan":"";
        String texto = nombre + " " + apellido + ": " + edadStr +
                " |  " + progressSeekBar + " | carrera: " + carrera +
                " | curso : " + curso + " | " + binding.spinner.getSelectedItem().toString();
        return texto;
    }

    public void registrar() {
        binding.buttonRegistrar.setOnClickListener(view -> {
            String texto = cadenaRegistro();
            mostrarRegistroConMaterial(texto);
            //Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
        });
    }

    private void cleanForm() {
        binding.buttonCleanForm.setOnClickListener(view -> {
            binding.editTextNombre.setText("");
            binding.editTextApellido.setText("");
            binding.textFieldEdad.getEditText().setText("");
            binding.seekBar.setProgress(0);
            binding.checkBox.setChecked(false);
            binding.checkBox2.setChecked(false);
            binding.checkBox3.setChecked(false);
            binding.checkBox4.setChecked(false);
        });
    }

    public void mostrarDialogo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Mensaje del profesor a los alumnos");
        alertDialog.setMessage("ya se acaba a la clase!!");

        alertDialog.setPositiveButton("YEEEE", (dialogInterface, i) -> {
            Log.d("msg-test", "YEE presionado");
        });

        alertDialog.setNegativeButton("NOOOO", ((dialogInterface, i) -> {
            Log.d("msg-test", "NOOOO presionado");
        }));

        alertDialog.show();
    }

    public void mostrarDialogoConMaterial() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Ultima clase");
        dialogBuilder.setMessage("No, nos vemos la próxima clase");
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    public void mostrarRegistroConMaterial(String texto) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Datos Registrados");
        dialogBuilder.setMessage(texto);
        dialogBuilder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> Log.d("msg-test","btn neutral"));
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }
}