package com.example.android.quakereport;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rijul on 3/4/2017.
 */

public class Quakes {
    private String mag;
    private String place;
    private String date;
    private long timeInMilliseconds;
    private String loc;
    private String Place;


    public Quakes(String mag,String place, String date)
    {
        this.date=date;
        this.mag=mag;
        this.place=place;
        timeInMilliseconds=Long.parseLong(date);
        int i = place.indexOf("of");
        if(i==-1)
        {loc ="Near the";
            Place = place.substring(i+1,place.length());
        }
        else{
            loc = place.substring(0,i+2);
            Place = place.substring(i+3,place.length());
        }
    }
    public String getMag()
    {return this.mag;}

    public String getLoc()
    {
        return this.loc;
    }

    public String getPlace()
    {return this.Place;}

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
