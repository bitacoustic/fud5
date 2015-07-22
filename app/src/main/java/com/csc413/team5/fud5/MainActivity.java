package com.csc413.team5.fud5;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.EditText;

import android.util.Log;


public class MainActivity extends AppCompatActivity {


    public void btnFuDPlz(View v){

        String location = ((EditText) findViewById(R.id.txtLocation)).getText().toString();
        String searchTerm = ((EditText) findViewById(R.id.txtSearchTerm)).getText().toString();
        Log.i("ResultPageActivity", "location " + location);
        Log.i("ResultPageActivity", "searchTerms " + searchTerm);
        Intent intent = new Intent(this, ResultPageActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("searchTerm", searchTerm);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Don't show keyboard immediately
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_main);

        // TODO: Migrate this to xml file instead
        //Spinner code from Android example
        Spinner spinner = (Spinner) findViewById(R.id.spnRadius);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
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
            showUserPreferencesMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showUserPreferencesMenu(){
        Intent intent = new Intent(this, UserPreferencesActivity.class);
        startActivity(intent);
    }

    //Closes keyboard if user clicks off of it
    //Code thanks to Lalit Poptani on stackoverflow.com
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
