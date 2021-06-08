package com.example.tazpitapp;

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
    public static final String rangeChoice="range";
    public static final String LastTimeAndDate="lasttimeanddate";

    
    //TIME VARS


    public static int[] daysID = {
            R.id.day_sunday,
            R.id.day_monday,
            R.id.day_tuesday,
            R.id.day_wednesday,
            R.id.day_thursday,
            R.id.day_friday,
            R.id.day_saturday
    };
    public static String[] daysNames = {
            "sunday",
            "monday",
            "tuesday",
            "wednesday",
            "thursday",
            "friday",
            "saturday"
    };

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

    //function meant for translating the id
    // from the views into the proper sharedprefs key
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
//belong to fill report
public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PRESSED_SCENARIO = "pressed scenario";
    public static final String CREDIT = "credit";
    public static final String MEDIAURL = "media url";


    //FIREBASE ROUTING AND ETC
    public static final String DOC_REF_SCENARIOS = "Scenarios"; //where our list of events are(firestore)
    public static final String DOC_REF_FILLED = "filled"; // the users who filled report for the event
    public static final String DOC_REF_ACCEPTED = "accepted"; //the users who signed up for event






}
