package com.example.sting.earthquakereport;

/**
 * {@link Earthquake} contains all the information related to an earthquake
 */
public class Earthquake {

    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mUrl;

    /**
     * Constructor used to create a new {@link Earthquake}
     * @param mMagnitude is the intensity of the earthquake
     * @param mLocation is the location in which the earthquake happened
     * @param mTimeInMilliseconds is the time expressed in UNIX Epoch Time in which the earthquake
     *                            happened
     */

    public Earthquake(double mMagnitude, String mLocation, long mTimeInMilliseconds, String mUrl) {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        this.mTimeInMilliseconds = mTimeInMilliseconds;
        this.mUrl = mUrl;
    }

    public double getMagnitude(){ return mMagnitude; }
    public String getLocation(){ return mLocation; }
    public long getTimeInMilliseconds(){ return mTimeInMilliseconds; }
    public String getUrl(){ return mUrl; }
}
