package com.example.tazpitapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SetActivity extends AppCompatActivity {
    //----------global variables--------------------------
    //the radio buttons for choosing between gps or city
    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private RadioGroup locationRadioGroup;
    private RadioButton gpsButton;
    private RadioButton cityButton;
    private double range;
    private TextView rangeCont;
    private Switch syncButton;

    private boolean[] daysChanged;
    private Button timePickerFrom;
    private Button timePickerTo;
    private static Button rightNow = null;

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

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Objects.requireNonNull(this.getSupportActionBar()).hide();
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
        SeekBar seekBar = findViewById(R.id.gpsRangeBar);
        rangeCont=findViewById(R.id.rangeContainer);
        range=Double.parseDouble(""+sharedpreferences.getFloat(constants.rangeChoice,(float)0.5));
        syncButton=findViewById(R.id.sync_button2);

        //day settings
        //the buttons for choosing between different days and timepicker
        //SUN
        Button daysButton1 = findViewById(R.id.day_sunday);
        //MON
        Button daysButton2 = findViewById(R.id.day_monday);
        //TUE
        Button daysButton3 = findViewById(R.id.day_tuesday);
        //WED
        Button daysButton4 = findViewById(R.id.day_wednesday);
        //THU
        Button daysButton5 = findViewById(R.id.day_thursday);
        //FRI
        Button daysButton6 = findViewById(R.id.day_friday);
        //SAT
        Button daysButton7 = findViewById(R.id.day_saturday);
        Button allDayPicker = findViewById(R.id.allDayButton);
        //timepicker & apply
        timePickerFrom = findViewById(R.id.timePickerSettingsFrom);
        timePickerTo = findViewById(R.id.timePickerSettingsTo);
        rightNow = null;
        Button[] daysArr = {daysButton1, daysButton2, daysButton3, daysButton4,
                daysButton5, daysButton6, daysButton7};
        syncButton.setChecked(sharedpreferences.getBoolean("sync",true));

        //reset the daysChanged array
        daysChanged=new boolean[7];
        for(int i=0;i<7;i++)
            daysChanged[i]=false;

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
        locationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            locationSettingChecker(group, checkedId);
            editor.commit();
            String toChange="";
            if(cityButton.isChecked())
                toChange=constants.SET_CITY;
            else
                toChange=constants.SET_GPS;
            editor.putString(constants.SHARED_PREFS_LOCATION,toChange).apply();
            docData.put(constants.SHARED_PREFS_LOCATION,toChange);
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
            day.setOnClickListener(v -> changeDay(v));
        }
        //for timePickers
        Button[] timers = {timePickerFrom, timePickerTo};
        for (Button b : timers) {
            b.setOnClickListener(v -> popTimePicker(v));
        }
        //for "all day" button
        allDayPicker.setOnClickListener(v -> {
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
            findViewById(R.id.allDayButton).setBackgroundColor(getColor(R.color.tps_color_blue));
            ((Button)findViewById(R.id.allDayButton)).setTextColor(getColor(R.color.white));

        });

        //for sync button
        syncButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {//if user doesn't want to sync settings to server
                    editor.putBoolean("sync", false);
                    editor.apply();
                    return;
                }
                //otherwise change the state of the boolean and ask user if they'd like to sync to or from server
                {
                    editor.putBoolean("sync",true);
                    new AlertDialog.Builder(SetActivity.this)
                            .setMessage(R.string.set_alert_sync_message)
                            .setPositiveButton(R.string.set_alert_sync_accept, (dialogInterface, i) -> {
                                {//download settings from server
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference docRef = db.collection(constants.DOC_REF_USERS).
                                            document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    docRef.get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document = task1.getResult();
                                            System.out.println(document);
                                            String loc = "";
                                            if (document.exists()) {
                                                //location
                                                loc = Objects.requireNonNull(document.get(constants.SHARED_PREFS_LOCATION)).toString();
                                                if (loc.equals(""))
                                                    loc = "city";
                                                editor.putString(constants.SHARED_PREFS_LOCATION, loc);
                                                //range
                                                float range = Float.parseFloat((Objects.requireNonNull(document.get(constants.rangeChoice))).toString());
                                                if (range == 0)
                                                    range = 10;
                                                editor.putFloat(constants.rangeChoice, range);


                                                //days
                                                String dtDEF = (new Gson()). //will be used in case of null strings
                                                        toJson(new dayTime(0, 00, 23, 59));
                                                for (String d : constants.daysNames) {
                                                    String toStore = (Objects.requireNonNull(document.get(d))).toString();
                                                    if (toStore.equals(""))//if string from srv is null
                                                        toStore = dtDEF;//then input default 'all day' state
                                                    editor.putString(d, toStore);
                                                }
                                                editor.apply();


                                            }
                                        } else {
                                            Log.d("gabi_test", "settings failed with ", task1.getException());
                                        }
                                        //return to main
                                    });
                                }
                                editor.putBoolean("sync",true);
                                editor.apply();
                                Toast.makeText(SetActivity.this, R.string.syncToServer, Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }).setNeutralButton(R.string.set_alert_sync_write, (dialog, which) -> {
                                saveToServer();
                    }).setCancelable(false)
                            .create()
                            .show();
                }
            }
        });
        editor.apply();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (unsaved && rightNow!=null)
            saveTemp();
        if(sharedpreferences.getBoolean("sync",true))
            saveToServer();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (unsaved && rightNow!=null)
            saveTemp();
        if(sharedpreferences.getBoolean("sync",true))
            saveToServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unsaved && rightNow!=null)
            saveTemp();
        if(sharedpreferences.getBoolean("sync",true))
            saveToServer();
    }

    private void saveToServer(){
        if (unsaved && rightNow!=null)
            saveTemp();
        FirebaseAuth userIdentifier=FirebaseAuth.getInstance();
        String UID = Objects.requireNonNull(userIdentifier.getCurrentUser()).getUid();

        DocumentReference DRF = FirebaseFirestore.getInstance().document(constants.DOC_REF_USERS+"/"+UID);
        final boolean[] success = {true};
        final Exception[] failToRet = new Exception[1];
        for(Map.Entry<String,String> entry: docData.entrySet()){
            String key = entry.getKey();
            String val = entry.getValue();
            DRF.update(key,val).addOnFailureListener(e -> {
                success[0] = false;
                failToRet[0]=e;
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
            Toast.makeText(this, R.string.sync_fail, Toast.LENGTH_SHORT).show();
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
                        .setPositiveButton(R.string.set_alert_accept, (dialogInterface, i) -> {
                            // ask permission from the user about setting location
                                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage(R.string.set_alert_message_for_location)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, final int id) {
                                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            }
                                        })
                                        .setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, final int id) {
                                                dialog.cancel();
                                            }
                                        });
                                final AlertDialog alert = builder.create();
                                alert.show();
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
                        }).setNeutralButton(R.string.set_alert_deny, (dialog, which) -> {
                            //in case user didn't want to authorize us location
                            cityButton.setChecked(true);
                            gpsButton.setChecked(!true);
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
            daysChanged[Integer.parseInt((rightNow.getTag()).toString())]=true;

            saveTemp();
            //unsave unsaved
            unsaved = false;
        } else {
            if (rightNow != null && !(daysChanged[Integer.parseInt((rightNow.getTag()).toString())])) {
                rightNow.setBackgroundColor(getColor(R.color.tps_color_gray));
                rightNow.setTextColor(getColor(R.color.black));
            }
            v.getTag();
        }
        rightNow = (Button) v;
        rightNow.setBackgroundColor(getColor(R.color.tps_color_blue));
        rightNow.setTextColor(getColor(R.color.white));
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
        if(hourStart==0&&hoursEnd==23&&minuteStart==00&&minutesEnd==59){
            findViewById(R.id.allDayButton).setBackgroundColor(getColor(R.color.tps_color_blue));
            ((Button)findViewById(R.id.allDayButton)).setTextColor(getColor(R.color.white));
        } else {
            findViewById(R.id.allDayButton).setBackgroundColor(getColor(R.color.tps_color_gray));
            ((Button)findViewById(R.id.allDayButton)).setTextColor(getColor(R.color.black));
        }

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
        //apply settings button
        int hour = 0;
        int minute = 0;
        if (v.getId() == R.id.timePickerSettingsFrom) {
            hour = hourStart;
            minute = minuteStart;
        } else {
            hour = hoursEnd;
            minute = minutesEnd;
        }
        String timeString = ((Button) v).getText().toString();
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, minutes) -> {
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
            if (((Button) v).getText() != toPut) {
                unsaved = true;
                if(hourStart==0&&hoursEnd==23&&minuteStart==00&&minutesEnd==59){
                   findViewById(R.id.allDayButton).setBackgroundColor(getColor(R.color.tps_color_blue));
                    ((Button)findViewById(R.id.allDayButton)).setTextColor(getColor(R.color.white));
        }
            }
            ((Button) v).setText(toPut);
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
        editor.apply();
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