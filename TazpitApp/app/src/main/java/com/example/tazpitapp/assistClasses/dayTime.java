package com.example.tazpitapp.assistClasses;

import java.security.InvalidParameterException;

public class dayTime {
    public int hourStart;
    public int minuteStart;
    public int minuteEnd;
    public int hourEnd;


    public dayTime(int hS,int mS,int hE,int mE){
        if(hS > 23 || hS < 0 || hE > 23 || hE < 0){
            throw new InvalidParameterException("Hours cannot exceed 23 or fall below 0");
        } else if(mS < 0 || mE < 0 || mS > 59 || mE > 59){
            throw new InvalidParameterException("Minutes cannot exceed 59 or fall below 0");
        }
        hourStart = hS;
        hourEnd = hE;
        minuteStart = mS;
        minuteEnd = mE;
    }
}
