package com.csc413.team5.fud5.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.csc413.team5.fud5.MainActivity;
import com.csc413.team5.fud5.R;

/**
 * A dialog providing the end-user license agreement.
 * <p>
 * If EulaDialogFragment has argumenthasAgreedToEula = false, the EULA has not been agreed to yet:
 * set button to "I agree" and display the dialog the first time the app runs. If true, the EULA
 * is available from the user preference/search page: button says "OK" (or perhaps hidden
 * altogether).
 */
public class EulaDialogFragment extends DialogFragment {
    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;

    private static final String EULA_PREFIX = "eula_";
    private String eulaKey;

    TextView mTxtEulaBody;

    public static EulaDialogFragment newInstance() {
        return new EulaDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    /**
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eula, container, false);

        Button button = (Button)v.findViewById(R.id.dialogEulaButton);

        // load user preferences
        userSettings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        // create a user settings editor for use in this activity
        userSettingsEditor = userSettings.edit();

        int versionCode;
        try {
            versionCode = getActivity().getPackageManager().getPackageInfo(getActivity()
                    .getPackageName(), 0).versionCode;
            eulaKey = EULA_PREFIX + versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            eulaKey = null;
        }

        String versionName;
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity()
                    .getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }

        if (eulaKey != null && !userSettings.getBoolean(eulaKey, false))
            userSettingsEditor.putBoolean("hasAgreedToEula", false).commit();

        // If user hasn't agreed to terms of service, set button text to "I agree".
        // This will be the case if user is starting the app for the first time.
        if (!userSettings.getBoolean("hasAgreedToEula", false))
            button.setText(R.string.dialog_eula_button_agree);

        mTxtEulaBody = (TextView) v.findViewById(R.id.dialogEulaText);

        mTxtEulaBody.setText(
                getActivity().getApplicationContext().getResources()
                        .getString(R.string.app_name) + " " + versionName
                        + getActivity().getApplicationContext().getResources()
                        .getString(R.string.dialog_eula_text));

        // watch for button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss(); // return to the calling activity

            }
        });

        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (!userSettings.getBoolean("hasAgreedToEula", false)) {
            userSettingsEditor.putBoolean("hasAgreedToEula", true)
                    .putBoolean(eulaKey, true)
                    .commit();
            // if it's the first time user has run the app or if they have not previously
            // agreed to the EULA, button click signals that the user has agreed

            LocationManager lm = (LocationManager) getActivity().getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = false;
            boolean networkEnabled = false;

            // check whether GPS and network providers are enabled
            try {
                gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) { }

            try {
                networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) { }

            // only show dialog if location services are not enabled
            if (!gpsEnabled && !networkEnabled) {
                DialogFragment dialogAskToUseLocation = new AskToUseLocationDialogFragment();
                dialogAskToUseLocation.show(getFragmentManager(), "askToUseLocation");
            } else {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        }
    }
}
