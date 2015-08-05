package com.csc413.team5.fud5.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.Constants;

/**
 * Dialog which displays when a search produces no results, or user reached last restaurant in
 * the results list (no more results).
 */
public class NoResultsDialogFragment extends DialogFragment {
    private static NoResultsDialogFragment instance = null;

    private int displayWhen;
    private Context mContext;

    public NoResultsDialogFragment() { // defeat instantiation
    }

    public static NoResultsDialogFragment getInstance(int displayWhen) {
        if (instance == null)
            instance = new NoResultsDialogFragment();

        Bundle args = new Bundle();
        args.putInt("displayWhen", displayWhen);
        instance.setArguments(args);

        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayWhen = getArguments().getInt("displayWhen", Constants.NO_RESULTS);
        mContext = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_no_results, container, false);

        if (displayWhen == Constants.NO_MORE_RESULTS) {
            TextView dialogTitle = (TextView) v.findViewById(R.id.textViewNoResultsTitle);
            dialogTitle.setText("No more results");
        }



        Button btnOk = (Button) v.findViewById(R.id.buttonNoResultsOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the calling activity from stack and return to previous activity
                getActivity().finish();
            }
        });
        return v;
    }
}