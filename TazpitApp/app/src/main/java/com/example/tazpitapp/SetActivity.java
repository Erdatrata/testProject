package com.example.tazpitapp;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetActivity extends AppCompatActivity {
    //----------global variables--------------------------
    //the radio buttons for choosing between gps or city
    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private RadioGroup locationRadioGroup;
    private RadioButton gpsButton;
    private RadioButton cityButton;
    private SeekBar seekBar;
    private double range;
    private TextView rangeCont;

    //the buttons for choosing between different days and timepicker
    private Button daysButton1;//SUN
    private Button daysButton2;//MON
    private Button daysButton3;//TUE
    private Button daysButton4;//WED
    private Button daysButton5;//THU
    private Button daysButton6;//FRI
    private Button daysButton7;//SAT
    private Button timePickerFrom;
    private Button timePickerTo;
    private Button AllDayPicker;
    private static Button rightNow = null;

    //apply settings button
    private static int hour = 0;
    private static int minute = 0;
    private static int hourStart = 0;
    private static int minuteStart = 0;
    private static int hoursEnd = 0;
    private static int minutesEnd = 0;
    //sharedprefs, dT and gson
    private SharedPreferences sharedpreferences;
    private static SharedPreferences.Editor editor;
    private static Gson gson;
    private static boolean unsaved;
    private static Map<String, String> docData;

    //----------global variables END----------------------
    //----------global functions--------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_set);

        //sharedPrefs
        sharedpreferences = getSharedPreferences(constants.SHARED_PREFS,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        gson = new Gson();
        docData = new HashMap<>();

        //set all the buttons for the settings activity
        //location settings
        locationRadioGroup = findViewById(R.id.locationRadioGroup);
        gpsButton = findViewById(R.id.radio_by_gps);
        cityButton = findViewById(R.id.radio_by_city);
        seekBar=findViewById(R.id.gpsRangeBar);
        rangeCont=findViewById(R.id.rangeContainer);
        range=Double.parseDouble(""+sharedpreferences.getFloat(constants.rangeChoice,(float)0.5));

        //day settings
        daysButton1 = findViewById(R.id.day_sunday);
        daysButton2 = findViewById(R.id.day_monday);
        daysButton3 = findViewById(R.id.day_tuesday);
        daysButton4 = findViewById(R.id.day_wednesday);
        daysButton5 = findViewById(R.id.day_thursday);
        daysButton6 = findViewById(R.id.day_friday);
        daysButton7 = findViewById(R.id.day_saturday);
        AllDayPicker = findViewById(R.id.allDayButton);
        //timepicker & apply
        timePickerFrom = findViewById(R.id.timePickerSettingsFrom);
        timePickerTo = findViewById(R.id.timePickerSettingsTo);
        rightNow = null;
        Button[] daysArr = {daysButton1, daysButton2, daysButton3, daysButton4,
                daysButton5, daysButton6, daysButton7};

        //load location selection
        String gpsPref = sharedpreferences.getString(constants.SHARED_PREFS_LOCATION,constants.SHARED_PREFS_DEAFULT);
        if(!gpsPref.equals(constants.SHARED_PREFS_DEAFULT)){
            if(gpsPref.equals(constants.SHARED_PREFS_GPS)){
                gpsButton.setChecked(true);
            } else {
                cityButton.setChecked(true);
            }
        }
        rangeCont.setText(range+"km");
        double progress = (100*range-50)/9.5;
        seekBar.setProgress((int) (progress));
        //setting event listeners

        //for location radio group
        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                locationSettingChecker(group, checkedId);
                editor.commit();
                String toChange="";
                if(cityButton.isChecked())
                    toChange=constants.SET_CITY;
                else
                    toChange=constants.SET_GPS;
                editor.putString(constants.SHARED_PREFS_LOCATION,toChange).apply();
                docData.put(constants.SHARED_PREFS_LOCATION,toChange);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range = 0.5 + (9.5)*(((double) progress)/100);
                String toPut = String.format("%-1.2f",range);
                rangeCont.setText(toPut+constants.onProgressChanged_KM);}

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putFloat(constants.rangeChoice,(float) range).apply();
                docData.put(constants.rangeChoice,""+range);
            }
        });

        //for day listeners
        for (Button day : daysArr) {
            day.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    changeDay(v);
                }
            });
        }
        //for timePickers
        Button[] timers = {timePickerFrom, timePickerTo};
        for (Button b : timers) {
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    popTimePicker(v);
                }
            });
        }
        //for "all day" button
        AllDayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rightNow==null){
                    Toast.makeText(SetActivity.this, R.string.set_toast_plsChooseDay
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                String idd = ""+rightNow.getId();
                String tidd = constants.AllDayPicker_TEMP+idd;
                dayTime dt = new dayTime(0, 0, 23, 59);//default for first time
                String toPut = gson.toJson(dt);
                editor.putString(tidd,toPut).commit();
                hourStart=0;hoursEnd=23;
                minuteStart=0;minutesEnd=59;
                timePickerFrom.setText(R.string.min_time_start);
                timePickerTo.setText(R.string.max_time_end);
                saveTemp();
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        saveToServer();
    }

    @Override
    public void onPause(){
        super.onPause();
        saveToServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveToServer();
    }

    private void saveToServer(){
        if (unsaved)
            saveTemp();
        FirebaseAuth userIdentifier=FirebaseAuth.getInstance();
        String UID = userIdentifier.getCurrentUser().getUid();

        DocumentReference DRF = FirebaseFirestore.getInstance().document(constants.DOC_REF_USERS+"/"+UID);
        final boolean[] success = {true};
        final Exception[] failToRet = new Exception[1];
        for(Map.Entry<String,String> entry: docData.entrySet()){
            String key = entry.getKey();
            String val = entry.getValue();
            DRF.update(key,val).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    success[0] = false;
                    failToRet[0]=e;
                }
            });
            /*DRF.set(docData).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    success[0] = false;failToRet[0]=e;
                }
            });*/
            if(!success[0])
                break;
        }

        if(success[0]){
            Log.d("EVJA", "Setting update success");
            Toast.makeText(getApplicationContext(), R.string.set_toast_success, Toast.LENGTH_LONG).show();
        } else {
            Log.w("EVJA", "Error writing settings", failToRet[0]);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 105){//if checking for gps permission
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, R.string.set_toast_gpsGranted,
                        Toast.LENGTH_SHORT).show();
                if(!RequestPermissionCall())
//                    startLocationService();
                    editor.putString(constants.EDITOR_LOACTIOM_TEMP,constants.SET_GPS);
            } else {
                Toast.makeText(this, R.string.set_toast_gpsDenied,
                        Toast.LENGTH_LONG).show();
                editor.putString(constants.EDITOR_LOACTIOM_TEMP,constants.SET_CITY);
                cityButton.setChecked(true);
                gpsButton.setChecked(false);
                stopLocationService();
            }

        }
        editor.commit();
        if(requestCode== REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }
            else{
                Toast.makeText(this, R.string.set_toast_gpsDenied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void locationSettingChecker(RadioGroup group, int checkedId) {

        //this will give give us the button that we checked just now
        RadioButton checkedButton = (RadioButton) group.findViewById
                (locationRadioGroup.getCheckedRadioButtonId());

//        String toString = "לפי מיקום GPS";
        if (checkedButton.getId() != gpsButton.getId()) {
//            toString = "לפי עיר";
            stopLocationService();
        } else {
            //open dialogue requesting user authorization to use gps location
            if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){


                new AlertDialog.Builder(SetActivity.this)
                        .setTitle(R.string.set_alert_title)
                        .setMessage(R.string.set_alert_message)
                        .setPositiveButton(R.string.set_alert_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED){
                                    Toast.makeText(SetActivity.this, R.string.set_toast_gpsGranted,
                                            Toast.LENGTH_SHORT).show();
                                    if(!RequestPermissionCall())
                                        startLocationService();
                                } else {
                                    requestPermissions(new String[]
                                            {Manifest.permission.ACCESS_BACKGROUND_LOCATION},105);
                                }
                            }
                        }).setNeutralButton(R.string.set_alert_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //in case user didn't want to authorize us location
                        cityButton.setChecked(true);
                        gpsButton.setChecked(!true);
                    }
                }).setCancelable(false)
                        .create()
                        .show();
            }
            else {
                if(!RequestPermissionCall())
                    startLocationService();
            }}
//        Toast.makeText(SetActivity.this,"בדיקת מיקום: "+ toString, Toast.LENGTH_SHORT).show();
        editor.commit();
    }

    //this function will apply all the settings in this page back to the server
    private void changeDay(View v) {
        //if switching between days after changing hours, save into temp
        if (unsaved && rightNow!=null) {
            //take hours from both start and end, put into new dayTime
            saveTemp();
            //unsave unsaved
            unsaved = false;
        }
        rightNow = (Button) v;
        String tidd = constants.id2name(v.getId());

        dayTime dtDEF = new dayTime(0, 0, 0, 1);//default for first time
        String dtGetString = sharedpreferences.getString(tidd, constants.SHARED_PREFS_DEAFULT);
        dayTime dtGET;

        if (!dtGetString.equals(constants.SHARED_PREFS_DEAFULT))
            dtGET = gson.fromJson(dtGetString, dayTime.class);
        else
            dtGET = dtDEF;

        hourStart = dtGET.getHourStart();
        hoursEnd = dtGET.getHourEnd();
        minuteStart = dtGET.getMinuteStart();
        minutesEnd = dtGET.getMinuteEnd();

        ((Button) findViewById(R.id.timePickerSettingsFrom)).setText(
                String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart));
        ((Button) findViewById(R.id.timePickerSettingsTo)).setText(
                String.format(Locale.getDefault(), "%02d:%02d", hoursEnd, minutesEnd));

    }

    //function to call and set the timer hours
    public void popTimePicker(View v) {
        if (rightNow == null) {
            Toast.makeText(this, R.string.set_toast_plsChooseDay, Toast.LENGTH_LONG).show();
            return;
        }
        if (v.getId() == R.id.timePickerSettingsFrom) {
            hour = hourStart;
            minute = minuteStart;
        } else {
            hour = hoursEnd;
            minute = minutesEnd;
        }
        String timeString = ((Button) v).getText().toString();
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                String toPut = "";
//                if(v.getId()==)
                view.setHour(1);
                if (v.getId() == R.id.timePickerSettingsFrom) {//for upper textView
                    //check for time correctness here (time should be chronological)
                    if (hourOfDay > hoursEnd || ((hourOfDay == hoursEnd) && (minutes >= minutesEnd))) {
                        Toast.makeText(SetActivity.this,
                                R.string.set_toast_cantSetStartLater, Toast.LENGTH_LONG).show();
                        return;
                    }
                    hourStart = hourOfDay;
                    minuteStart = minutes;
                    toPut = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart);
                } else {//for lower textView
                    //check for time correctness here also
                    if (hourOfDay < hourStart || ((hourOfDay == hourStart) && (minutes <= minuteStart))) {
                        Toast.makeText(SetActivity.this,
                                R.string.set_toast_cantSetEndFirst, Toast.LENGTH_LONG).show();
                        return;
                    }
                    hoursEnd = hourOfDay;
                    minutesEnd = minutes;
                    toPut = String.format(Locale.getDefault(), "%02d:%02d", hoursEnd, minutesEnd);
                }
                //if changes were made to time, make sure to let it be known in logic
                if (((Button) v).getText() != toPut)
                    unsaved = true;
                ((Button) v).setText(toPut);
            }
        };
//        int style= AlertDialog.THEME_HOLO_DARK;
        //int style = R.style.Theme_MaterialComponents_Dialog_Alert;
        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener,
                hour, minute, true);
        timePickerDialog.show();
    }

    //saves days into the sp and datadoc
    public void saveTemp() {
        String idd = constants.id2name((rightNow).getId());//day's name
        //create the string to put in sp and send to server
        dayTime toPut = new dayTime(hourStart, minuteStart, hoursEnd, minutesEnd);
        String stringified = gson.toJson(toPut);
        //saving to sp
        editor.putString(idd, stringified).apply();
        //write to sendDoc
        docData.put(idd,stringified);
    }

    /**********************************
     *         GPS FUNCTIONS          *
     **********************************/

    private boolean RequestPermissionCall(){//true if needed false if permmison alridy given
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
            return true;
        }
        return false;
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
        setStateOfGps(true);
        if(!isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            intent.setAction(constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,R.string.set_toast_location_service_started,Toast.LENGTH_SHORT).show();
        }
    }
    private void stopLocationService(){
        setStateOfGps(false);
        if(isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            intent.setAction(constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,R.string.set_toast_location_service_stoped,Toast.LENGTH_SHORT).show();
        }
    }
    public void setStateOfGps(boolean state){
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(constants.gpsState, state);
        editor.apply();

    }
    public boolean getStateOfGps(){//return true or false if the gps is working
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(constants.gpsState,false);
    }
    public String getlatOfGps(){//get latitude of gps
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.latOfGps,"");
    }
    public String getlongOfGps(){//get longtitude of gps
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.longOfGps,"");
    }
//----------global functions END----------------------
}