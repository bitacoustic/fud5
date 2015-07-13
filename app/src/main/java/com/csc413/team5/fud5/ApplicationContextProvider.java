package com.csc413.team5.fud5;

import android.app.Application;
import android.content.Context;

public class ApplicationContextProvider extends Application {

    /**
     * Keeps a reference of the application context
     */
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return mContext;
    }

}