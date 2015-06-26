package com.csc413.team5.fud5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity {

    dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new dbHelper(this, null, null, 1);
        TextView dbTitle = (TextView) findViewById(R.id.dbTitle);

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

    public void insertRestaurant(View v) {
        TextView t = (TextView) findViewById(R.id.green_list_text);
        Button b = (Button) v;
        Restaurant r = new Restaurant();
        r.setRestaurantName((String) b.getText());

        db.insertRestaurantToList(r,1);

        t.append("\n" + r.getBusinessName());
    }

    public void showGreenList(View v) {
        TextView t = (TextView) findViewById(R.id.green_list_text);

        t.setText(db.showContents(1));
    }
}
