package com.example.proyecto_2025.Activities_Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_2025.BaseActivity;
import com.example.proyecto_2025.R;
import com.example.proyecto_2025.databinding.ActivityAdministradorVistaInicialBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class Administrador_Activity_VistaInicial extends BaseActivity {

    private ActivityAdministradorVistaInicialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorVistaInicialBinding.inflate(getLayoutInflater());
        setActivityContent(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // ViewPager + Tabs
        AdminHomePagerAdapter adapter = new AdminHomePagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, pos) -> {
            switch (pos) {
                case 0: tab.setText("CHATS"); tab.setIcon(R.drawable.ic_chat_24); break;
                case 1: tab.setText("TOURS"); tab.setIcon(R.drawable.ic_list_24); break;
                case 2: tab.setText("GUÍAS"); tab.setIcon(R.drawable.ic_badge_24); break;
                default: tab.setText("MÁS");  tab.setIcon(R.drawable.ic_more_24); break;
            }
        }).attach();

        // FAB visible solo en pestaña TOURS
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                binding.fab.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }
        });
        binding.fab.setOnClickListener(v ->
                startActivity(new Intent(this, TourFormActivity.class)));
    }

    static class AdminHomePagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        public AdminHomePagerAdapter(@NonNull androidx.fragment.app.FragmentActivity fa) { super(fa); }
        @NonNull @Override public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new AdminChatsFragment();
                case 1: return new AdminToursFragment();
                case 2: return new AdminGuiasFragment();
                default: return new AdminMasFragment();
            }
        }
        @Override public int getItemCount() { return 4; }
    }
}
