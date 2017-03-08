package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;

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

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Quakes>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    //private static final String USGS_REQUEST_URL ="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=5&limit=20";
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private QuakeAdapter adapter;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private  TextView mEmptyStateTextView;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        progress = (ProgressBar) findViewById(R.id.progress);
        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            progress.setVisibility(View.GONE);
            mEmptyStateTextView.setText("No Internet Connection");
            return;
        }
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setEmptyView(mEmptyStateTextView);
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

        /*EarthquakeAsync level = new EarthquakeAsync();
        level.execute(USGS_REQUEST_URL);*/
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        // Find a reference to the {@link ListView} in the layout

    }

    @Override
    public Loader<ArrayList<Quakes>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("eventtype", "earthquake");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        Log.i(LOG_TAG,"This is OncreateLoader");
        return new EarthquakeLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Quakes>> loader, ArrayList<Quakes> data) {
        Log.i(LOG_TAG,"This is OnLoadFinished");
        progress.setVisibility(View.GONE);
        mEmptyStateTextView.setText("no_earthquakes");
        if (data==null) {
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

    @Override
    public void onLoaderReset(Loader<ArrayList<Quakes>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,  SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*private class EarthquakeAsync extends AsyncTask<String,Void,ArrayList<Quakes>>
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
                urlConnection.setReadTimeout(10000 );
                urlConnection.setConnectTimeout(15000 );
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
         *\/
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
    }*/
}
