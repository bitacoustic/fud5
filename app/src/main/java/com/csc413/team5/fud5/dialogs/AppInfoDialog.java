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

import static com.csc413.team5.fud5.R.id.okayAppInfo;

/**
 * Created by JagatJagat on 7/28/15.
 */
public class AppInfoDialog extends DialogFragment {
    private static AppInfoDialog instance = null;

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
        //View v = inflater.inflate(R.layout.fragment_restaurant_menus, container, false);
        View v = inflater.inflate(R.layout.fragment_app_info, container, false);

        //Button button = (Button)v.findViewById(R.id.okayAppInfo);

        Button okButton = (Button)v.findViewById(R.id.okayAppInfo);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
                getDialog().dismiss(); // return to the calling activity

            }
        });

        return v;
    }




}
