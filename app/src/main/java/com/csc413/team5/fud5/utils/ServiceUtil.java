package com.csc413.team5.fud5.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * An Android adapter with methods to obtain information about running services.
 */
public class ServiceUtil {
    /**
     * Returns whether there is currently network connectivity (data is on) in specified context
     * @param context  the context from which to check
     * @return true if there is network connectivity, otherwise false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Returns whether location services are currently active in the specified context. Location
     * services are active if both GPS and network location services are enabled.
     * @param context the context from which to check
     * @return true if there location services are on, otherwise false
     */
    public static boolean isLocationServicesOn(Context context) {
        LocationManager lm = (LocationManager) context.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        // check whether GPS and network providers are enabled
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) { }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) { }

        // only show dialog if location services are not enabled
        return (gpsEnabled || networkEnabled);
    }
}
