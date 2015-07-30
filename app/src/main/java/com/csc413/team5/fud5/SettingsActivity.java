package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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
import com.csc413.team5.fud5.dialogs.AppInfoDialog;
import com.csc413.team5.fud5.dialogs.EulaDialogFragment;
import com.csc413.team5.fud5.settings.ApplicationSettingsDialogFragment;
import com.csc413.team5.fud5.settings.FoodPreferencesActivity;
import com.csc413.team5.fud5.settings.ModifyRedListDialogFragment;
import com.csc413.team5.fud5.tests.ImageTestActivity;
import com.csc413.team5.fud5.tests.LocuMenuTestActivity;
import com.csc413.team5.fud5.tests.MapsTestActivity;
import com.csc413.team5.fud5.tests.RestaurantSelectorTestActivity;
import com.csc413.team5.fud5.tests.RestaurantTestActivity;
import com.csc413.team5.fud5.tests.SharedPreferencesTestActivity;
import com.csc413.team5.fud5.utils.AppSettingsHelper;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;


public class SettingsActivity extends AppCompatActivity
        implements ApplicationSettingsDialogFragment.ApplicationSettingsConfirmListener {
    private static final String TAG = "Settings";
    Context mContext;

    protected Button mBtnReset;
    protected Button mBtnLocationServices;

    // check boxes with associated info buttons and tooltips
    protected CheckBox mCheckBoxUserSettings;
    protected ImageButton mBtnUserSettings;
    protected ToolTipView mTooltipUserSettings;

    protected CheckBox mCheckBoxIgnoredRestaurantHistory;
    protected ImageButton mBtnIgnoredRestaurantHistory;
    protected ToolTipView mTooltipIgnoredRestaurantHistory;

    protected CheckBox mCheckBoxAllRestaurantHistory;
    protected ImageButton mBtnAllRestaurantHistory;
    protected ToolTipView mTooltipAllRestaurantHistory;

    // database and shared preferences
    private dbHelper db;

    public void showAppInfo(View v) {
        DialogFragment infoDialog = AppInfoDialog.getInstance();
        infoDialog.show(getFragmentManager(), "App Info");

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

        AppSettingsHelper.init(this);

        db = new dbHelper(this, null, null, 1);

        mCheckBoxUserSettings = (CheckBox) findViewById(R.id.checkBoxAppSettingsUser);
        mCheckBoxUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "user settings checkbox was " +
                        (mCheckBoxUserSettings.isChecked() ? "checked" : "unchecked"));
            }
        });

        mCheckBoxIgnoredRestaurantHistory = (CheckBox) findViewById(R.id.reset_red_list);
        mCheckBoxIgnoredRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBoxIgnoredRestaurantHistory.isChecked()) {
                    if (mCheckBoxAllRestaurantHistory.isChecked())
                        mCheckBoxAllRestaurantHistory.setChecked(false);
                } else {
                    mCheckBoxAllRestaurantHistory.setChecked(false);
                }


                Log.i(TAG, "ignored restaurant history checkbox was " +
                        (mCheckBoxIgnoredRestaurantHistory.isChecked() ? "checked" : "unchecked"));
            }
        });

        mCheckBoxAllRestaurantHistory = (CheckBox) findViewById(R.id.checkBoxAppSettingsRestaurant);
        mCheckBoxAllRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBoxAllRestaurantHistory.isChecked()) {
                    if (mCheckBoxIgnoredRestaurantHistory.isChecked())
                        mCheckBoxIgnoredRestaurantHistory.setChecked(false);
                } else {
                    mCheckBoxIgnoredRestaurantHistory.setChecked(false);
                }

                Log.i(TAG, "all restaurant history checkbox was " +
                        (mCheckBoxAllRestaurantHistory.isChecked() ? "checked" : "unchecked"));
            }
        });

        mBtnReset = (Button) findViewById(R.id.buttonAppSettingsReset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show confirmation dialog if any of the checkboxes are checked
                if (mCheckBoxUserSettings.isChecked()
                        || mCheckBoxIgnoredRestaurantHistory.isChecked()
                        || mCheckBoxAllRestaurantHistory.isChecked()) {
                    DialogFragment confirmDialog = ApplicationSettingsDialogFragment
                            .newInstance(mCheckBoxUserSettings.isChecked(),
                                    mCheckBoxIgnoredRestaurantHistory.isChecked(),
                                    mCheckBoxAllRestaurantHistory.isChecked());
                    confirmDialog.show(getFragmentManager(), "ConfirmResetSettings");
                } else // otherwise, confirm nothing was changed
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
            // toggle tooltip
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

        mBtnIgnoredRestaurantHistory = (ImageButton) findViewById(R.id
                .imageButtonResetIgnoredRestaurantHistoryInfo);
        mBtnIgnoredRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            // toggle tooltip
            @Override
            public void onClick(View v) {
                Log.i(TAG, "ignored restaurant history info button was pressed");

                if (mTooltipIgnoredRestaurantHistory != null
                        && mTooltipIgnoredRestaurantHistory.isShown()) {
                    mTooltipIgnoredRestaurantHistory.remove();
                    return;
                }

                ToolTipRelativeLayout tooltipView
                        = (ToolTipRelativeLayout) findViewById(R.id.
                        tooltipAppSettingsIgnoredRestaurants);
                ToolTip tooltip = new ToolTip()
                        .withText(getString(R.string
                                .activity_application_settings_ignored_restaurant_history_caption))
                        .withColor(Color.parseColor("#ECCD7F"));
                mTooltipIgnoredRestaurantHistory = tooltipView
                        .showToolTipForView(tooltip,
                                findViewById(R.id.reset_red_list));
            }
        });

        mBtnAllRestaurantHistory= (ImageButton) findViewById(R.id
                .imageButtonResetRestaurantHistoryInfo);
        mBtnAllRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            // toggle tooltip
            @Override
            public void onClick(View v) {
                Log.i(TAG, "restaurant history info button was pressed");

                if (mTooltipAllRestaurantHistory != null && mTooltipAllRestaurantHistory.isShown()) {
                    mTooltipAllRestaurantHistory.remove();
                    return;
                }

                ToolTipRelativeLayout tooltipView
                        = (ToolTipRelativeLayout) findViewById(R.id.tooltipAppSettingsRestaurant);
                ToolTip tooltip = new ToolTip()
                        .withText(getString(R.string
                                .activity_application_settings_restaurant_history_caption))
                        .withColor(Color.parseColor("#ECCD7F"));
                mTooltipAllRestaurantHistory = tooltipView
                        .showToolTipForView(tooltip,
                                findViewById(R.id.checkBoxAppSettingsRestaurant));
            }
        });
    }

    public void resetAppSettings() {
        if (mCheckBoxUserSettings.isChecked()) {
            AppSettingsHelper.clear();

            // Reset preferences to default
            /* DEFAULT SETTINGS
            * Min star: 3.5
            * Radius: 5 mi.
            * Search term: Tacos
            * */

            final float minStar = 3.50f;
            final float searchRadius = 1.0f;
            final String searchTerm = "Tacos";

            // default search term
            AppSettingsHelper.setDefaultSearchTermInput(searchTerm);
            // search radius
            AppSettingsHelper.setDefaultRadiusValue(searchRadius);
            // star rating
            AppSettingsHelper.setDefaultStarRating(minStar);
            //

            Log.i(TAG, "cleared user settings");
            ToastUtil.showShortToast(this,
                    getString(R.string.activity_user_preferences_cleared_user_settings));
            mCheckBoxUserSettings.setChecked(false);
        }

        if (mCheckBoxIgnoredRestaurantHistory.isChecked()) {
            db.wipeRestaurantList(Constants.RED_LIST);
            Log.i(TAG, "wiped red list (3)");
            ToastUtil.showShortToast(this, getString(R.string
                    .activity_user_preferences_cleared_ignored_restaurants));
            mCheckBoxIgnoredRestaurantHistory.setChecked(false);
        }

        if (mCheckBoxAllRestaurantHistory.isChecked()) {
            db.wipeRestaurantList(Constants.GREEN_LIST);
            Log.i(TAG, "wiped green list (1)");
            db.wipeRestaurantList(Constants.YELLOW_LIST);
            Log.i(TAG, "wiped yelw list (2)");
            db.wipeRestaurantList(Constants.RED_LIST);
            Log.i(TAG, "wiped red list (3)");
            ToastUtil.showShortToast(this,
                    getString(R.string.activity_user_preferences_cleared_restaurant_history));
            mCheckBoxAllRestaurantHistory.setChecked(false);
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

    /* Dev playground */

    public void showFoodSettings(View v) {
        // TODO
        Intent intent = new Intent(this, FoodPreferencesActivity.class);
        startActivity(intent);

        ToastUtil.showShortToast(this, "Showing Food Settings");
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
