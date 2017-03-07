/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL ="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=5&limit=20";
    private QuakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        //      this, android.R.layout.simple_list_item_1, earthquakes);
        adapter = new QuakeAdapter(EarthquakeActivity.this,new ArrayList<Quakes>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Quakes w = adapter.getItem(view.getVerticalScrollbarPosition());
                // Did this type first
                Quakes w = adapter.getItem(position);
                String url = w.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        // Create a fake list of earthquake locations.
        //ArrayList<Quakes> earthquakes = QueryUtils.extractEarthquakes();
        //ArrayList<String> earthquakes = new ArrayList<>();
        /*earthquakes.add(new Quakes("4.5","San Francisco","Feb 2 , 2017"));
        earthquakes.add(new Quakes("4.5","London","Feb 2 , 2017"));
        earthquakes.add(new Quakes("4.5","Tokyo","Feb 2 , 2017"));
        earthquakes.add(new Quakes("4.5","Mexico City","Feb 2 , 2017"));
        earthquakes.add(new Quakes("4.5","Moscow","Feb 2 , 2017"));
        earthquakes.add(new Quakes("4.5","Rio de Janeiro","Feb 2 , 2017"));
        earthquakes.add(new Quakes("4.5","Paris","Feb 2 , 2017"));*/

        EarthquakeAsync level = new EarthquakeAsync();
        level.execute(USGS_REQUEST_URL);

        // Find a reference to the {@link ListView} in the layout

    }

    private class EarthquakeAsync extends AsyncTask<String,Void,ArrayList<Quakes>>
    {
        @Override
        protected ArrayList<Quakes> doInBackground(String... strings) {
            URL basic = createUrl(strings[0]);

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
        protected void onPostExecute(ArrayList<Quakes> data) {

            if (data.size()==0 || data.get(0)==null) {
                Toast.makeText(getApplicationContext(),"Cannot Connect to Net or No Data Available",Toast.LENGTH_LONG).show();
                return;
            }
            adapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            }
        }
    }
}
