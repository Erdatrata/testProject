package com.example.tazpitapp;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityMainBinding binding;
v
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setNavigationViewListener();
//        setContentView(R.layout.activity_main);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        aToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        mDrawerLayout.addDrawerListener(aToggle);
////        toolbar = (Toolbar) findViewById(R.id.nav);
//        toolbar.setNavigationIcon(R.drawable.ic_menu_camera);
//        setSupportActionBar(toolbar);
//        aToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        navigationView.setItemIconTintList(null);
////        recyclerView = (RecyclerView) findViewById(R.id.);
////        recyclerAdapter = new RecyclerAdapter(getApplicationContext());
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(layoutManager);
////        recyclerView.setAdapter(recyclerAdapter);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }
    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.login_button: {
                //do somthing
                Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (aToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Toast.makeText(this, ""+item, Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);

    }

    public void selectItem(int position) {
        Intent intent = null;
        Toast.makeText(this, "Position:\t"+position, Toast.LENGTH_SHORT).show();
//        switch(position) {
//            case 0:
//                intent = new Intent(this, LoginActivity.class);
//                break;
//            case 1:
//                intent = new Intent(this, Activity_1.class);
//                break;
//            case 4:
//                intent = new Intent(this, Activity_4.class);
//                break;
//
//            default :
//                intent = new Intent(this, Activity_0.class); // Activity_0 as default
//                break;
//        }
//
//        startActivity(intent);
    }
}