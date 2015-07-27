package com.csc413.team5.fud5.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.csc413.team5.fud5.R;

public class ApplicationSettingsFragment extends DialogFragment {
    protected boolean mIsUserSettingsChecked;
    protected boolean mIsIgnoredRestaurantHistoryChecked;
    protected boolean mIsAllRestaurantHistoryChecked;

    ApplicationSettingsConfirmListener mListener;

    public static ApplicationSettingsFragment
    newInstance(boolean isUserSettingsChecked, boolean isIgnoredRestaurantHistoryChecked,
                boolean isAllRestaurantHistoryChecked) {
        ApplicationSettingsFragment f = new ApplicationSettingsFragment();

        Bundle args = new Bundle();
        args.putBoolean("isUserSettingsChecked", isUserSettingsChecked);
        args.putBoolean("isIgnoredRestaurantHistoryChecked", isIgnoredRestaurantHistoryChecked);
        args.putBoolean("isAllRestaurantHistoryChecked", isAllRestaurantHistoryChecked);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsUserSettingsChecked = getArguments()
                .getBoolean("isUserSettingsChecked", false);
        mIsIgnoredRestaurantHistoryChecked = getArguments().getBoolean
                ("isIgnoredRestaurantHistoryChecked", false);
        mIsAllRestaurantHistoryChecked = getArguments()
                .getBoolean("isAllRestaurantHistoryChecked", false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String userSettings = getActivity().getResources().getString(R.string
                .activity_application_settings_user_settings_text);
        String ignoredRestaurantHistory = getActivity().getString(R.string
                .activity_application_settings_ignored_restaurant_history_text);
        String allRestaurantHistory = getActivity().getResources().getString(R.string
                .activity_application_settings_all_restaurant_history_text);
        String areYouSureYouWantToResset = getActivity().getResources().getString(R.string
                .activity_application_settings_are_you_sure_text);
        String positiveButtonText = getActivity().getResources().getString(R.string
                .activity_application_settings_positive_button_text);
        String negativeButtonText = getActivity().getResources().getString(R.string
                .activity_application_settings_negative_button_text);

        StringBuilder message = new StringBuilder(areYouSureYouWantToResset);
        message.append(" ");

        if (mIsUserSettingsChecked
                && (mIsIgnoredRestaurantHistoryChecked || mIsAllRestaurantHistoryChecked))
            message.append(userSettings + " & " + (mIsIgnoredRestaurantHistoryChecked ?
                    ignoredRestaurantHistory : allRestaurantHistory)  + "?");
        else if (mIsUserSettingsChecked)
            message.append(userSettings + "?");
        else if (mIsIgnoredRestaurantHistoryChecked)
            message.append(ignoredRestaurantHistory + "?");
        else if (mIsAllRestaurantHistoryChecked)
            message.append(allRestaurantHistory + "?");
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
