package com.example.tazpitapp;
import com.example.tazpitapp.assistClasses.constants;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity<imageView> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SHARED_PREFS = "sharedPrefs";


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public ImageView nav_view_image;
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            mNavigationView.getMenu().setGroupVisible(R.id.logged_user_menu,true);
            mNavigationView.getMenu().setGroupVisible(R.id.unlogged_user_menu,false);
        }
        else{
            mNavigationView.getMenu().setGroupVisible(R.id.logged_user_menu,false);
            mNavigationView.getMenu().setGroupVisible(R.id.unlogged_user_menu,true);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nav_view_image = (ImageView)findViewById(R.id.nav_view_image);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        aToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        mDrawerLayout.addDrawerListener(aToggle);
        aToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            mNavigationView.getMenu().setGroupVisible(R.id.logged_user_menu,true);
            mNavigationView.getMenu().setGroupVisible(R.id.unlogged_user_menu,false);
        }
        else{
            mNavigationView.getMenu().setGroupVisible(R.id.logged_user_menu,false);
            mNavigationView.getMenu().setGroupVisible(R.id.unlogged_user_menu,true);

        }
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ImageView menuIcon = (ImageView) findViewById(R.id.nav_view_image);
        menuIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.RIGHT);
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
        if (id == R.id.about_button) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        if (id == R.id.logout_button) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedpreferences = getSharedPreferences(constants.SHARED_PREFS,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear().apply();
            this.recreate();
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

    public void setLogged(boolean logged){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logged", logged);
        editor.apply();

    }
    public boolean getLogged(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        System.out.println(sharedPreferences.getBoolean("logged",false));
        return sharedPreferences.getBoolean("logged",false);
    }
}