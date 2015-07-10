package com.csc413.team5.appdbtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;

public class AppDbTestActivity extends AppCompatActivity {

    private dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_db_test);

        db = new dbHelper(this, null, null, 1);
        db.getWritableDatabase();

        TextView dbTitle = (TextView) findViewById(R.id.dbTitle);
        TextView dbPath = (TextView) findViewById(R.id.dbPath);

        dbTitle.append(db.getDbName());
        dbPath.append(db.getDbPath(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_db_test, menu);
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

    public void createRecord(View v) {
        dbColorDialogFragment createDialog = dbColorDialogFragment.newInstance(1);
        createDialog.show(getFragmentManager(), "createDialog");
    }

    public void readRecord(View v) {
        dbColorDialogFragment readDialog = dbColorDialogFragment.newInstance(2);
        readDialog.show(getFragmentManager(), "readDialog");
    }

    public void updateRecord(View v) {
        dbColorDialogFragment updateDialog = dbColorDialogFragment.newInstance(3);
        updateDialog.show(getFragmentManager(), "updateDialog");
    }

    public void deleteRecord(View v) {
        dbColorDialogFragment deleteDialog = dbColorDialogFragment.newInstance(4);
        deleteDialog.show(getFragmentManager(), "deleteDialog");
    }
}
