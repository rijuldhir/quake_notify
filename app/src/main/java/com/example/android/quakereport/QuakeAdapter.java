package com.example.android.quakereport;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
        TextView Place = (TextView) listItemView.findViewById(R.id.textView2);
        Place.setText(w.getPlace());

        TextView Date = (TextView) listItemView.findViewById(R.id.textView3);
        Date.setText(w.getDate());

        TextView Magnitude = (TextView) listItemView.findViewById(R.id.textView1);
        Magnitude.setText((w.getMag()));

        return listItemView;
    }
}
