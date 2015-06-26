package com.csc413.team5.appdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public class dbHelper extends SQLiteOpenHelper implements dbHelperInterface {
    /*Private Data members*/
//    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="fudfives.db";

    // Tables
    private static final String GREEN_TABLE_NAME = "greenList";
    private static final String YELLOW_TABLE_NAME = "yellowList";
    private static final String RED_TABLE_NAME = "redList";

    // Columns
    private static final String RESTAURANT_COLUMN_NAME = "restaurantName";


    public dbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Initialize queries
        String greenDBQuery = "CREATE TABLE " + GREEN_TABLE_NAME + "(" + RESTAURANT_COLUMN_NAME + ");";
        String yellowDBQuery = "CREATE TABLE " + YELLOW_TABLE_NAME + "(" + RESTAURANT_COLUMN_NAME + ");";
        String redDBQuery = "CREATE TABLE " + RED_TABLE_NAME + "(" + RESTAURANT_COLUMN_NAME + ");";

        // Concatenate into one query
        String query = greenDBQuery + yellowDBQuery + redDBQuery;

        db.execSQL(query);
    }

    @Override
    // DANGER ZONE, don't call this method yet. Not sure what does.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GREEN_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + YELLOW_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RED_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public boolean insertRestaurantToList(Restaurant restaurantEntry, int listClassification) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        boolean isDbInsertSuccess = false;

        // TODO: Find a better way to write this
        if (listClassification >= 1 || listClassification <= 3) {
            // if green
            if (listClassification == 1) {
                values.put(RESTAURANT_COLUMN_NAME, restaurantEntry.getBusinessName());
                db.insert(GREEN_TABLE_NAME, null, values);
            }

            // if red
            if (listClassification == 2) {
                values.put(RESTAURANT_COLUMN_NAME, restaurantEntry.getBusinessName());
                db.insert(YELLOW_TABLE_NAME, null, values);
            }

            // if red
            if (listClassification == 3) {
                values.put(RESTAURANT_COLUMN_NAME, restaurantEntry.getBusinessName());
                db.insert(RED_TABLE_NAME, null, values);
            }
        }
        db.close();

        // TODO: return if db.insert is success
        return isDbInsertSuccess;
    }

    @Override
    public ArrayList<String> showListContents(int listClassification) {
        ArrayList <String> listItems = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "";

        if (listClassification >= 1 || listClassification <= 3) {
            if (listClassification == 1) {                          // green
                query = "SELECT * FROM " + GREEN_TABLE_NAME;
            }
            else if (listClassification == 2) {                          // yellow
                query = "SELECT * FROM " + YELLOW_TABLE_NAME;
            }
            else                                                    // red
                query = "SELECT * FROM " + RED_TABLE_NAME;
        }
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        // add restaurants to array list
        while (!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex(RESTAURANT_COLUMN_NAME)) != null) {
                listItems.add(c.getString(c.getColumnIndex(RESTAURANT_COLUMN_NAME)));
            }

            c.moveToNext();
        }

        db.close();
        return listItems;
    }

    @Override
    public String showContents(int listClassification) {
        String contents = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + GREEN_TABLE_NAME;


        //Cursor points to a location in results.
        Cursor c = db.rawQuery(query, null);
        //Move to the first row.
        c.moveToFirst();

        //When beginning with first data string, continue to ends of database.
        while (!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex(RESTAURANT_COLUMN_NAME)) != null)
            {
                contents += c.getString(c.getColumnIndex(RESTAURANT_COLUMN_NAME));
                contents += "\n";
            }

            c.moveToNext();
        }
        db.close();

        return contents;
    }

    @Override
    public String getName() {
        return DATABASE_NAME;
    }
}
