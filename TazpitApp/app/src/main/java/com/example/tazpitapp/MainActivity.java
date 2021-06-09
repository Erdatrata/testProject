package com.example.tazpitapp;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity<imageView> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    RecyclerView showNews;
    String [] title;
    String [] data;
    String [] type;
    String [] date;
    String [] writer;
    String [] image;
    String [] url;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public  boolean getStateOfGps(){//return true or false if the gps is working
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(constants.gpsState,false);
    }
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager=
                (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager!=null){
            for(ActivityManager.RunningServiceInfo service:
                    activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(backgroundService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    private  void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            intent.setAction(constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service started",Toast.LENGTH_SHORT).show();
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
        if(FirebaseAuth.getInstance().getCurrentUser() != null){if(getStateOfGps()){
            startLocationService();
        }
        else{
            startCityService();
        }}else{stopLocationService();}

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

         showNews=(RecyclerView) findViewById(R.id.newsRecycle);
       FirebaseFirestore.getInstance().collection("news")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            title=new String[1];
                            data=new String[1];
                            type=new String[1];
                            date=new String[1];
                            writer=new String[1];
                            image=new String[1];
                            url=new String[1];
                            System.out.println("im here $$$");
                            List<String> titleList = new ArrayList<>();
                            List<String> dataList = new ArrayList<>();
                            List<String> typeList = new ArrayList<>();
                            List<String> dateList = new ArrayList<>();
                            List<String> writerList = new ArrayList<>();
                            List<String> imageList = new ArrayList<>();
                            List<String> urlList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                              titleList.add(document.getId());
                              dataList.add(document.get("data").toString());
                              typeList.add(document.get("type").toString());
                               Timestamp time=(Timestamp)document.getTimestamp("date");
                                Date date=time.toDate();
                                date.setHours(date.getHours()+3);
                                dateList.add(date.toLocaleString());
                                writerList.add(document.get("writer").toString());
                                imageList.add(document.get("image").toString());
                                urlList.add(document.get("url").toString());
                                //System.out.println("the image url is: "+image[0]);



                                Log.d("SUCCESARTICLE", document.getId() + " => " + document.getData());
                            }
                            title=titleList.toArray(new String[0]);
                            data=dataList.toArray(new String[0]);
                            type=typeList.toArray(new String[0]);
                            date=dateList.toArray(new String[0]);
                            writer=writerList.toArray(new String[0]);
                            image=imageList.toArray(new String[0]);
                            url=urlList.toArray(new String[0]);

                            //System.out.println("check the bla another " + title[0]);
//                            MyAdapter myAdapter=new MyAdapter(MainActivity.this, title,data,date,image,type,writer);
//                            showNews.setAdapter(myAdapter);
//                            showNews.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            try {
                                MyAdapter myAdapter = new DownloadLink().execute().get();
                                                            showNews.setAdapter(myAdapter);
                            showNews.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("FAILARTICLE", "Error getting documents: ", task.getException());
                        }
                    }

                });

//FIREBASE PARTTTTTTTTTTTTTTT






    }
    class DownloadLink extends AsyncTask<Void, Void, MyAdapter> {


        @Override
        protected MyAdapter doInBackground(Void... params) {
            Bitmap[] bitmap=imageToBitMapArray(image,image.length);
            MyAdapter myAdapter=new MyAdapter(MainActivity.this, title,data,date,bitmap,type,writer,url);


            return myAdapter;
        }
    }
    public static Bitmap[] imageToBitMapArray(String[] image,int len){
        Bitmap[] bitmaps=new Bitmap[len];
        for(int i=0;i<len;i++){
            bitmaps[i]=LoadImageFromWebOperations(image[i])[0];
        }

    return bitmaps;
    }
    public static Bitmap[] LoadImageFromWebOperations(String src) {

        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap[] myBitmap=new Bitmap[1];
            myBitmap[0] = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            Log.d("onComplet","main_1");
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
        if (id == R.id.contact_button) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tps.co.il/contact-us/"));
            startActivity(browserIntent);
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
    private void stopLocationService(){
        setStateOfGps(false);
        if(isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            intent.setAction(constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service stopped",Toast.LENGTH_SHORT).show();
        }
    }
    public void setStateOfGps(boolean state){
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(constants.gpsState, state);
        editor.apply();

    }
    public void startCityService(){
        if(!isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            startService(intent);
            Toast.makeText(this,"City service started",Toast.LENGTH_SHORT).show();

        }

    }
}