package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityAdministradorVistaInicialBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Administrador_Activity_VistaInicial extends BaseActivity {

    private ActivityAdministradorVistaInicialBinding binding;

    // Orden fiel a requerimientos
    private static final int TAB_EMPRESA  = 0;  // datos de empresa, fotos promocionales
    private static final int TAB_TOURS    = 1;  // CRUD tours
    private static final int TAB_GUIAS    = 2;  // estado/ubicación, asignaciones
    private static final int TAB_REPORTES = 3;  // ventas por servicio/tour
    private static final int TAB_CHAT     = 4;  // chat por empresa (atención al cliente)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorVistaInicialBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // ViewPager + Tabs
        AdminHomePagerAdapter adapter = new AdminHomePagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(4);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, pos) -> {
            switch (pos) {
                case TAB_EMPRESA:
                    tab.setText(R.string.tab_empresa);
                    tab.setIcon(R.drawable.ic_business_24);
                    break;
                case TAB_TOURS:
                    tab.setText(R.string.tab_tours);
                    tab.setIcon(R.drawable.ic_list_24);
                    break;
                case TAB_GUIAS:
                    tab.setText(R.string.tab_guias);
                    tab.setIcon(R.drawable.ic_badge_24);
                    break;
                case TAB_REPORTES:
                    tab.setText(R.string.tab_reportes);
                    tab.setIcon(R.drawable.ic_trending_up_24);
                    break;
                case TAB_CHAT:
                default:
                    tab.setText(R.string.tab_chat);
                    tab.setIcon(R.drawable.ic_chat_24);
                    break;
            }
        }).attach();

        // FAB visible solo donde el Admin crea/gestiona entidades
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                switch (position) {
                    case TAB_TOURS:
                        showFabForTours();
                        break;
                    case TAB_GUIAS:
                        showFabForGuias();
                        break;
                    default:
                        binding.fab.setVisibility(View.GONE);
                        binding.fab.setOnClickListener(null);
                        break;
                }
            }
        });

        // Inicio en EMPRESA (para completar datos y fotos al principio)
        binding.viewPager.setCurrentItem(TAB_EMPRESA, false);
    }

    private void showFabForTours() {
        binding.fab.setVisibility(View.VISIBLE);
        binding.fab.setImageResource(R.drawable.ic_add_24);
        binding.fab.setOnClickListener(v ->
                startActivity(new Intent(this, TourFormActivity.class)));
    }

    private void showFabForGuias() {
        binding.fab.setVisibility(View.VISIBLE);
        binding.fab.setImageResource(R.drawable.ic_person_add_24);
        binding.fab.setOnClickListener(v ->
                startActivity(new Intent(this, GuiaFormActivity.class)));
    }

    static class AdminHomePagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        public AdminHomePagerAdapter(@NonNull FragmentActivity fa) { super(fa); }

        @NonNull @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case TAB_EMPRESA:  return new AdminEmpresaFragment();   // ubicación, correo, teléfono, fotos (mín 2)
                case TAB_TOURS:    return new AdminToursFragment();     // CRUD + asignar guía con propuesta de pago
                case TAB_GUIAS:    return new AdminGuiasFragment();     // estado (asignado/no), ubicación en mapa
                case TAB_REPORTES: return new AdminReportesFragment();  // ventas por servicio/tour (ordenes)
                case TAB_CHAT:
                default:           return new AdminChatEmpresaFragment();// chat por empresa con clientes
            }
        }
        @Override public int getItemCount() { return 5; }
    }
}
