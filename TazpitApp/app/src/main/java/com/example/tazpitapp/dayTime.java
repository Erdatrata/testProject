package com.example.tazpitapp;

import java.security.InvalidParameterException;

public class dayTime {
    private int hourStart;
    private int minuteStart;
    private int minuteEnd;
    private int hourEnd;

    //constructor
    public dayTime(int hS, int mS, int hE, int mE){
        if(hS > 23 || hS < 0 || hE > 23 || hE < 0){
            throw new InvalidParameterException("Hours cannot exceed 23 or fall below 0");
        } else if(mS < 0 || mE < 0 || mS > 59 || mE > 59){
            throw new InvalidParameterException("Minutes cannot exceed 59 or fall below 0");
        } else if((hS>hE) || ((hS==hE)&&(mS>=mE)))
            throw new InvalidParameterException("End times cannot come before start time");
        hourStart = hS;
        hourEnd = hE;
        minuteStart = mS;
        minuteEnd = mE;
    }
    //setters and getters

    public int getHourStart() {return hourStart;}
    public int getMinuteStart() {return minuteStart;}
    public int getMinuteEnd() {return minuteEnd;}
    public int getHourEnd() {return hourEnd;}

    //check if start time is earlier than finish time
    public void setHourStart(int hourStart){
        if(hourStart<=this.getHourEnd())this.hourStart = hourStart;
        else throw new InvalidParameterException("beginning times cannot come before end times");

    }
    public void setMinuteStart(int minuteStart){
        if(this.getHourStart()<getHourEnd() ||
                ((this.getHourStart()==this.getHourEnd())&&(minuteStart<this.getMinuteEnd())))
        this.minuteStart = minuteStart;
        else throw new InvalidParameterException("beginning times cannot come before end times");

    }
    //check that end time is later than start time
    public void setHourEnd(int hourEnd){
        if(hourEnd>=this.getHourStart())this.hourEnd = hourEnd;
        else throw new InvalidParameterException("End times cannot come before beginning of time");
    }
    public void setMinuteEnd(int minuteEnd){if(this.getHourStart()<getHourEnd() ||
            ((this.getHourStart()==this.getHourEnd())&&(this.getMinuteStart()<minuteEnd)))
        this.minuteEnd = minuteEnd;
    else throw new InvalidParameterException("End times cannot come before beginning of time");
    }

    //returns time from start of active time to end of active time in minutes
    public int calcDelta(){
        return (this.hourEnd-this.hourStart)*60 + (this.minuteEnd-this.minuteStart);
    }//get hours by dividing this by 60, and minutes is mod 60's result


    public String toString(){
        String pastNoonStart="AM";
        String pastNoonEnd="AM";
        if(this.getHourStart() >= 12)
            pastNoonStart="PM";
        if(this.getHourEnd() >= 12)
            pastNoonEnd="PM";
        return this.getHourStart()%12+":"+this.getMinuteStart()+pastNoonStart+" to "+
                this.getHourEnd()%12+":"+this.getMinuteEnd()+pastNoonEnd;
    }

    public static void main(String[] args) {
        System.out.println(new dayTime(10,30,13,30));
    }
}