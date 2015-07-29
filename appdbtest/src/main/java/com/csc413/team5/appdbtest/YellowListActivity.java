package com.csc413.team5.appdbtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;


public class YellowListActivity extends AppCompatActivity {

    dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yellow_list);

        /* display DB Information */
        db = new dbHelper(this, null, null, 1);
        TextView dbTitle = (TextView) findViewById(R.id.dbTitle);
        TextView dbPath = (TextView) findViewById(R.id.dbPath);
        TextView dbTable = (TextView) findViewById(R.id.dbTableTitle);

        dbTitle.append(db.getDbName());
        dbPath.append(db.getDbPath(this));
        dbTable.append(db.getDbTableName(2));

        /* display DB contents */
        TextView restaurantIDEntries = (TextView) findViewById(R.id.yellow_restaurant_id_text);
        TextView restaurantTimeStampEntries = (TextView) findViewById(R.id.yellow_restaurant_timestamp_text);

        if (db.isTableExist(2) && !db.isTableEmpty(2)) { // if db exists and is not empty
            for (int i = 0; i < db.getRestaurantNamesFromList(2).size(); i++) {
                restaurantIDEntries.append("\n" + db.getRestaurantNamesFromList(2).get(i));
                restaurantTimeStampEntries.append("\n" + db.getRestaurantTimeStampsFromList(2).get(i));
            }
        }

        restaurantIDEntries.append("\n" + "---TABLE INFO---");
        restaurantIDEntries.append("\n" + "Exists: " + db.isTableExist(2));
        restaurantIDEntries.append("\n" + "Empty: " + db.isTableEmpty(2));
    }
}
