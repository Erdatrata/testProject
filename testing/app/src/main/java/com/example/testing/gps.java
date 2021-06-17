package com.example.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class gps extends AppCompatActivity {


    @Override
    protected void onStop() {
        super.onStop();

        while(true) {
            try {
                Thread.sleep(5 * 1000);
                System.out.println("loop on stop ,5 sec pass");
                updateGps();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    private DocumentReference mDocRef;
    String name;
    boolean sleepMode=false;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Button setTest;
    EditText textTest;

    Switch sw_locationupdates, sw_gps;
    LocationCallback locationCallBack;
    boolean updateOn = false;//gps on/off
    //google api location servcies
    FusedLocationProviderClient fusedLocationProviderClient;


    LocationRequest locationRequest;//config file for all settings realted to fustedlocation

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);


        name= UUID.randomUUID().toString();
        mDocRef= FirebaseFirestore.getInstance().document("new/location/List/"+"tester-"+name);
        setTest=findViewById(R.id.setForTest);
        textTest=findViewById(R.id.nameTest);
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);

        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);


        //set all properties of locationRequest
        locationRequest = new LocationRequest();


        locationRequest.setInterval(30000);//how often the defult location occur
        locationRequest.setFastestInterval(5000);//same  at the most frequest update


        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        setTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!textTest.getText().toString().equals("")){
                    mDocRef= FirebaseFirestore.getInstance().document("new/location/List/"+"tester-"+textTest.getText().toString()+name);
                }
            }
        });
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };
        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Useing GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//update location frequently
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
            updateGps();


    }//end of create

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        tv_updates.setText("Location being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

            updateGps();


    }

    private void stopLocationUpdates() {
        tv_updates.setText("location no being tracked");

        tv_lat.setText("not tracking");
        tv_lon.setText("not tracking");
        tv_speed.setText("not tracking");
        tv_address.setText("not tracking");
        tv_accuracy.setText("not tracking");
        tv_altitude.setText("not tracking");
        tv_sensor.setText("not tracking");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

                updateGps();
            }
            else{
                Toast.makeText(this,"this app require gps",Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private void updateGps(){
        //get permissions from user
        //get current location
        //update the ui

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(gps.this);
        System.out.println("im here");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            System.out.println("and now im here");
            //permission for gps
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    System.out.println("even depper");
                    //location XXX into ui components
                    if (location!=null) {
                        updateUIValues(location);
                    }


                }
            });

        }
        else{
            //no permmison
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);

            }

        }
    }
    int i=0;
    private void updateUIValues(Location location) {
        //update all of the text view objects with new location
        i++;
            String tv_latInString=String.valueOf(location.getLatitude());
        String tv_lonInString=String.valueOf(location.getLongitude());
        String tv_accuracyInString=String.valueOf(location.getAccuracy());
        String tv_addressInString="";

            System.out.println(tv_latInString);
            System.out.println(tv_lonInString);
            tv_lat.setText(tv_latInString);
            tv_lon.setText(tv_lonInString);
            tv_accuracy.setText(tv_accuracyInString);

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            }
            else {
                tv_altitude.setText("Not available");
            }

            if (location.hasSpeed()) {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            } else {
                tv_speed.setText("Not available");
            }
            Geocoder geocoder = new Geocoder(gps.this);

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tv_addressInString=addresses.get(0).getAddressLine(0);
                tv_address.setText(tv_addressInString);
            } catch (Exception e) {
                tv_address.setText("Unale to get address");
            }
                final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};//file comeing in map object [key,data] example [name,"moshe"] //map comes as array at size one ,cause ansync resons
        dataToSave[0].put("lat-stage-"+ i,tv_latInString);
        dataToSave[0].put("lon"+"-stage-"+i,String.valueOf(location.getLongitude()));
        dataToSave[0].put("accuracy"+"-stage-"+i,String.valueOf(location.getAccuracy()));
        dataToSave[0].put("nameOfCalls"+"-stage-"+i,String.valueOf(i));

        if(tv_addressInString!=null&&!tv_addressInString.equals("")) {
            dataToSave[0].put("city" + "-stage-" + i,tv_addressInString);
                        mDocRef.set(dataToSave[0]).addOnSuccessListener(new OnSuccessListener<Void>() {//set into doc new/new on the server what's in dataToSave
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("testing", "saved");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tesing", "not saved");
                            }
                        });


            }

        }
}




