package com.csc413.team5.fud5.userpreferences;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ToastUtil;

import java.util.Locale;
import java.util.Set;

/**
 * Resets user settings (clears the SharedPreferences file "UserSettings") and/or restaurant
 * history (wipes the green, yellow, and red lists).
 * <p/>
 * Created on 7/16/2015.
 *
 * @author Eric C. Black
 */
public class WipeDataDialogFragment extends DialogFragment {
    private static final String TAG = "AppSettingsDialog";

    protected CheckBox mCheckBoxUserSettings;
    protected CheckBox mCheckBoxRestaurantHistory;
    protected Button mBtnReset;

    protected boolean isUserSettingsChecked;
    protected boolean isRestaurantHistoryChecked;

    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;
    private dbHelper db;

    public static WipeDataDialogFragment newInstance() {
        return new WipeDataDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_application_settings, container, false);

        mCheckBoxUserSettings = (CheckBox) v.findViewById(R.id.checkBoxAppSettingsUser);

        userSettings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        userSettingsEditor = userSettings.edit();

        db = new dbHelper(getActivity(), null, null, 1);

        isUserSettingsChecked = false;
        mCheckBoxUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserSettingsChecked = !isUserSettingsChecked;
                Log.i(TAG, "user settings checkbox was " +
                        (isUserSettingsChecked ? "checked" : "unchecked"));
            }
        });

        mCheckBoxRestaurantHistory = (CheckBox) v.findViewById(R.id.checkBoxAppSettingsRestaurant);
        isRestaurantHistoryChecked = false;
        mCheckBoxRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRestaurantHistoryChecked = !isRestaurantHistoryChecked;
                Log.i(TAG, "restaurant history checkbox was " +
                        (isRestaurantHistoryChecked ? "checked" : "unchecked"));
            }
        });

        mBtnReset = (Button) v.findViewById(R.id.buttonAppSettingsReset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserSettingsChecked || isRestaurantHistoryChecked)
                    resetAppSettings();
            }
        });

        return v;
    }

    public boolean isUserSettingsChecked() {
        return isUserSettingsChecked;
    }

    public boolean isRestaurantHistoryChecked() {
        return isRestaurantHistoryChecked;
    }

    public void resetAppSettings() {
        if (isUserSettingsChecked) {
            // Wipe SharedPreferences
            userSettingsEditor.clear().commit();

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
            String appLanguage = Locale.getDefault().getDisplayLanguage();

            userSettingsEditor.putBoolean("hasAgreedToEula", hasAgreed).apply();
            userSettingsEditor.putFloat("defaultMinStar", minStar).apply();
            userSettingsEditor.putString("defaultSearchLocation", searchLocation).apply();
            userSettingsEditor.putFloat("defaultSearchRadius", searchRadius).apply();
//            userSettings.getStringSet("defaultPreferredNiches", getPreferredNiches().apply());
            userSettingsEditor.putString("appLanguage", Locale.getDefault().getDisplayLanguage()).apply();

            Log.i(TAG, "cleared user settings");
            ToastUtil.showShortToast(getActivity(), "Cleared User Settings");
        }

        if (isRestaurantHistoryChecked) {
            db.wipeRestaurantList(1);
            Log.i(TAG, "wiped green list (1)");
            db.wipeRestaurantList(2);
            Log.i(TAG, "wiped yellow list (2)");
            db.wipeRestaurantList(3);
            Log.i(TAG, "wiped red list (3)");
            ToastUtil.showShortToast(getActivity(), "Cleared Restaurant History");
        }

        getDialog().dismiss();
    }
}
