package com.example.tazpitapp.assistClasses;

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


}
