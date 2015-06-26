package com.csc413.team5.appdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public class dbHelper extends SQLiteOpenHelper implements dbHelperInterface {
    public static final String DB_NAME = "appdb.db";
    public static final String GREEN_TABLE_NAME = "greenList";
    public static final String YELLOW_TABLE_NAME = "yellowList";
    public static final String RED_TABLE_NAME = "redList";


    public dbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public boolean insertList(Restaurant new_restaurant) {
        return false;
    }

    @Override
    public ArrayList<String> showListContents(int listCategory) {
        return null;
    }

    @Override
    public String getName() {
        return DB_NAME;
    }
}
