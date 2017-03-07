package com.example.android.quakereport;

/**
 * Created by Rijul on 3/5/2017.
 */

 import android.text.TextUtils;
 import android.util.Log;
 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;
 import java.util.ArrayList;
/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Quakes} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Quakes> extractEarthquakes(String response) {

        if(TextUtils.isEmpty(response))
            return null;
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Quakes> earthquakes = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(response);
            JSONArray featuresarray = root.getJSONArray("features");
            for(int i=0;i<featuresarray.length();i++)
            {
                JSONObject obj = featuresarray.getJSONObject(i);
                JSONObject prop = obj.optJSONObject("properties");
                String mag = prop.getString("mag");
                String place = prop.getString("place");
                String time = prop.getString("time");
                String url = prop.getString("url");
                Quakes object = new Quakes(mag,place,time,url);
                earthquakes.add(object);
            }
            // build up a list of Earthquake objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}