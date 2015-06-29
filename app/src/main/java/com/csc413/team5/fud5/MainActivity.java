package com.csc413.team5.fud5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

public class MainActivity extends AppCompatActivity {

    dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new dbHelper(this, null, null, 1);
        TextView dbTitle = (TextView) findViewById(R.id.dbTitle);
        TextView dbColumns = (TextView) findViewById(R.id.dbColumns);

        dbTitle.setText(db.getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void insertGreenRestaurant(View v) {
        Button b = (Button) v;
        Restaurant r = new Restaurant();
        r.setRestaurantName(b.getTag().toString());
        db.insertRestaurantToList(r,1);
    }

    public void insertRedRestaurant(View v) {
        Button b = (Button) v;
        Restaurant r = new Restaurant();
        r.setRestaurantName(b.getTag().toString());
        db.insertRestaurantToList(r,3);
    }

    public void showBothList(View v) {
        TextView t1 = (TextView) findViewById(R.id.green_list);
        TextView t2 = (TextView) findViewById(R.id.red_list);

        t1.setText("Hello");
        t2.setText("World");
    }
}
