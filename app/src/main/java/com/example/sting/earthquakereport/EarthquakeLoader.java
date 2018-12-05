package com.example.sting.earthquakereport;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

/**
 * {@link EarthquakeLoader} fetches data from the provided URL by perform and HTTP request through
 * {@link QueryUtils}
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private String mURL;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        this.mURL = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i("Information","onStartLoading() called");
        if (isConnected()) forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if (mURL == null || !isConnected()){
            Log.i("Information","No Internet connection");
            return null;
        }

        Log.i("Information", "loadInBackground() called");
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakesData(mURL);
        return earthquakes;
    }

    private boolean isConnected() {
        // Check for connectivity status
        ConnectivityManager cm = (ConnectivityManager) getContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
