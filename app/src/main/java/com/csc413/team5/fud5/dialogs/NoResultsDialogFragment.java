package com.csc413.team5.fud5.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.csc413.team5.fud5.R;

/**
 * Dialog which displays when a search produces no results.
 */
public class NoResultsDialogFragment extends DialogFragment {
    private static NoResultsDialogFragment instance = null;

    public NoResultsDialogFragment() { // defeat instantiation
    }

    public static NoResultsDialogFragment getInstance() {
        instance = new NoResultsDialogFragment();
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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