package com.example.proyecto_2025.Activities_Administrador;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyecto_2025.R;

public class AdminMasFragment extends Fragment {
    public AdminMasFragment() { super(R.layout.fragment_admin_placeholder); }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        TextView tv = v.findViewById(R.id.txtPlaceholder);
        tv.setText("Más (Empresa, Reportes, Ajustes)");
    }
}
