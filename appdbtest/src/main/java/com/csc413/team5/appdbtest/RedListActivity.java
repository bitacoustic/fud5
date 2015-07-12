package com.csc413.team5.appdbtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;


public class RedListActivity extends AppCompatActivity {

    dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_list);

        /* display DB Information */
        db = new dbHelper(this, null, null, 1);
        TextView dbTitle = (TextView) findViewById(R.id.dbTitle);
        TextView dbPath = (TextView) findViewById(R.id.dbPath);
        TextView dbTable = (TextView) findViewById(R.id.dbTableTitle);

        dbTitle.append(db.getDbName());
        dbPath.append(db.getDbPath(this));
        dbTable.append(db.getDbTableName(3));

        /* display DB contents */
        TextView restaurantIDEntries = (TextView) findViewById(R.id.red_restaurant_id_text);


        if (db.isTableExist(3) && !db.isTableEmpty(3)) { // if db exists and is not empty
            for (int i = 0; i < db.getRestaurantNamesFromList(3).size(); i++) {
                restaurantIDEntries.append("\n" + db.getRestaurantNamesFromList(3).get(i));
            }
        }
        restaurantIDEntries.append("\n" + "---TABLE INFO---");
        restaurantIDEntries.append("\n" + "Exists: " + db.isTableExist(3));
        restaurantIDEntries.append("\n" + "Empty: " + db.isTableEmpty(3));
    }
}
