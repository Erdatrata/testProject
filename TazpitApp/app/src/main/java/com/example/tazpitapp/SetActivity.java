package com.example.tazpitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tazpitapp.assistClasses.dayTime;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SetActivity extends AppCompatActivity {
    //----------global variables--------------------------
    //the radio buttons for choosing between gps or city
    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private RadioGroup locationRadioGroup;
    private RadioButton gpsButton;
    private RadioButton cityButton;

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
    private Button applyButton;
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


    //----------global variables END----------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_set);


        //set all the buttons for the settings activity
        //location settings
        locationRadioGroup = findViewById(R.id.locationRadioGroup);
        gpsButton = findViewById(R.id.radio_by_gps);
        cityButton = findViewById(R.id.radio_by_city);
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
        applyButton = findViewById(R.id.applySettingsButton);
        rightNow = null;
        Button[] daysArr = {daysButton1, daysButton2, daysButton3, daysButton4,
                daysButton5, daysButton6, daysButton7};

        //sharedPrefs
        sharedpreferences = getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        gson = new Gson();

        {
            //loading temps from mains
            dayTime dtDEF = new dayTime(0, 0, 0, 1);//default for first time
            String dtDEFSTR = gson.toJson(dtDEF);
            for (Button v : daysArr) {
                String idd = "" + v.getId();
                String tidd = "temp_" + idd;
                String dt = sharedpreferences.getString(idd, dtDEFSTR);
                editor.putString(tidd, dt);
            }
            editor.apply();
            //load location selection
            String gpsPref = sharedpreferences.getString("location","DEFAULT");
            if(!gpsPref.equals("DEFAULT")){
                if(gpsPref.equals("GPS")){
                    gpsButton.setChecked(true);
                } else {
                    cityButton.setChecked(true);
                }
            }

        }


        //setting event listeners

        //for location radio group
        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                locationSettingChecker(group, checkedId);
                editor.commit();
                System.out.println(sharedpreferences.getString("location_temp","CAT"));
            }
        });

        //for day listeners

        for (Button day : daysArr) {
            day.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    changeDay(v);
//                    Toast.makeText(SetActivity.this, ""+v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //for timePickers
        Button[] timers = {timePickerFrom, timePickerTo};
        for (Button b : timers) {
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    popTimePicker(v);
//                    Toast.makeText(SetActivity.this, ""+v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //for "all day" button
        AllDayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rightNow==null){
                    Toast.makeText(SetActivity.this, "אנא בחרו באחד הימים בכפתורים מימין"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                String idd = ""+rightNow.getId();
                String tidd = "temp_"+idd;
                dayTime dt = new dayTime(0, 0, 23, 59);//default for first time
                String toPut = gson.toJson(dt);
                editor.putString(tidd,toPut).commit();
                hourStart=0;hoursEnd=23;
                minuteStart=0;minutesEnd=59;


                timePickerFrom.setText("00:00");
                timePickerTo.setText("23:59");


            }
        });

        //for apply button
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyButtonFunction(daysArr);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 105){//if checking for gps permission
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ההרשאה התקבלה",
                        Toast.LENGTH_SHORT).show();
                if(!RequestPermissionCall())
                    startLocationService();
                editor.putString("location_temp","GPS");
            } else {
                Toast.makeText(this, "ההרשאה נדחתה, אנא אשרו אותה במידה ותרצו להשתמש בתכונה",
                        Toast.LENGTH_LONG).show();
                editor.putString("location_temp","city");
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
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void locationSettingChecker(RadioGroup group, int checkedId) {

        //this will give give us the button that we checked just now
        RadioButton checkedButton = (RadioButton) group.findViewById
                (locationRadioGroup.getCheckedRadioButtonId());

        String toString = "לפי מיקום GPS";
        if (checkedButton.getId() != gpsButton.getId()) {
            toString = "לפי עיר";
            stopLocationService();
        } else {
            //open dialogue requesting user authorization to use gps location
            if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){


            new AlertDialog.Builder(SetActivity.this)
                    .setTitle("צורך בקבלת אישור מיקום")
                    .setMessage("על מנת שנוכל ליידע אותך על אירועים באיזור," +
                            " אנא אשרו לנו לקבל את מיקומכם.")
                    .setPositiveButton("למעבר לאישור", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
//                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                //if phone version is M or greater
                                if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED){
                                    Toast.makeText(SetActivity.this, "שימוש במיקום אושר",
                                            Toast.LENGTH_SHORT).show();
                                    if(!RequestPermissionCall())
                                        startLocationService();
                                } else {
                                    requestPermissions(new String[]
                                        {Manifest.permission.ACCESS_BACKGROUND_LOCATION},105);
                                }


//                            } else {
//                                Toast.makeText(SetActivity.this, "שימוש במיקום אושר",
//                                        Toast.LENGTH_SHORT).show();
//                                editor.putString("location_temp","GPS");
//                            }
                        }
                    }).setNeutralButton("ביטול", new DialogInterface.OnClickListener() {
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
        Toast.makeText(SetActivity.this,"בדיקת מיקום: "+ toString, Toast.LENGTH_SHORT).show();
        editor.commit();
    }

    //this function will apply all the settings in this page back to the server
    private void changeDay(View v) {
        //if switching between days after changing hours, save into temp
        if (unsaved) {
            //take hours from both start and end, put into new dayTime
            saveTemp();
            //unsave unsaved
            unsaved = false;
        }
        rightNow = (Button) v;
        String idd = "temp_" + v.getId();

        dayTime dtDEF = new dayTime(0, 0, 0, 1);//default for first time
        String dtGetString = sharedpreferences.getString(idd, "DEFAULT");
        dayTime dtGET;

        if (!dtGetString.equals("DEFAULT"))
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


        //how to store gson back
//        toToast="Time:\t"+dtGET+", delte:\t"+dtGET.calcDelta();
//        Toast.makeText(getApplicationContext(), toToast, Toast.LENGTH_SHORT).show();
//        //add 1 hour to time
//        dtGET.setHourEnd(dtGET.getHourEnd()+1);
//        dtGetString=gson.toJson(dtGET);
//        editor.putString("time",dtGetString);
//        editor.commit();
    }

    //function to call and set the timer hours
    public void popTimePicker(View v) {
        if (rightNow == null) {
            Toast.makeText(this, "אנא בחרו באחד הימים בכפתורים מימין ", Toast.LENGTH_LONG).show();
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
        System.out.println("before time picker");
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
                                "לא ניתן לכוון זמן התחלה מאוחר מזמן סיום", Toast.LENGTH_LONG).show();
                        return;
                    }
                    hourStart = hourOfDay;
                    minuteStart = minutes;
                    toPut = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart);
                } else {//for lower textView
                    //check for time correctness here also
                    if (hourOfDay < hourStart || ((hourOfDay == hourStart) && (minutes <= minuteStart))) {
                        Toast.makeText(SetActivity.this,
                                "לא ניתן לכוון זמן סיום מוקדם מזמן התחלה", Toast.LENGTH_LONG).show();
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
        int style = R.style.Theme_MaterialComponents_Dialog_Alert;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener,
                hour, minute, true);
        timePickerDialog.show();
    }

    //saves number into a temp in sp
    public static void saveTemp() {
        String dtKey = "";
        String temp = tempName((rightNow).getId());

        dayTime toPut = new dayTime(hourStart, minuteStart, hoursEnd, minutesEnd);
        String stringified = gson.toJson(toPut);
        editor.putString(temp, stringified);
        editor.apply();
    }

    public static String tempName(int cur) {
        return "temp_" + cur;
    }

    //to be used if apply button pressed
    public void applyButtonFunction(Button[] daysArr) {
        //if unsaved, save it first
        if (unsaved) {
            //take hours from both start and end, put into new dayTime
            saveTemp();
            //unsave unsaved
            unsaved = false;
        }
        //put the dayTimes back into the sp
        for (Button v : daysArr) {
            String idd = "" + v.getId();
            String tidd = "temp_" + idd;
            String toPut = sharedpreferences.getString(tidd, null);
            editor.putString(idd, toPut);
        }

        //put gps-city into locationPref
        editor.putString("location",sharedpreferences.getString("location_temp",null));

        String gpsSet="DEFAULT";
        if(gpsButton.isChecked())
            gpsSet="GPS";
        else if(cityButton.isChecked())
            gpsSet="city";
        editor.putString("location",gpsSet);


        //finally, apply all edited
        editor.apply();
        //make sure to call the background service and tell them of a change of hours

        //send data to server
    }

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
        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
        if(!isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            intent.setAction(constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service started",Toast.LENGTH_SHORT).show();
        }
    }
    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),backgroundService.class);
            intent.setAction(constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service stopped",Toast.LENGTH_SHORT).show();
        }
    }

}