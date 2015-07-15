package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.csc413.team5.appdbtest.AppDbTestActivity;
import com.csc413.team5.fud5.userpreferences.ModifyRedListDialogFragment;
import com.csc413.team5.restauranttest.RestaurantTestActivity;


public class UserPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
    }

    public void showFoodSettings(View v) {
//        DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
//        eulaDialog.show(getFragmentManager(), "EULA");

        CharSequence text = "Showing Food Settings";

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showAppSettings(View v) {
//        DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
//        eulaDialog.show(getFragmentManager(), "EULA");

        CharSequence text = "Showing App Settings...";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void showEULA(View v) {
        DialogFragment eulaDialog = EulaDialogFragment.newInstance(true);
        eulaDialog.show(getFragmentManager(), "EULA");

        CharSequence text = "Showing EULA...";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void showAppInfo(View v) {
//        DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
//        eulaDialog.show(getFragmentManager(), "EULA");

        CharSequence text = "Showing app info...";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void showDbTest(View view){
        Intent intent = new Intent(this, AppDbTestActivity.class);
        startActivity(intent);

        CharSequence text = "Testing database...";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void showYelpTest(View view){
        Intent intent = new Intent(this, RestaurantTestActivity.class);
        startActivity(intent);

        CharSequence text = "Testing Yelp integration...";

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void showLocuTest(View view){
        Intent intent = new Intent(this, LocuMenuTestActivity.class);
        startActivity(intent);

        CharSequence text = "Testing Locu integration...";

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void showMapTest(View view){
//        Intent intent = new Intent(this, UserPreferencesActivity.class);
//        startActivity(intent);

        CharSequence text = "Testing Google Maps integration...";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        toast.show();
    }

    public void resetRedList(View view){
//        Intent intent = new Intent(this, UserPreferencesActivity.class);
//        startActivity(intent);

        CharSequence text = "Successfully reset red list.";

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void modifyRedList(View view) {
        DialogFragment modifyRedListDialog = ModifyRedListDialogFragment.newInstance();
        modifyRedListDialog.show(getFragmentManager(), "red_list");

        CharSequence text = "Modifying red list...";

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
