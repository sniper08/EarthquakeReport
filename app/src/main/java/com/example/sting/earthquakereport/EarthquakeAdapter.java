package com.example.sting.earthquakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link EarthquakeAdapter} allows you to display data in a ListView using a list of {@link Earthquake}
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    /**
     * Constructor that allow you to create new {@link EarthquakeAdapter}
     * @param context
     * @param earthquakes
     */

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        super(context,0,earthquakes);
    }

    // Returns information about the earthquakes to be displayed in a ListView item according to
    // the given position

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
       View listItemView = convertView;
       if (listItemView == null){
           listItemView = LayoutInflater.from(getContext()).
                   inflate(R.layout.list_item,parent,false);
       }
       Earthquake currentEarthquake = getItem(position);

       TextView magnitudeTextView = listItemView.findViewById(R.id.magnitude_text_view);

       // Set the proper background color on the magnitude circle.
       // Fetch the background from the TextView, which is a GradientDrawable.
       GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

       // Get the appropriate background color based on the current earthquake magnitude
       int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

       // Set the color on the magnitude circle
       magnitudeCircle.setColor(magnitudeColor);
       magnitudeTextView.setText(formatMagnitude(currentEarthquake.getMagnitude()));

       // Get full location String
       String location = currentEarthquake.getLocation();
       String locationOffset;
       String primaryLocation;

       // Check type of location
       if (location.contains(" of ")){
           String[] locationContent = location.split(" of ");
           locationOffset = locationContent[0] + " of";
           primaryLocation = locationContent[1];
       } else {
           locationOffset = "Near the";
           primaryLocation = location;
       }

       // Set correct info in location offset field
       TextView locationOffsetTextView = listItemView.findViewById(R.id.location_offset_text_view);
       locationOffsetTextView.setText(locationOffset);

       // Set correct info in primary location field
       TextView primaryLocationTextView = listItemView.findViewById(R.id.primary_location_text_view);
       primaryLocationTextView.setText(primaryLocation);

       // Transform Unix Epoch time into readable time and date object
       Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

       TextView dateTextView = listItemView.findViewById(R.id.date_text_view);
       dateTextView.setText(formatDate(dateObject));

       TextView timeTextView = listItemView.findViewById(R.id.time_text_view);
       timeTextView.setText(formatTime(dateObject));

       return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    /**
     * Return color that will be used to highlight the magnitude according to value of it
     */
    private int getMagnitudeColor(double magnitude){
        int magnitudeColor;
        switch ((int) magnitude){
            case 0: case 1:
                magnitudeColor = R.color.magnitude1;
                break;
            case 2:
                magnitudeColor = R.color.magnitude2;
                break;
            case 3:
                magnitudeColor = R.color.magnitude3;
                break;
            case 4:
                magnitudeColor = R.color.magnitude4;
                break;
            case 5:
                magnitudeColor = R.color.magnitude5;
                break;
            case 6:
                magnitudeColor = R.color.magnitude6;
                break;
            case 7:
                magnitudeColor = R.color.magnitude7;
                break;
            case 8:
                magnitudeColor = R.color.magnitude8;
                break;
            case 9:
                magnitudeColor = R.color.magnitude9;
                break;
            case 10: default:
                magnitudeColor = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(),magnitudeColor);
    }
}


