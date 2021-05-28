package com.example.tazpitapp.assistClasses;

import com.example.tazpitapp.R;

public class constants {

    //gps id and location state
    public static final int LOCATION_SERVICE_ID=175;
    public static final String ACTION_START_LOCATION_SERVICE="startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE="stopLocationService";






    //Shared preferences name of each file saved for sharing data
    public static final String SHARED_PREFS = "sharedPrefs";//main file for shared preferences
    //GPS VARS
    public static final String longOfGps = "longofgps";
    public static final String latOfGps ="latofgps";
    public static final String gpsState="gpsstate";
    
    //TIME VARS
    //to get key of certain day, create a string containing its name like this:
    //String key = "" +R.id.day_*
    //for example, to get the timeDay object for saturday, make key like this
    //String key = ""+R.id.day_saturday
    //String value = sharedpreferences.getString(key, "DEFAULT");
    //dayTime dt = gson.fromJson(value, dayTime.class);

    //function meant for translating the name
    // value from the server into the proper sharedprefs key
    public static int name2id(String id){
        int toReturn=0;
        switch(id){
            case "sunday":
                toReturn= R.id.day_sunday;
                break;
            case "monday":
                toReturn=R.id.day_monday;
                break;
            case "tuesday":
                toReturn=R.id.day_tuesday;
                break;
            case "wednesday":
                toReturn=R.id.day_wednesday;
                break;
            case "thursday":
                toReturn=R.id.day_thursday;
                break;
            case "friday":
                toReturn=R.id.day_friday;
                break;
            case "saturday":
                toReturn=R.id.day_saturday;
                break;
        }
        return toReturn;
    }

    //function meant for translating the sharedprefs key
    // value from the device into the proper server key
    public static String id2name(int id){
        String toReturn="";
        switch(id){
            case R.id.day_sunday:
                toReturn= "sunday";
                break;
            case R.id.day_monday:
                toReturn="monday";
                break;
            case R.id.day_tuesday:
                toReturn="tuesday";
                break;
            case R.id.day_wednesday:
                toReturn="wednesday";
                break;
            case R.id.day_thursday:
                toReturn="thursday";
                break;
            case R.id.day_friday:
                toReturn="friday";
                break;
            case R.id.day_saturday:
                toReturn="saturday";
                break;
        }
        return toReturn;
    }




}
