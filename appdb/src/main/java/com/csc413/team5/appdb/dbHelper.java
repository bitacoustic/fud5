package com.csc413.team5.appdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class dbHelper extends SQLiteOpenHelper implements dbHelperInterface {
    // Database info
    private static final String TAG = "DATABASE";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="fudfive.db";

    // Table info
    private static final String GREEN_RESTAURANTS_TABLE_NAME = "greenRestaurants";
    private static final String YELLOW_RESTAURANTS_TABLE_NAME = "yellowRestaurants";
    private static final String RED_RESTAURANTS_TABLE_NAME = "redRestaurants";

    // Column info
    private static final String RESTAURANT_ID_COLUMN = "RESTAURANT_ID";
    private static final String TIMESTAMP_COLUMN = "DATE_ADDED";


    // SQL Syntax Helper variables
    private static final String TEXTTYPE = " TEXT";
    private static final String DATETYPE = " DATETIME";
    private static final String COMMA_SEP = ", ";
    private static final String DEFAULT_FLAG = " DEFAULT";

    // Creates an instance of SQLiteDatabase
    public dbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // Generates tables
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
    }

    // DANGER ZONE, don't call this method yet. Not sure what does.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GREEN_RESTAURANTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + YELLOW_RESTAURANTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RED_RESTAURANTS_TABLE_NAME);
        onCreate(db);
    }

    // Main insertion method
    @Override
    public void rawInsertRestaurantToList(Restaurant my_restaurant, int listClass) {
        if (listClass == 1) { // inserting to yellow list
            if (!isRestaurantOnlyInList(my_restaurant, 1)) { // record not in green list
                if (isRestaurantOnlyInList(my_restaurant, 2)) { // record found, but in yellow
                    migrateRestaurantListItem(my_restaurant,2,1);  // migrate it from yellow to green
                    Log.i("List migration", "Yellow -> Green");
                } else if (isRestaurantOnlyInList(my_restaurant, 3)) { // record found, but in red
                    migrateRestaurantListItem(my_restaurant, 3, 1);    // migrate it from red to green
                    Log.i("List migration", "Red -> Green");
                } else {    // record not found anywhere, create new record in green list
                    // TODO: HANDLE DUPLICATES
                    insertRestaurantToList(my_restaurant, 1);  // insert restaurant to green list
                    Log.i("GREEN LIST", "Added new record");
                }
            } // end if
            else {    // record is already in green list
                deleteRestaurantFromList(my_restaurant, 1); // delete previous record
                insertRestaurantToList(my_restaurant, 1); // insert restaurant to green list
                Log.i("GREEN LIST", "Updated record");
            } // end else
        } /* end else inserting to green list */

        else if (listClass == 2) { // inserting to yellow list
            if (!isRestaurantOnlyInList(my_restaurant, 2)) { // record not in yellow list
                if (isRestaurantOnlyInList(my_restaurant, 1)) { // record found, but in green
                    migrateRestaurantListItem(my_restaurant,1,2);  // migrate it from green to yellow
                    Log.i("List migration", "Green -> Yellow");
                } else if (isRestaurantOnlyInList(my_restaurant, 3)) { // record found, but in red
                    migrateRestaurantListItem(my_restaurant, 3, 2);    // migrate it from red to yellow
                    Log.i("List migration", "Red -> Green");
                } else {    // record not found anywhere, create new record in yellow list
                    // TODO: HANDLE DUPLICATES
                    insertRestaurantToList(my_restaurant, 2);  // insert restaurant to yellow list
                    Log.i("YELLOW LIST", "Added new record");
                }
            } // end if
            else {    // record is already in yellow list
                deleteRestaurantFromList(my_restaurant, 2); // delete previous record
                insertRestaurantToList(my_restaurant, 2); // insert restaurant to yellow list
                Log.i("YELLOW LIST", "Updated record");
            } // end else
        } /* end else inserting to yellow list*/

        else { // inserting to red list
            // if record not in red list
            if (!isRestaurantOnlyInList(my_restaurant, 3)) {
                if (isRestaurantOnlyInList(my_restaurant, 1)) { // record found, but in green
                    migrateRestaurantListItem(my_restaurant,1,3);  // migrate it from green to red
                    Log.i("List migration", "Green -> Red");
                } else if (isRestaurantOnlyInList(my_restaurant, 2)) { // record found, but in yellow
                    migrateRestaurantListItem(my_restaurant, 2, 3);    // migrate it from yellow to red
                    Log.i("List migration", "Yellow -> Red");
                } else {    // record not found anywhere, create new record in red list
                    // TODO: HANDLE DUPLICATES
                    insertRestaurantToList(my_restaurant, 3);
                    Log.i("RED LIST", "Added new record"); // insert restaurant to red list
                }
            } // end if
            else {    // else record is already in red list
                deleteRestaurantFromList(my_restaurant, 3); // delete previous record
                insertRestaurantToList(my_restaurant, 3); // insert restaurant to red list
                Log.i("RED LIST", "Updated record");
            } // end else
        } /* end else inserting to red list*/
    }

    // private insertion helper method
    // used by both rawInsertRestaurantToList and migrateRestaurantListItem methods
    public boolean insertRestaurantToList(Restaurant new_restaurant, int listClass) {
        // TODO: Find a way to throw an error if there's an error in insertion
        SQLiteDatabase db = getWritableDatabase();

        if (listClass == 1) {
            db.execSQL("INSERT INTO " + GREEN_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + ") " + "VALUES(" +
                    "\"" + new_restaurant.getBusinessName() + "\"" + ");");
            db.close();
            return true;
        }

        else if (listClass == 2) {
            db.execSQL("INSERT INTO " + YELLOW_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + ") " + "VALUES(" +
                    "\"" + new_restaurant.getBusinessName() + "\"" + ");");
            Log.i("YELLOW LIST:", "Added new record");
            db.close();
            return true;
        }
        else if (listClass == 3){
            db.execSQL("INSERT INTO " + RED_RESTAURANTS_TABLE_NAME + "(" +
                    RESTAURANT_ID_COLUMN + ") " + "VALUES(" +
                    "\"" + new_restaurant.getBusinessName() + "\"" + ");");
            Log.i("RED LIST:", new_restaurant.getBusinessName());
            db.close();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getRestaurantNamesFromList(int listClass) {
        SQLiteDatabase db = getWritableDatabase();
        List<String> restaurantNames = new ArrayList<>();

        String query;

        if (listClass == 1) {   // green
            query = "SELECT * FROM " + GREEN_RESTAURANTS_TABLE_NAME;
        } else if (listClass == 2) {    // yellow
            query = "SELECT * FROM " + YELLOW_RESTAURANTS_TABLE_NAME;
        } else if (listClass == 3){ // red
            query = "SELECT * FROM " + RED_RESTAURANTS_TABLE_NAME;
        } else {
            Log.d("Error", "listClassificationError");
            return null;
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                restaurantNames.add(cursor.getString(0));   // get first column value
            } while (cursor.moveToNext());  // repeat until no more value
        }

        // TODO: Catch when there is no item in a list

        cursor.close();
        db.close();
        return restaurantNames;
    }

    // Helper class that uses delete and insert methods to migrate records
    @Override
    public boolean migrateRestaurantListItem(Restaurant my_restaurant, int fromList, int toList) {
        // green to yellow
        if (fromList == 1 && toList == 2) {
            deleteRestaurantFromList(my_restaurant, 1); // delete from green
            insertRestaurantToList(my_restaurant, 2); // insert to yellow
            return true;
        }

        // green to red
        else if (fromList == 1 && toList == 3) {
            deleteRestaurantFromList(my_restaurant, 1); // delete from green
            insertRestaurantToList(my_restaurant, 3);    // insert to red
            return true;
        }

        // yellow to green
        else if (fromList == 2 && toList == 1) {
            deleteRestaurantFromList(my_restaurant, 2); // delete from yellow
            insertRestaurantToList(my_restaurant, 1); // insert to green
            return true;
        }

        // yellow to red
        else if (fromList == 2 && toList == 3) {
            deleteRestaurantFromList(my_restaurant, 2); // delete from yellow
            insertRestaurantToList(my_restaurant, 3);    // insert to green
            return true;
        }

        // red to green
        else if (fromList == 3 && toList == 1) {
            deleteRestaurantFromList(my_restaurant, 3); // delete from red
            insertRestaurantToList(my_restaurant, 1);    // insert to green
            return true;
        }
        // red to yellow
        else if (fromList == 3 && toList == 2) {
            deleteRestaurantFromList(my_restaurant, 3); // delete from red
            insertRestaurantToList(my_restaurant, 2); // insert to yellow;
            return true;
        }
        else {
        // Wrong from and to bounds
            Log.d("Error:", "listOutOfBoundsError");
            return false;
        }
    }

    @Override
    public boolean deleteRestaurantFromList(Restaurant my_restaurant, int listClass) {
        SQLiteDatabase db = getWritableDatabase();

        boolean isDeleteSuccess;

        if (listClass == 1) {
            isDeleteSuccess = (db.delete(GREEN_RESTAURANTS_TABLE_NAME, RESTAURANT_ID_COLUMN
                    + "= \"" + my_restaurant.getBusinessName()+ "\"", null) > 0);
        } else if (listClass == 2) {
            isDeleteSuccess = (db.delete(YELLOW_RESTAURANTS_TABLE_NAME, RESTAURANT_ID_COLUMN
                    + "= \"" + my_restaurant.getBusinessName()+ "\"", null) > 0);
        } else if (listClass == 3) {
            isDeleteSuccess = (db.delete(RED_RESTAURANTS_TABLE_NAME, RESTAURANT_ID_COLUMN
                    + "= \"" + my_restaurant.getBusinessName()+ "\"", null) > 0);
        } else {
            isDeleteSuccess = false;
            Log.i("ERROR", "listClassificationError");
        }

        db.close();
        return isDeleteSuccess;
    }

    @Override
    public boolean wipeRestaurantList(Restaurant my_restaurant, int listClass) {
        SQLiteDatabase db = getWritableDatabase();
        if (listClass == 1) {
            db.execSQL("DELETE * FROM " + GREEN_RESTAURANTS_TABLE_NAME + ";");
            db.close();
            return true;
        } else if (listClass == 2) {
            db.execSQL("DELETE * FROM " + YELLOW_RESTAURANTS_TABLE_NAME + ";");
            db.close();
            return true;
        } else if (listClass == 3) {
            db.execSQL("DELETE * FROM " + RED_RESTAURANTS_TABLE_NAME + ";");
            db.close();
            return true;
        } else {
            // Wrong listClass
            Log.d("Error:", "listOutOfBoundsError");
            db.close();
            return false;
        }
    }

    @Override
    public boolean isRestaurantInList(Restaurant my_restaurant, int listClass) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "";

        if (listClass == 1) {
            query = "SELECT EXISTS(SELECT 1 FROM " + GREEN_RESTAURANTS_TABLE_NAME +
                    " WHERE " + RESTAURANT_ID_COLUMN + "=\"" + my_restaurant.getBusinessName() +
                    "\");";
        } else if (listClass == 2) {
            query = "SELECT EXISTS(SELECT 1 FROM " + YELLOW_RESTAURANTS_TABLE_NAME +
                    " WHERE " + RESTAURANT_ID_COLUMN + "=\"" + my_restaurant.getBusinessName() +
                    "\");";
        } else if (listClass == 3) {
            query = "SELECT EXISTS(SELECT 1 FROM " + RED_RESTAURANTS_TABLE_NAME +
                    " WHERE " + RESTAURANT_ID_COLUMN + "=\"" + my_restaurant.getBusinessName() +
                    "\");";
        } else {
            Log.d("Error", "listClassificationError");
        }

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        if (cursor.getInt(0) > 0) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    @Override
    public boolean isRestaurantOnlyInList(Restaurant my_restaurant, int listClass) {
        boolean onlyExists;

        // TODO: HANDLE DUPLICATES

        if (listClass == 1) {
            if (isRestaurantInList(my_restaurant, 1) &&
                    !isRestaurantInList(my_restaurant, 2) &&
                    !isRestaurantInList(my_restaurant, 3)) {
                // green, but not yellow and not red
                onlyExists = true;
            } else {
                onlyExists = false;
                // a record must be in two or more places
                Log.d("Error", "listCollisionError");
                // a record does not exist
                Log.d("Error", "recordNotFoundError");
            }
        } else if (listClass == 2) {
            if (!isRestaurantInList(my_restaurant, 1) &&
                    isRestaurantInList(my_restaurant, 2) &&
                    isRestaurantInList(my_restaurant, 3)) {
                // yellow, but not green and not red
                onlyExists = true;
            } else {
                onlyExists = false;
                // a record must be in two or more places
                Log.d("Error", "listCollisionError");
                // a record does not exist
                Log.d("Error", "recordNotFoundError");
            }
        } else if (listClass == 3) {
            if (!isRestaurantInList(my_restaurant, 1) &&
                    !isRestaurantInList(my_restaurant, 2) &&
                    isRestaurantInList(my_restaurant, 3)) {
                // green, but not yellow and not red
                onlyExists = true;
            } else {
                onlyExists = false;
                // a record must be in two or more places
                Log.d("Error", "listCollisionError");
                // a record does not exist
                Log.d("Error", "recordNotFoundError");
            }
        } else {
            onlyExists = false;
            Log.i("ERROR", "listClassificationError");
        }
        return onlyExists;
    }

    @Override
    public int getRestaurantListColor(Restaurant my_restaurant) {
        int whichList;
        if (isRestaurantOnlyInList(my_restaurant, 1)) {
            // green
            whichList = 1;
        } else if (isRestaurantOnlyInList(my_restaurant, 2)) {
            // yellow
            whichList = 2;
        } else if (isRestaurantOnlyInList(my_restaurant, 3)) {
            // red
            whichList = 3;
        } else {
            whichList = -1;
            // a record must be in two or more places
            Log.d("Error", "listCollisionError");
            // a record does not exist
            Log.d("Error", "recordNotFoundError");
        }
        return whichList;
    }

    @Override
    public boolean isDbExist(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    @Override
    public boolean isTableExist(int listClass) {
        String query = "";
        SQLiteDatabase db = getWritableDatabase();

        if (listClass == 1) {   // green
            query = "SELECT 1 FROM sqlite_master WHERE type=\'table\' AND name =\'" +
                    GREEN_RESTAURANTS_TABLE_NAME+"\'";
        } else if (listClass == 2) {    // yellow
            query = "SELECT 1 FROM sqlite_master WHERE type=\'table\' AND name =\'" +
                    YELLOW_RESTAURANTS_TABLE_NAME+"\'";
        } else if (listClass == 3){ // red
            query = "SELECT 1 FROM sqlite_master WHERE type=\'table\' AND name =\'" +
                    RED_RESTAURANTS_TABLE_NAME+"\'";
        } else {
            Log.d("Error", "listClassificationError");
        }

        Cursor cursor = db.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            return false;
        }

        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count > 0;   // exists if > 0
    }

    @Override
    public boolean isTableEmpty(int listClass) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "";

        if (listClass == 1) {   // green
            query = "SELECT COUNT(*) FROM " + GREEN_RESTAURANTS_TABLE_NAME;
        } else if (listClass == 2) {    // yellow
            query = "SELECT COUNT(*) FROM " + YELLOW_RESTAURANTS_TABLE_NAME;
        } else if (listClass == 3){ // red
            query = "SELECT COUNT(*) FROM " + RED_RESTAURANTS_TABLE_NAME;
        } else {
            Log.d("Error", "listClassificationError");
        }

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count == 0;   // not empty if != 0
    }

    @Override
    public String getDbName() {
        return DATABASE_NAME;
    }

    @Override
    public String getDbTableName(int listClass) {
        if (listClass == 1) {
            return GREEN_RESTAURANTS_TABLE_NAME;
        } else if (listClass == 2) {
            return YELLOW_RESTAURANTS_TABLE_NAME;
        } else {
            return RED_RESTAURANTS_TABLE_NAME;
        }
    }

    @Override
    public String getDbPath(Context context) {
        return context.getDatabasePath(DATABASE_NAME).toString();
    }
}
