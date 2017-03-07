package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Created by Rijul on 3/7/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Quakes>>  {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String url;

    public EarthquakeLoader(Context context,String url) {
        super(context);
        this.url = url;
    }


    @Override
    public ArrayList<Quakes> loadInBackground() {

        if (url == null) {
            return null;
        }

        URL basic = createUrl(url);
        if(basic==null)
            return null;

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(basic);
        } catch (IOException e) {
            // TODO Handle the IOException
            Log.d("error","IO EXception");
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Quakes> earthquake = QueryUtils.extractEarthquakes(jsonResponse);
        // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
        return earthquake;
    }
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
    private String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if(url==null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200)
            {inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);}
            else
                Log.e(LOG_TAG,Integer.toString(urlConnection.getResponseCode()));
        } catch (IOException e) {
            Log.e(LOG_TAG, "IO Error", e);
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
