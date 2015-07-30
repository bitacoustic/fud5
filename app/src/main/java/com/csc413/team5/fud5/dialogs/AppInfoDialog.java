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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;

import com.csc413.team5.fud5.R;

import static com.csc413.team5.fud5.R.id.okayAppInfo;

/**
 * Created by JagatJagat on 7/28/15.
 */
public class AppInfoDialog extends DialogFragment {
    private static AppInfoDialog instance = null;
    private Context context1;
    private String LicenseInfo;


    public AppInfoDialog(){
        
    }



    public static AppInfoDialog getInstance(){

         if(instance == null)
         {
             instance = new AppInfoDialog();
         }
        else
         {
             return instance;
         }

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        return dialog;

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_app_info, container, false);


        Button okButton = (Button)v.findViewById(R.id.okayAppInfo);

        GoogleApiAvailability AGoogleApiAvailability = GoogleApiAvailability.getInstance();
        LicenseInfo = AGoogleApiAvailability.getOpenSourceSoftwareLicenseInfo(getActivity());

        LinearLayout linearLayout = (LinearLayout)v.findViewById(R.id.googleMapsLicense);

        TextView textView1 = new TextView(v.getContext());

        textView1.setText(LicenseInfo);
        linearLayout.addView(textView1);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss(); // return to the calling activity

            }
        });

        return v;
    }




}
