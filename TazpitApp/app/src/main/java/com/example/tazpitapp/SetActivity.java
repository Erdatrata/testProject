package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tazpitapp.assistClasses.dayTime;
import com.google.gson.Gson;

import java.util.Locale;

public class SetActivity extends AppCompatActivity {
    //----------global variables--------------------------
        //the radio buttons for choosing between gps or city
        private RadioGroup locationRadioGroup;
        private RadioButton gpsButton;
        private RadioButton cityButton;

        //the buttons for choosing between different days and timepicker
        private static Button daysButton1;//SUN
        private static Button daysButton2;//MON
        private static Button daysButton3;//TUE
        private static Button daysButton4;//WED
        private static Button daysButton5;//THU
        private static Button daysButton6;//FRI
        private static Button daysButton7;//SAT
        private static Button timePickerFrom;
        private static Button timePickerTo;
        private static Button rightNow=null;

        //apply settings button
        private Button applyButton;
        private static int hour = 0;
    private static int minute=0;
        private static int hourStart = 0;
    private static int minuteStart=0;
        private static int hoursEnd = 0;
    private static int minutesEnd=0;
        //sharedprefs, dT and gson
        private SharedPreferences sharedpreferences;
        private static SharedPreferences.Editor editor;
        private static Gson gson;
        private static boolean unsaved;
    //----------global variables END----------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
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
        timePickerFrom=findViewById(R.id.timePickerSettingsFrom);
        timePickerTo=findViewById(R.id.timePickerSettingsTo);
        applyButton=findViewById(R.id.applySettingsButton);
        rightNow=null;

                //sharedPrefs
        sharedpreferences = getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        gson = new Gson();
//        local=getPreferences(MODE_PRIVATE);
//        localEditor = local.edit();

        //setting event listeners

        //for day listeners
        Button[] daysArr = {daysButton1,daysButton2,daysButton3,daysButton4,daysButton5,
                daysButton6,daysButton7};
        for(Button day: daysArr){
            day.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    changeDay(v);
//                    Toast.makeText(SetActivity.this, ""+v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //for timePickers
        Button[] timers= {timePickerFrom,timePickerTo};
        for(Button b:timers){
            b.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    popTimePicker(v);
//                    Toast.makeText(SetActivity.this, ""+v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //for apply button
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.apply();
            }
        });
    }



    //this function will apply all the settings in this page back to the server
    private void changeDay(View v){
        //if switching between days after changing hours, save into temp
        if(unsaved){
            //take hours from both start and end, put into new dayTime
            saveTemp();
            //unsave unsaved
            unsaved=false;
        }
        String toToast ="";
        rightNow = (Button)v;
        String idd= "temp_"+v.getId();

        dayTime dtDEF = new dayTime(0,0,0,1);//default for first time
        String dtGetString = sharedpreferences.getString(idd,"DEFAULT");
        dayTime dtGET;

        if(!dtGetString.equals("DEFAULT"))
            dtGET=gson.fromJson(dtGetString, dayTime.class);
        else
            dtGET=dtDEF;

        hourStart=dtGET.getHourStart();
        hoursEnd=dtGET.getHourEnd();
        minuteStart=dtGET.getMinuteStart();
        minutesEnd=dtGET.getMinuteEnd();

        ((Button)findViewById(R.id.timePickerSettingsFrom)).setText(
                String.format(Locale.getDefault(),"%02d:%02d",hourStart,minuteStart));
        ((Button)findViewById(R.id.timePickerSettingsTo)).setText(
                String.format(Locale.getDefault(),"%02d:%02d",hoursEnd,minutesEnd));


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
    public void popTimePicker(View v){
        if(rightNow==null){
            Toast.makeText(this, "אנא בחר באחד הימים בכפתורים מימין ", Toast.LENGTH_LONG).show();
            return;
        }
        if(v.getId()==R.id.timePickerSettingsFrom){
            hour=hourStart;minute=minuteStart;
        } else {
            hour=hoursEnd;minute=minutesEnd;
        }
        String timeString = ((Button)v).getText().toString();
        System.out.println("before time picker");
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                String toPut="";
//                if(v.getId()==)
                view.setHour(1);
                if(v.getId()==R.id.timePickerSettingsFrom){//for upper textView
                    //check for time correctness here (time should be chronological)
                    if(hourOfDay>hoursEnd || ((hourOfDay==hoursEnd)&&(minutes>=minutesEnd))){
                        Toast.makeText(SetActivity.this,
                                "לא ניתן לכוון זמן התחלה מאוחר מזמן סיום", Toast.LENGTH_LONG).show();
                        return;
                    }
                    hourStart = hourOfDay; minuteStart = minutes;
                    toPut=String.format(Locale.getDefault(),"%02d:%02d",hourStart,minuteStart);
                } else {//for lower textView
                    //check for time correctness here also
                    if(hourOfDay<hourStart || ((hourOfDay==hourStart)&&(minutes<=minuteStart))){
                        Toast.makeText(SetActivity.this,
                                "לא ניתן לכוון זמן סיום מוקדם מזמן התחלה", Toast.LENGTH_LONG).show();
                        return;
                    }
                    hoursEnd=hourOfDay;minutesEnd=minutes;
                    toPut=String.format(Locale.getDefault(),"%02d:%02d",hoursEnd,minutesEnd);
                }
                //if changes were made to time, make sure to let it be known in logic
                if(((Button)v).getText()!=toPut)
                    unsaved=true;
                ((Button)v).setText(toPut);
            }
        };
//        int style= AlertDialog.THEME_HOLO_DARK;
        int style= R.style.Theme_MaterialComponents_Dialog_Alert;

        TimePickerDialog timePickerDialog=new TimePickerDialog(this,style,onTimeSetListener,
                hour,minute,true);
        timePickerDialog.show();
    }

    //saves number into a temp in sp
    public static void saveTemp(){
        String dtKey="";
        String temp = tempName((rightNow).getId());

        dayTime toPut = new dayTime(hourStart,minuteStart,hoursEnd,minutesEnd);
        String stringified = gson.toJson(toPut);
        editor.putString(temp,stringified);
        editor.apply();
    }

    public static String tempName(int cur){
        return "temp_"+cur;
    }

}