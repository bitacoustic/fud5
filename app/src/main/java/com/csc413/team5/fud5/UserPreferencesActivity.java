package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.appdbtest.AppDbTestActivity;
import com.csc413.team5.fud5.tests.ImageTestActivity;
import com.csc413.team5.fud5.tests.LocuMenuTestActivity;
import com.csc413.team5.fud5.tests.MapsTestActivity;
import com.csc413.team5.fud5.tests.RestaurantSelectorTestActivity;
import com.csc413.team5.fud5.tests.RestaurantTestActivity;
import com.csc413.team5.fud5.tests.SharedPreferencesTestActivity;
import com.csc413.team5.fud5.userpreferences.ApplicationSettingsActivity;
import com.csc413.team5.fud5.userpreferences.ModifyRedListDialogFragment;
import com.csc413.team5.fud5.utils.ToastUtil;


public class UserPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
    }

    public void showFoodSettings(View v) {
        // TODO
//        DialogFragment eulaDialog = EulaDialogFragment.newInstance();
//        eulaDialog.show(getFragmentManager(), "EULA");

        ToastUtil.showShortToast(this, "Showing Food Settings");
    }

    public void showAppSettings(View v) {
        Intent intent = new Intent(this, ApplicationSettingsActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Showing App Settings...");
    }

    public void showEULA(View v) {
        DialogFragment eulaDialog = EulaDialogFragment.newInstance();
        eulaDialog.show(getFragmentManager(), "EULA");

        ToastUtil.showShortToast(this, "Showing EULA...");
    }

    public void showAppInfo(View v) {
//        DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
//        eulaDialog.show(getFragmentManager(), "EULA");

        ToastUtil.showShortToast(this, "Showing app info...");
    }

    public void showDbTest(View view){
        Intent intent = new Intent(this, AppDbTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing database...");
    }

    public void showYelpTest(View view){
        Intent intent = new Intent(this, RestaurantTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing Yelp integration...");
    }

    public void showImageTest(View view){
        Intent intent = new Intent(this, ImageTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing Images...");
    }

    public void showLocuTest(View view){
        Intent intent = new Intent(this, LocuMenuTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing Locu integration...");
    }

    public void showMapTest(View view){
        Intent intent = new Intent(this, MapsTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing Google Maps integration...");
    }

    public void showSelectorTest(View view) {
        Intent intent = new Intent(this, RestaurantSelectorTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing selector...");
    }

    public void showSharedPreferencesTest(View view) {
        Intent intent = new Intent(this, SharedPreferencesTestActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Testing shared preferences...");
    }

    public void resetRedList(View view){
//        Intent intent = new Intent(this, UserPreferencesActivity.class);
//        startActivity(intent);

        dbHelper db;
        db = new dbHelper(this, null, null, 1);

        // resets red list
        db.wipeRestaurantList(3);

        ToastUtil.showShortToast(this, "Successfully reset red list.");
    }

    public void modifyRedList(View view) {
        DialogFragment modifyRedListDialog = ModifyRedListDialogFragment.newInstance();
        modifyRedListDialog.show(getFragmentManager(), "red_list");

        ToastUtil.showShortToast(this, "Modifying red list...");
    }
}
