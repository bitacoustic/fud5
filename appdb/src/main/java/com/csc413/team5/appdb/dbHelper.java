package com.csc413.team5.appdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public class dbHelper extends SQLiteOpenHelper implements dbHelperInterface {
    private static final String TAG = "DATABASE";

    /*Private Data members*/
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="fudfive.db";

    // Table
    private static final String GREEN_RESTAURANTS_TABLE_NAME = "greenRestaurants";
    private static final String YELLOW_RESTAURANTS_TABLE_NAME = "yellowRestaurants";
    private static final String RED_RESTAURANTS_TABLE_NAME = "redRestaurants";

    // Columns
    private static final String RESTAURANT_ID_COLUMN = "RESTAURANT_ID";
    private static final String TIMESTAMP_COLUMN = "DATE_ADDED";


    // Helpers
    private static final String TEXTTYPE = " TEXT";
    private static final String DATETYPE = " DATETIME";
    private static final String COMMA_SEP = ", ";
    private static final String DEFAULT_FLAG = " DEFAULT";


    public dbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Initialize queries
        String createGreenList = "CREATE TABLE " + GREEN_RESTAURANTS_TABLE_NAME + "(" +
                RESTAURANT_ID_COLUMN + TEXTTYPE + COMMA_SEP +
                TIMESTAMP_COLUMN + DATETYPE + DEFAULT_FLAG + " CURRENT_TIMESTAMP" + ");";

        String createYellowList = "CREATE TABLE " + YELLOW_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + TEXTTYPE + COMMA_SEP +
                    TIMESTAMP_COLUMN + DATETYPE + DEFAULT_FLAG + " CURRENT_TIMESTAMP" + ");";

        String createRedList = "CREATE TABLE " + RED_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + TEXTTYPE + COMMA_SEP +
                    TIMESTAMP_COLUMN + DATETYPE + DEFAULT_FLAG + " CURRENT_TIMESTAMP" + ");";

        String[] listQueries = new String[]{createGreenList, createYellowList, createRedList};


        // Batch create tables
        for (String sqlQueries : listQueries) {
            db.execSQL(sqlQueries);
        }

//        db.execSQL(createGreenList);
    }

    @Override
    // DANGER ZONE, don't call this method yet. Not sure what does.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GREEN_RESTAURANTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + YELLOW_RESTAURANTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RED_RESTAURANTS_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public boolean insertRestaurantToList(Restaurant new_restaurant,int listClass) {
        SQLiteDatabase db = getWritableDatabase();

        if (listClass == 1) {
            db.execSQL("INSERT INTO " + GREEN_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + ") " + "VALUES(" +
                    "\"" + new_restaurant.getBusinessName() + "\"" + ");");
            Log.i("GREEN LIST:", new_restaurant.getBusinessName());
        }

        else if (listClass == 2) {
            db.execSQL("INSERT INTO " + YELLOW_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + ") " + "VALUES(" +
                    "\""+ new_restaurant.getBusinessName() + "\"" + ");");
            Log.i("YELLOW LIST:", new_restaurant.getBusinessName());
        }
        else {
            db.execSQL("INSERT INTO " + RED_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + ") " + "VALUES(" +
                    "\"" + new_restaurant.getBusinessName() + "\"" + ");");
            Log.i("RED LIST:", new_restaurant.getBusinessName());
        }
        return true;
    }

    @Override
    public boolean migrateRestaurantListItem(Restaurant my_restaurant, int fromList, int toList) {
        // green to yellow
        if (fromList == 1 && toList == 2) {
            deleteRestaurantFromGreenList(my_restaurant); // delete from green
            insertRestaurantToList(my_restaurant,2); // insert to yellow
            return true;
        }

        // green to red
        else if (fromList == 1 && toList == 3) {
            deleteRestaurantFromGreenList(my_restaurant); // delete from green
            insertRestaurantToList(my_restaurant,3);    // insert to red
            return true;
        }

        // yellow to green
        else if (fromList == 2 && toList == 1) {
            deleteRestaurantFromYellowList(my_restaurant); // delete from yellow
            insertRestaurantToList(my_restaurant, 1); // insert to green
            return true;
        }

        // yellow to red
        else if (fromList == 2 && toList == 3) {
            deleteRestaurantFromYellowList(my_restaurant); // delete from yellow
            insertRestaurantToList(my_restaurant,3);    // insert to green
            return true;
        }

        // red to green
        else if (fromList == 3 && toList == 1) {
            deleteRestaurantFromRedList(my_restaurant); // delete from red
            insertRestaurantToList(my_restaurant,1);    // insert to green
            return true;
        }
        // red to yellow
        else if (fromList == 3 && toList == 2) {
            deleteRestaurantFromRedList(my_restaurant); // delete from red
            insertRestaurantToList(my_restaurant,2); // insert to yellow;
            return true;
        }
        else {
        // something went wrong
            Log.d("Error:", "switchRestaurantList()");
            return false;
        }
    }

    @Override
    public boolean deleteRestaurantFromGreenList(Restaurant my_restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(GREEN_RESTAURANTS_TABLE_NAME, RESTAURANT_ID_COLUMN + "=" + my_restaurant.getBusinessName(), null) > 0;
    }

    @Override
    public boolean deleteRestaurantFromYellowList(Restaurant my_restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(YELLOW_RESTAURANTS_TABLE_NAME, RESTAURANT_ID_COLUMN + "=" + my_restaurant.getBusinessName(), null) > 0;
    }

    @Override
    public boolean deleteRestaurantFromRedList(Restaurant my_restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(RED_RESTAURANTS_TABLE_NAME, RESTAURANT_ID_COLUMN + "=" + my_restaurant.getBusinessName(), null) > 0;
    }

    @Override
    public int getRestaurantList(Restaurant my_restaurant) {
        int whichList = 0;
        if (    isRestaurantInGreenList(my_restaurant) &&
                !isRestaurantInYellowList(my_restaurant) &&
                !isRestaurantInRedList(my_restaurant)) {
            // green
            whichList = 1;

        } else if ( !isRestaurantInGreenList(my_restaurant) &&
                    isRestaurantInYellowList(my_restaurant) &&
                    !isRestaurantInRedList(my_restaurant)) {
            // yellow
            whichList = 2;
        } else if ( !isRestaurantInGreenList(my_restaurant) &&
                    !isRestaurantInYellowList(my_restaurant) &&
                    isRestaurantInRedList(my_restaurant)) {
            // red
            whichList = 3;
        } else {
            // something went wrong
            whichList = 0;
            Log.d("Error:", "getRestaurantList");
        }
        return whichList;
    }

    @Override
    public boolean isRestaurantInGreenList(Restaurant my_restaurant) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT EXISTS(SELECT 1 FROM " + GREEN_RESTAURANTS_TABLE_NAME +
                " WHERE " + RESTAURANT_ID_COLUMN + "=\"" + my_restaurant.getBusinessName() +
                "\"" + "LIMIT 1);";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {   // not in the list
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();
        db.close();
        return true;       // in list
    }

    @Override
    public boolean isRestaurantInYellowList(Restaurant my_restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT EXISTS(SELECT 1 FROM " + YELLOW_RESTAURANTS_TABLE_NAME +
                " WHERE " + RESTAURANT_ID_COLUMN + "=\"" + my_restaurant.getBusinessName() +
                "\"" + "LIMIT 1);";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) { // not in list
            cursor.close();
            return false;
        }

        cursor.close();
        db.close();
        return true;    // in list
    }

    @Override
    public boolean isRestaurantInRedList(Restaurant my_restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT EXISTS(SELECT 1 FROM " + RED_RESTAURANTS_TABLE_NAME +
                " WHERE " + RESTAURANT_ID_COLUMN + "=\"" + my_restaurant.getBusinessName() +
                "\"" + "LIMIT 1);";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {   // not in list
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();
        db.close();
        return true;    // in list
    }

    @Override
    public ArrayList<String> showListContents(int listClassification) {
        return null;
    }

    @Override
    public boolean checkDbExist(String path) {
        return false;
    }

    @Override
    public String showContents(int listClassification) {
        return null;
    }

    @Override
    public String getName() {
        return DATABASE_NAME;
    }

    @Override
    public String getDBPath() {
        return "dummy path";
    }
}
