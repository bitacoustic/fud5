package com.example.healthinspectionwrapper;

import java.io.File;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Wrapper for health record database.
 */
public class HealthRecordClient extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "healthRecords.db";
    private static final int DATABASE_VERSION = 1;
    public HealthRecordClient(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    public void onCreate(SQLiteDatabase healthRecords){

        healthRecords.execSQL(
                "CREATE TABLE healthrecord (business_id TEXT, name TEXT, address TEXT, " +
                        "latitude REAL, longitude REAL, score REAL)"
        );
    };
    //TODO: implement
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {};
}
