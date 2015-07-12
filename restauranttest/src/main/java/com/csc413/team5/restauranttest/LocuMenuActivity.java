package com.csc413.team5.restauranttest;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csc413.team5.restaurantapiwrapper.LocuApiKey;
import com.csc413.team5.restaurantapiwrapper.LocuExtension;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

public class LocuMenuActivity extends Activity
        implements MenuNotFoundFragment.MenuNotFoundDialogListener {
    DisplayMenuTask displayMenu;
    LocuApiKey locuKey;
    Restaurant r;
    TextView txtDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locu_menu);

        txtDebug = (TextView) findViewById(R.id.textViewDebug);

        // create a LocuApiKey to pass to the LocuExtension
        locuKey = new LocuApiKey(getApplicationContext().getResources()
                .getString(R.string.locu_key));

        // define what happens on button click: display menus for the respective restaurant

        Button btnNewTsingTao = (Button) findViewById(R.id.buttonNewTsingTao);
        btnNewTsingTao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DisplayMenuTask().execute("64e0307ca073dccb5666");
            }
        });

        Button btnChevys = (Button) findViewById(R.id.buttonChevys);
        btnChevys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DisplayMenuTask().execute("c44a1b13d0ae6261985a");
            }
        });

        Button btnNetties = (Button) findViewById(R.id.buttonNetties);
        btnNetties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DisplayMenuTask().execute("47339afd5e4f01cbe13e");
            }
        });

        Button btnCheesecake = (Button) findViewById(R.id.buttonCheesecake);
        btnCheesecake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DisplayMenuTask().execute("255493eca6ae926b2b5c");
            }
        });

        // display a menu for a restaurant with the specified Locu ID

        final EditText editTextLocuSearchId = (EditText) findViewById(R.id.editTextLocuSearchId);

        Button btnDisplayMenu = (Button) findViewById(R.id.buttonDisplayMenu);
        btnDisplayMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchId = editTextLocuSearchId.getText().toString().trim().toLowerCase();

                if (searchId.length() == 20) {
                    new DisplayMenuTask().execute(searchId);
                }

                else
                    txtDebug.setText("Locu ID must be 20 characters. " + searchId.length()
                        + " characters were entered.");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locu_menu, menu);
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

    private class DisplayMenuTask extends AsyncTask<String, String, Restaurant> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Restaurant doInBackground(String... params) {
            r = new Restaurant(); // contains only default parameters
            LocuExtension locu = new LocuExtension(locuKey);
            locu.updateFromMatchedLocuId(r, params[0]);

            return r;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param restaurant The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Restaurant restaurant) {
            // show the corresponding dialog for displaying the menu or telling user that
            // a menu was not found

            if (r.hasLocuMenus()) {
                txtDebug.setText("Loading menu...");
                // create an instance of DisplayRestaurantMenusFragment with the restaurant
                // result of doInBackground() as an argument
                DialogFragment displayRestaurantMenus = DisplayRestaurantMenusFragment
                        .newInstance(restaurant);
                displayRestaurantMenus.show(getFragmentManager(), "menus");
            }
            else {
                txtDebug.setText("No menus are available for the specified ID.");
                DialogFragment menuNotFoundDialog = new MenuNotFoundFragment();
                menuNotFoundDialog.show(getFragmentManager(), "menuNotFound");
            }
        }


    }

    // MenuNotFoundFragment dialog

    @Override
    public void onMenuNotFoundPositiveClick(DialogFragment dialog) {
        txtDebug.setText("");
    }
}


/*
New Tsing Tao Restaurant 64e0307ca073dccb5666
Chevy's Fresh Mex c44a1b13d0ae6261985a
Nettie's Crab Shack 47339afd5e4f01cbe13e
 */