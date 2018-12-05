package com.example.sting.earthquakereport;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.button.MaterialButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main page that will display information about recent Earthquakes implements LoaderCallBacks to
 * control the creation of a Loader {@link EarthquakeLoader}
 */

public class EarthquakeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    EarthquakeAdapter adapter;
    TextView emptyTextView;
    ProgressBar progressBar;
    MaterialButton retryButton;
    LoaderManager loaderManager;
    private boolean firstLoad = true;
    private boolean isMenuCreated = false;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Information","onCreate() called");
        Log.i("Information","firstLoad: " + firstLoad);
        setContentView(R.layout.activity_report);

        adapter = new EarthquakeAdapter(this,new ArrayList<Earthquake>());
        emptyTextView = findViewById(R.id.empty_text_view);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyTextView.setText("");
                retryButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                retryButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLoader();
                    }
                },1000);
            }
        });

        ListView earthquakeListView = findViewById(R.id.container_list_view);
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setEmptyView(emptyTextView);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Earthquake earthquake = adapter.getItem(position);
                Uri webPage = Uri.parse(earthquake.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        startLoader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuItem = menu.findItem(R.id.menu_load);
        isMenuCreated = true;
        if (!firstLoad) menuItem.setVisible(true);
        else menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_load:
                emptyTextView.setText("");
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                Runnable r = new Runnable() {
                    public void run() {
                        startLoader();
                    }
                };
                handler.postDelayed(r, 1000);
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(EarthquakeActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
            default:
                break;
        }
        return true;
    }

    /**
     * Starts the the loader if there is an internet connection, shows no connection message
     * if device is disconnected
     */
    private void startLoader() {
        Log.i("Information","starLoader() called");
        // If network active start fetching data
        if (isConnected()) {
            Log.i("Information","Calling initLoader()");
            progressBar.setVisibility(View.VISIBLE);
            loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(0, null, this).forceLoad();
            firstLoad = true;
        } else {
            emptyTextView.setText("No internet connection");
            retryButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (!firstLoad){
                menuItem.setVisible(false);
            }
            firstLoad = true;
        }
    }

    private boolean isConnected() {
        // Check for connectivity status
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.i("Information","onCreateLoader() called");

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
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(EarthquakeActivity.this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        Log.i("Information","onLoadFinished() called..");

        if (earthquakes != null && !earthquakes.isEmpty()) {
            if (firstLoad) {
                Log.i("Information", "First Load " + String.valueOf(firstLoad));
                adapter.clear();
                adapter.addAll(earthquakes);
                progressBar.setVisibility(View.GONE);
                firstLoad = false;
            } else {
                Log.i("Information", "First Load " + String.valueOf(firstLoad));
            }
            if (isMenuCreated) menuItem.setVisible(true);
            retryButton.setVisibility(View.GONE);
        } else {
            adapter.clear();
            emptyTextView.setText("No earthquakes found");
            progressBar.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
            if (isMenuCreated) menuItem.setVisible(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        adapter.clear();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("Information","firstLoad: " + firstLoad);
        Log.i("Information","onDestroy() called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("Information","onStop() called");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("Information","onStart() called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("Information","onResume() called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if (!isConnected()){
            adapter.clear();
            emptyTextView.setText("No internet connection");
            retryButton.setVisibility(View.VISIBLE);
            if (isMenuCreated) menuItem.setVisible(false);
        }
        Log.i("Information","onRestart() called");
    }

}
