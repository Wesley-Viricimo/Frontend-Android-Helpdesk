package com.example.helpdesk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.helpdesk.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivityUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        toolbar = findViewById(R.id.toobarMainActivity);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragmentUI()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.toString()) {
            case "Home":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragmentUI()).commit();
                break;

            case "Tecnicos":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TecnicosFragmentUI()).commit();
                break;

            case "Clientes":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientesFragmentUI()).commit();
                break;

            case "Github":
                Toast.makeText(this, "Exibição do github", Toast.LENGTH_SHORT).show();
                break;

            case "Logout":
                Toast.makeText(this, "Saindo do App", Toast.LENGTH_SHORT).show();
                removerToken();
                abrirTelaLogin();
                break;

            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void removerToken() {
        SharedPreferences preferences = getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("TOKEN");
        editor.apply();
    }

    private void abrirTelaLogin() {
        Intent intent = new Intent(this, LoginActivityUI.class);
        startActivity(intent);
    }
}
