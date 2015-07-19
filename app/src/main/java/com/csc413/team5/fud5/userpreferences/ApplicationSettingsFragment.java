package com.csc413.team5.fud5.userpreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.csc413.team5.fud5.R;

public class ApplicationSettingsFragment extends DialogFragment {
    protected boolean mIsUserSettingsChecked;
    protected boolean mIsRestaurantHistoryChecked;

    ApplicationSettingsConfirmListener mListener;

    public static ApplicationSettingsFragment
    newInstance(boolean isUserSettingsChecked, boolean isRestaurantHistoryChecked) {
        ApplicationSettingsFragment f = new ApplicationSettingsFragment();

        Bundle args = new Bundle();
        args.putBoolean("isUserSettingsChecked", isUserSettingsChecked);
        args.putBoolean("isRestaurantHistoryChecked", isRestaurantHistoryChecked);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsUserSettingsChecked = getArguments()
                .getBoolean("isUserSettingsChecked", false);
        mIsRestaurantHistoryChecked = getArguments()
                .getBoolean("isRestaurantHistoryChecked", false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String userSettings = getActivity().getResources().getString(R.string
                .activity_application_settings_user_settings_text);
        String restaurantHistory = getActivity().getResources().getString(R.string
                .activity_application_settings_restaurant_history_text);
        String areYouSureYouWantToResset = getActivity().getResources().getString(R.string
                .activity_application_settings_are_you_sure_text);
        String positiveButtonText = getActivity().getResources().getString(R.string
                .activity_application_settings_positive_button_text);
        String negativeButtonText = getActivity().getResources().getString(R.string
                .activity_application_settings_negative_button_text);

        StringBuilder message = new StringBuilder(areYouSureYouWantToResset);
        message.append(" ");

        if (mIsUserSettingsChecked && mIsRestaurantHistoryChecked)
            message.append(userSettings + " & " + restaurantHistory + "?");
        else if (mIsUserSettingsChecked)
            message.append(userSettings + "?");
        else if (mIsRestaurantHistoryChecked)
            message.append(restaurantHistory + "?");
        else
            this.dismiss();

        builder.setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onApplicationSettingsConfirmPositiveClick
                                (ApplicationSettingsFragment.this);
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onApplicationSettingsConfirmNegativeClick
                                (ApplicationSettingsFragment.this);
                    }
                });

        return builder.create();
    }

    public interface ApplicationSettingsConfirmListener {
        void onApplicationSettingsConfirmPositiveClick(DialogFragment dialog);

        void onApplicationSettingsConfirmNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ApplicationSettingsConfirmListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
