package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    //----------global variables--------------------------
    //the radio buttons for choosing between gps or city
    RadioGroup locationRadioGroup;
    RadioButton gpsButton;
    RadioButton cityButton;

    //the buttons for choosing between different days and timepicker
    Button daysButton1;//SUN
    Button daysButton2;//MON
    Button daysButton3;//TUE
    Button daysButton4;//WED
    Button daysButton5;//THU
    Button daysButton6;//FRI
    Button daysButton7;//SAT
    TimePicker timePicker;

    //apply settings button
    Button applyButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //set all the buttons for the settings activity
        //location settings
        locationRadioGroup = findViewById(R.id.locationRadioGroup);
        gpsButton=findViewById(R.id.radio_by_gps);
        cityButton=findViewById(R.id.radio_by_city);
        //day settings
        daysButton1=findViewById(R.id.day_sunday);
        daysButton2=findViewById(R.id.day_monday);
        daysButton3=findViewById(R.id.day_tuesday);
        daysButton4=findViewById(R.id.day_wednesday);
        daysButton5=findViewById(R.id.day_thursday);
        daysButton6=findViewById(R.id.day_friday);
        daysButton7=findViewById(R.id.day_saturday);
        //timepicker & apply
        timePicker=findViewById(R.id.TimePickerSettings);
        applyButton=findViewById(R.id.applySettingsButton);


        //setting event listeners
        //for day listeners
        Button[] daysArr = {daysButton1,daysButton2,daysButton3,daysButton4,daysButton5,
                daysButton6,daysButton7};
        for(Button day: daysArr){
            day.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    applySettings(v);
                }
            });
        }
    }


    //this function will apply all the settings in this page back to the server
    private void applySettings(View v){
        String toToast ="";
//        switch(v.getId()){
//            case R.id.day_sunday:
//                toToast="1";
//                break;
//            case R.id.day_monday:
//                toToast="2";
//                break;
//            case R.id.day_tuesday:
//                toToast="3";
//                break;
//            case R.id.day_wednesday:_:
//                toToast="4";
//                break;
//            case R.id.day_thursday:
//                toToast="5";
//                break;
//            case R.id.day_friday:
//                toToast="6";
//                break;
//            case R.id.day_saturday:
//                toToast="7";
//                break;
//        }
        SharedPreferences sharedpreferences = getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String idd= ""+v.getId();
        int getInt = sharedpreferences.getInt(idd,-1);
        editor.putInt(idd,getInt+1);
        editor.commit();

        toToast="id "+idd+": "+getInt;
        Toast.makeText(getApplicationContext(), toToast, Toast.LENGTH_LONG).show();
    }


    public void onDestroy() {

        super.onDestroy();
    }
}