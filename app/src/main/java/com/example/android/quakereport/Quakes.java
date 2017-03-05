package com.example.android.quakereport;

/**
 * Created by Rijul on 3/4/2017.
 */

public class Quakes {
    private String mag;
    private String place;
    private String date;

    public Quakes(String mag,String place, String date)
    {
        this.date=date;
        this.mag=mag;
        this.place=place;

    }
    public String getMag()
    {return this.mag;}

    public String getPlace()
    {return this.place;}

    public  String getDate()
    {return this.date;}

}
