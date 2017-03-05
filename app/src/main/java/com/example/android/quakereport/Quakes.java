package com.example.android.quakereport;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rijul on 3/4/2017.
 */

public class Quakes {
    private String mag;
    private String place;
    private String date;
    private String url;
    private long timeInMilliseconds;


    public Quakes(String mag,String place, String date, String url)
    {
        this.date=date;
        this.mag=mag;
        this.place=place;
        timeInMilliseconds=Long.parseLong(date);
        this.url =url;
        }


    public String getMag()
    {return this.mag;}

    public String getUrl(){
        return this.url;
    }

    public String getLocation()
    {
        return this.place;
    }

    public long getTimeInMilliseconds()
    {
        return timeInMilliseconds;
    }

    public  String getDate(Date dateObject)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    public  String getTime(Date dateObject){

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    /**
     * Return the formatted time string (i.e. "4:30 PM") from a Date object.
     */

}
