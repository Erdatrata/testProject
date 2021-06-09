package com.example.tazpitapp;

public class constants {

    //gps id and location state
    public static final int LOCATION_SERVICE_ID=175;
    public static final String ACTION_START_LOCATION_SERVICE="startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE="stopLocationService";


//SET ACTIVITY
public static final String SET_CITY= "city";
    public static final String SET_GPS= "GPS";
    public static final String onProgressChanged_KM=  "km";
    public static final String  AllDayPicker_TEMP=  "temp_";
    public static final String  EDITOR_LOACTIOM_TEMP=   "location_temp";












    //Shared preferences name of each file saved for sharing data
    public static final String SHARED_PREFS = "sharedPrefs";//main file for shared preferences
    public static final String SHARED_PREFS_LOCATION = "location";
    public static final String SHARED_PREFS_DEAFULT =   "DEFAULT";
    public static final String SHARED_PREFS_GPS =     "GPS";




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
    public static final String PRESSED_SCENARIO = "pressed scenario"; //also belongs to scenario detail
    public static final String CREDIT = "credit";
    public static final String MEDIAURL = "media url";

//belong to scenario detail
public static final String ISSIGEND = "isSigned";
    public static final String GEO = "geo:";

    // belong to day time
    public static final String CONSTRUCTOR_HOUR_PROBLEM= "Hours cannot exceed 23 or fall below 0" ;
    public static final String CONSTRUCTOR_MINTUES_PROBLEM= "Minutes cannot exceed 59 or fall below 0" ;
    public static final String END_BEFORE_START=   "End times cannot come before start time" ;
    public static final String AM=  "AM" ;
    public static final String PM=  "PM" ;
    public static final String setHourStartSetMinutesStartErrors= "beginning times cannot come before end times";








    //FIREBASE ROUTING AND ETC
    public static final String DOC_REF_SCENARIOS = "Scenarios"; //where our list of events are(firestore)
    public static final String DOC_REF_FILLED = "filled"; // the users who filled report for the event
    public static final String DOC_REF_ACCEPTED = "accepted"; //the users who signed up for event
    public static final String SCENARIO_TYPE_EVENT = "סוג האירוע" ; //type of the event
    public static final String SCENARIO_LOCATION = "מיקום" ; //type of the event
    public static final String DOC_REF_USERS = "Users" ; //USERS setting in firebase




}
