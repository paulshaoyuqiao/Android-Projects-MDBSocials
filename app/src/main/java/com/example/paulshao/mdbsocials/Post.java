package com.example.paulshao.mdbsocials;

import android.net.Uri;

import java.net.URI;

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


    //initiating the constructor of the post object
    public Post (String email, String eventName, String eventPictureURL, String shortDescription, int pplRSVPed, String key, String date)
    {
        this.email = email;
        this.eventName = eventName;
        this.eventPictureURL = eventPictureURL;
        this.pplRSVPed = pplRSVPed;
        this.shortDescription = shortDescription;
        this.key = key;
        this.date = date;
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

    public void setPplRSVPed(int pplRSVPed) {
        this.pplRSVPed = pplRSVPed;
    }
    public void setDate(String date){this.date=date;}
}
