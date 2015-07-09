package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A dialog providing the end-user license agreement. If EulaDialogFragment has argument
 * hasAgreedToEula = false, the EULA has not been agreed to yet.
 * <p>
 * Created by Eric on 7/8/2015.
 *
 * @author Eric C. Black
 */
public class EulaDialogFragment extends DialogFragment {
    boolean mHasAgreedToEula;

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
        if (!mHasAgreedToEula)
            button.setText(R.string.dialog_eula_button_agree);

        // watch for button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return to the calling activity; if it's the first time user has run the
                // app, a return should signal that the user has agreed to the EULA

                // ((FragmentDialog)getActivity()).showDialog(); -- doesn't work, deprecated?
                getActivity(); // TODO does this work?
            }
        });

        return v;
    }
}
