package com.csc413.team5.fud5;

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


        if (!db.isTableEmpty(3)) {  // if db is not empty
            for (int i = 0; i < db.getRestaurantNamesFromList(1).size(); i++) {
                restaurantIDEntries.append("\n" + db.getRestaurantNamesFromList(3).get(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_red_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
