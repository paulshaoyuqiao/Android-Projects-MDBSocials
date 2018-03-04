package com.example.paulshao.mdbsocials;

import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by paulshao on 9/27/17.
 */

public class Post {

    //Attributes of a post object
    String email;
    String eventName;
    String eventPictureURL;
    String shortDescription;
    int pplRSVPed;
    String key;
    String date;
    ArrayList<String> attendance;


    //initiating the constructor of the post object
    public Post (String email, String eventName, String eventPictureURL, String shortDescription, int pplRSVPed, String key, String date, ArrayList<String> attendance)
    {
        this.email = email;
        this.eventName = eventName;
        this.eventPictureURL = eventPictureURL;
        this.pplRSVPed = pplRSVPed;
        this.shortDescription = shortDescription;
        this.key = key;
        this.date = date;
        this.attendance = attendance;
        //attendance.add("1");
    }

    //creating an empty constructor
    Post(){

    }

    //the getter and setter methods
    public String getEmail ()
    {
        return email;
    }
    public String getEventName ()
    {
        return eventName;
    }
    public String getEventPictureURL ()
    {
        return eventPictureURL;
    }
    public String getShortDescription ()
    {
        return shortDescription;
    }
    public int getPplRSVPed ()
    {
        return pplRSVPed;
    }
    public String getKey() {return key;}
    public String getDate(){return date;}
    //public ArrayList<String> getAttendance(){return attendance;}

    //public void setPplRSVPed(int pplRSVPed) {
    //    this.pplRSVPed = pplRSVPed;
    //}
    public void setDate(String date){this.date=date;}
    public void setAttendance(ArrayList<String> attendance1){this.attendance = attendance1;}
}
