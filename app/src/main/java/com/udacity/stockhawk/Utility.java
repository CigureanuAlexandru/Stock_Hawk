package com.udacity.stockhawk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;

public class Utility {

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyy.MM";
    /**
     * Converts db dateInMillis into nace format
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedYearMonth(long dateInMillis ) {
        SimpleDateFormat format = new SimpleDateFormat(Utility.DATE_FORMAT);
        String monthDayString = format.format(dateInMillis);
        return monthDayString;
    }

    public static boolean networkUp(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
