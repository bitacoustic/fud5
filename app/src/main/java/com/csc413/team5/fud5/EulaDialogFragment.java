package com.csc413.team5.fud5;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

/**
 * A dialog providing the end-user license agreement.
 * <p>
 * If EulaDialogFragment has argumenthasAgreedToEula = false, the EULA has not been agreed to yet:
 * set button to "I agree" and display the dialog the first time the app runs. If true, the EULA
 * is available from the user preference/search page: button says "OK" (or perhaps hidden
 * altogether).
 * <p>
 * Created by Eric on 7/8/2015.
 *
 * @author Eric C. Black
 */
public class EulaDialogFragment extends DialogFragment {
    boolean mHasAgreedToEula;
    boolean mIsFirstRun;

    public static EulaDialogFragment newInstance(boolean hasAgreedToEula) {
        EulaDialogFragment f = new EulaDialogFragment();

        // Supply boolean input as an argument
        Bundle args = new Bundle();
        args.putBoolean("hasAgreedToEula", hasAgreedToEula);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the argument
        mHasAgreedToEula = getArguments().getBoolean("hasAgreedToEula");
        mIsFirstRun = !mHasAgreedToEula; // flag if this is app's first run
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

        // If user hasn't agreed to terms of service, set button text to "I agree".
        // This will be the case if user is starting the app for the first time.
        if (mIsFirstRun)
            button.setText(R.string.dialog_eula_button_agree);

        // watch for button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if it's the first time user has run the app or if they have not previously
                // agreed to the EULA, button click signals that the user has agreed
                if (mIsFirstRun)
                    ((SplashScreenActivity)getActivity()).setHasAgreedToEula(true);

                getDialog().dismiss(); // return to the calling activity

            }
        });

        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mIsFirstRun) {
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
                DialogFragment dialogAskToUseLocation = new AskToUseLocationFragment();
                dialogAskToUseLocation.show(getFragmentManager(), "askToUseLocation");
            } else {
                getActivity().finish();   // removes this activity from the back stack
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
