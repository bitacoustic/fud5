package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.appdbtest.AppDbTestActivity;
import com.csc413.team5.fud5.dialogs.EulaDialogFragment;
import com.csc413.team5.fud5.tests.ImageTestActivity;
import com.csc413.team5.fud5.tests.LocuMenuTestActivity;
import com.csc413.team5.fud5.tests.MapsTestActivity;
import com.csc413.team5.fud5.tests.RestaurantSelectorTestActivity;
import com.csc413.team5.fud5.tests.RestaurantTestActivity;
import com.csc413.team5.fud5.tests.SharedPreferencesTestActivity;
import com.csc413.team5.fud5.settings.ApplicationSettingsActivity;
import com.csc413.team5.fud5.settings.ApplicationSettingsFragment;
import com.csc413.team5.fud5.settings.FoodPreferencesActivity;
import com.csc413.team5.fud5.settings.ModifyRedListDialogFragment;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;


public class SettingsActivity extends AppCompatActivity
        implements ApplicationSettingsFragment.ApplicationSettingsConfirmListener {
    private static final String TAG = "Settings";
    Context mContext;

    protected CheckBox mCheckBoxUserSettings;
    protected CheckBox mCheckBoxRestaurantHistory;
    protected Button mBtnReset;
    protected Button mBtnLocationServices;
    protected ImageButton mBtnUserSettings;
    protected ImageButton mBtnRestaurantHistory;

    protected ToolTipView mTooltipUserSettings;
    protected ToolTipView mTooltipRestaurantHistory;

    protected boolean isUserSettingsChecked;
    protected boolean isRestaurantHistoryChecked;

    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;
    private dbHelper db;

    public void showAppInfo(View v) {
//        DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
//        eulaDialog.show(getFragmentManager(), "EULA");

        ToastUtil.showShortToast(this, "Showing app info...");
    }

    public void showEULA(View v) {
        DialogFragment eulaDialog = EulaDialogFragment.newInstance();
        eulaDialog.show(getFragmentManager(), "EULA");

        //ToastUtil.showShortToast(this, "Showing EULA...");
    }

    public void resetRedList(View view){
        dbHelper db;
        db = new dbHelper(this, null, null, 1);

        // resets red list
        // TODO: show a confirmation dialog
        db.wipeRestaurantList(3);
        Log.i(TAG, "Red list was cleared");

        ToastUtil.showShortToast(this, getString(R.string
                .activity_user_preferences_toast_removed_all_ignored_restaurants));
    }

    public void modifyRedList(View view) {
        DialogFragment modifyRedListDialog = ModifyRedListDialogFragment.newInstance();
        modifyRedListDialog.show(getFragmentManager(), "red_list");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String title = getString(R.string.title_activity_user_preferences);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>" + title + "</font>"));

        mContext = this;

        mCheckBoxUserSettings = (CheckBox) findViewById(R.id.checkBoxAppSettingsUser);

        userSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        userSettingsEditor = userSettings.edit();

        db = new dbHelper(this, null, null, 1);

        isUserSettingsChecked = false;
        mCheckBoxUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserSettingsChecked = !isUserSettingsChecked;
                Log.i(TAG, "user settings checkbox was " +
                        (isUserSettingsChecked ? "checked" : "unchecked"));
            }
        });

        mCheckBoxRestaurantHistory = (CheckBox) findViewById(R.id.checkBoxAppSettingsRestaurant);
        isRestaurantHistoryChecked = false;
        mCheckBoxRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRestaurantHistoryChecked = !isRestaurantHistoryChecked;
                Log.i(TAG, "restaurant history checkbox was " +
                        (isRestaurantHistoryChecked ? "checked" : "unchecked"));
            }
        });

        mBtnReset = (Button) findViewById(R.id.buttonAppSettingsReset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserSettingsChecked || isRestaurantHistoryChecked) {
                    DialogFragment confirmDialog = ApplicationSettingsFragment
                            .newInstance(isUserSettingsChecked, isRestaurantHistoryChecked);
                    confirmDialog.show(getFragmentManager(), "ConfirmResetSettings");
                } else
                    ToastUtil.showShortToast(mContext,
                            getString(R.string.activity_user_preferences_nothing_was_reset));
            }
        });

        mBtnLocationServices = (Button) findViewById(R.id.buttonManageLocationServices);
        mBtnLocationServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings
                        .ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        mBtnUserSettings = (ImageButton) findViewById(R.id.imageButtonResetUserSettingsInfo);
        mBtnUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "user settings info button was pressed");
                if (mTooltipUserSettings != null && mTooltipUserSettings.isShown()) {
                    mTooltipUserSettings.remove();
                    return;
                }

                ToolTipRelativeLayout tooltipView
                        = (ToolTipRelativeLayout) findViewById(R.id.tooltipAppSettingsUser);
                ToolTip tooltip = new ToolTip()
                        .withText(getString(R.string
                                .activity_application_settings_user_settings_caption))
                        .withColor(Color.parseColor("#ECCD7F"));
                mTooltipUserSettings = tooltipView
                        .showToolTipForView(tooltip, findViewById(R.id.checkBoxAppSettingsUser));
            }
        });

        mBtnRestaurantHistory = (ImageButton) findViewById(R.id
                .imageButtonResetRestaurantHistoryInfo);
        mBtnRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "restaurant history info button was pressed");

                if (mTooltipRestaurantHistory != null && mTooltipRestaurantHistory.isShown()) {
                    mTooltipRestaurantHistory.remove();
                    return;
                }

                ToolTipRelativeLayout tooltipView
                        = (ToolTipRelativeLayout) findViewById(R.id.tooltipAppSettingsRestaurant);
                ToolTip tooltip = new ToolTip()
                        .withText(getString(R.string
                                .activity_application_settings_restaurant_history_caption))
                        .withColor(Color.parseColor("#ECCD7F"));
                mTooltipRestaurantHistory = tooltipView
                        .showToolTipForView(tooltip,
                                findViewById(R.id.checkBoxAppSettingsRestaurant));
            }
        });
    }

    public void resetAppSettings() {
        if (isUserSettingsChecked) {
            userSettingsEditor.clear().commit();

            // TODO: test this
            // Reset preferences to default

            /* DEFAULT SETTINGS
            * Has agreed to EULA: false
            * Min star: 3.5
            * Location search: 1600 Holloway Ave.
            * Radius: 5 mi.
            * Niches: Italian, Carnivore, Hole in the wall, affordable
            * App Language: Relative to device language
            * */

            final boolean hasAgreed = false;
            final float minStar = 3.50f;
            final String searchLocation = "1600 Holloway Ave.";
            final float searchRadius = 1.5f;
//            Set<String> preferredNiches;
//            String appLanguage = Locale.getDefault().getDisplayLanguage();

            userSettingsEditor.putBoolean("hasAgreedToEula", hasAgreed).apply();
            userSettingsEditor.putFloat("defaultMinStar", minStar).apply();
            userSettingsEditor.putString("defaultSearchLocation", searchLocation).apply();
            userSettingsEditor.putFloat("defaultSearchRadius", searchRadius).apply();
//            userSettings.getStringSet("defaultPreferredNiches", getPreferredNiches().apply());
            userSettingsEditor.putString("appLanguage", Locale.getDefault().getDisplayLanguage()).apply();

            Log.i(TAG, "cleared user settings");
            ToastUtil.showShortToast(this,
                    getString(R.string.activity_user_preferences_cleared_user_settings));
            mCheckBoxUserSettings.setChecked(false);
        }

        if (isRestaurantHistoryChecked) {
            db.wipeRestaurantList(Constants.GREEN_LIST);
            Log.i(TAG, "wiped green list (1)");
            db.wipeRestaurantList(Constants.YELLOW_LIST);
            Log.i(TAG, "wiped yellow list (2)");
            db.wipeRestaurantList(Constants.RED_LIST);
            Log.i(TAG, "wiped red list (3)");
            ToastUtil.showShortToast(this,
                    getString(R.string.activity_user_preferences_cleared_restaurant_history));
            mCheckBoxRestaurantHistory.setChecked(false);
        }
    }

    @Override
    public void onApplicationSettingsConfirmPositiveClick(DialogFragment dialog) {
        resetAppSettings();
    }

    @Override
    public void onApplicationSettingsConfirmNegativeClick(DialogFragment dialog) {
        ToastUtil.showShortToast(this,
                getString(R.string.activity_user_preferences_nothing_was_reset));
    }

    // Preferred niches = Search term in the main app
    // TODO: Implement this in the future
    private Set<String> getPreferredNiches() {

        // load preferred niches from resources
        // TODO This array must be able to be modified when Food Preferences is modified
        // ie: when user deletes a niche, update resources
        TypedArray preferred_niche = getResources().obtainTypedArray(R.array.preferred_niche_array);

        Set<String> preferredNicheSet = new TreeSet<String>();

        int preferred_niche_size = getResources().obtainTypedArray(R.array.preferred_niche_array).length();

        for (int i = 0; i < preferred_niche_size; i++) {
            preferredNicheSet.add(preferred_niche.getString(i));
        }

        return preferredNicheSet;
    }



    /* Dev playground */

    public void showFoodSettings(View v) {
        // TODO
        Intent intent = new Intent(this, FoodPreferencesActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Showing Food Settings");
    }

    public void showAppSettings(View v) {
        Intent intent = new Intent(this, ApplicationSettingsActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Showing App Settings...");
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
}
