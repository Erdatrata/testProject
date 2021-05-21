package com.example.tazpitapp;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
//    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setNavigationViewListener();
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        aToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        mDrawerLayout.addDrawerListener(aToggle);
//        toolbar = (Toolbar) findViewById(R.id.na);
//        toolbar.setNavigationIcon(R.drawable.ic_menu_camera);
//        setSupportActionBar(toolbar);
        aToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        recyclerView = (RecyclerView) findViewById(R.id.);
//        recyclerAdapter = new RecyclerAdapter(getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(recyclerAdapter);

//        navigationView.
//        setttings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }
    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Toast.makeText(MainActivity.this, ""+item, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.settings_button) {
            Intent intent = new Intent(this, SetActivity.class);
            startActivity(intent);
        }
        if (id == R.id.login_button) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.register_button) {
            Intent intent = new Intent(this, register.register1.class);
            startActivity(intent);
        }
        if (id == R.id.scenerios_button) {
            Intent intent = new Intent(this, SceneriosListActivity.class);
            startActivity(intent);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (aToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}