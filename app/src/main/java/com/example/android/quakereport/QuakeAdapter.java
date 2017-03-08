package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.android.quakereport.R.id.magnitude;

/**
 * Created by Rijul on 3/4/2017.
 */

public class QuakeAdapter extends ArrayAdapter<Quakes> {

    public QuakeAdapter(Activity context, ArrayList<Quakes> z)
    {
        super(context, 0, z);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Quakes w = getItem(position);
        String Location = w.getLocation();
        String loc,Place;
        int i = Location.indexOf("of");
        if(i==-1)
        {
            loc ="Near the";
            Place = Location.substring(i+1,Location.length());
        }
        else{
            loc = Location.substring(0,i+2);
            Place = Location.substring(i+3,Location.length());}


        TextView located = (TextView) listItemView.findViewById(R.id.textView2);
        located.setText(loc);

        TextView OffsetPlace = (TextView) listItemView.findViewById(R.id.textView5);
        OffsetPlace.setText(Place);

        long timeInMilli = w.getTimeInMilliseconds();
        Date dateObject = new Date(timeInMilli);

        TextView Date = (TextView) listItemView.findViewById(R.id.textView3);
        Date.setText(w.getDate(dateObject));

        DecimalFormat formatter = new DecimalFormat("0.0");
        String outputMag = formatter.format(Double.parseDouble(w.getMag()));

        TextView Magnitude = (TextView) listItemView.findViewById(magnitude);

        GradientDrawable magnitudeCircle = (GradientDrawable) Magnitude.getBackground();

        int magnitudeColor = getMagnitudeColor(Double.parseDouble(w.getMag()));

        magnitudeCircle.setColor(magnitudeColor);

        Magnitude.setText(outputMag);

        TextView time = (TextView) listItemView.findViewById(R.id.textView4);
        time.setText(w.getTime(dateObject));
        return listItemView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
