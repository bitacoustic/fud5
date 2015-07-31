package com.csc413.team5.fud5.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.csc413.team5.fud5.R;

public class GreenFollowupDialogFragment extends DialogFragment {
    public static GreenFollowupDialogFragment instance = null;

    GreenFollowupDialogListener mListener;

    String mId;
    long mTimestamp;

    public GreenFollowupDialogFragment() {}

    public static GreenFollowupDialogFragment getInstance(String id, long timestamp) {
        if (instance == null)
            instance = new GreenFollowupDialogFragment();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putLong("timestamp", timestamp);
        instance.setArguments(args);

        return instance;
    }

    public interface GreenFollowupDialogListener {
        void OnGreenFollowupClickedGreen(DialogFragment dialog);
        void OnGreenFollowupClickedYellow(DialogFragment dialog);
        void OnGreenFollowupClickedRed(DialogFragment dialog);
    }

    // Instantiate a GreenFollowupDialogListener and make sure the host activity implements the
    // callback interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GreenFollowupDialogListener so we can send events to the host
            mListener = (GreenFollowupDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GreenFollowupDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance.mId = getArguments().getString("id");
        instance.mTimestamp = getArguments().getLong("timestamp");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_green_followup, container, false);

        TextView dialogTitle = (TextView) v.findViewById(R.id.textViewGreenFollowupTitle);
        dialogTitle.append(instance.mId + "?");

        Button green = (Button) v.findViewById(R.id.buttonGreenFollowupGreen);
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnGreenFollowupClickedGreen(GreenFollowupDialogFragment.this);
            }
        });

        Button yellow = (Button) v.findViewById(R.id.buttonGreenFollowupYellow);
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnGreenFollowupClickedYellow(GreenFollowupDialogFragment.this);
            }
        });

        Button red = (Button) v.findViewById(R.id.buttonGreenFollowupRed);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnGreenFollowupClickedRed(GreenFollowupDialogFragment.this);
            }
        });

        return v;
    }
}
