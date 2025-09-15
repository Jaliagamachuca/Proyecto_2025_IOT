package com.example.proyecto_2025;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_2025.databinding.ActivityBaseBinding;


public class BaseActivity extends AppCompatActivity {

    private ActivityBaseBinding baseBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseBinding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(baseBinding.getRoot());
        setSupportActionBar(baseBinding.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_config) {
            Toast.makeText(this, "Abrir Configuración", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_salir) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setActivityContent(View view) {
        baseBinding.contentFrame.addView(view);
    }
}
