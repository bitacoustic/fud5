package com.csc413.team5.fud5.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csc413.team5.fud5.R;
import com.csc413.team5.restaurantapiwrapper.Menu;
import com.csc413.team5.restaurantapiwrapper.MenuContent;
import com.csc413.team5.restaurantapiwrapper.MenuItem;
import com.csc413.team5.restaurantapiwrapper.MenuSection;
import com.csc413.team5.restaurantapiwrapper.MenuSectionText;
import com.csc413.team5.restaurantapiwrapper.MenuSubSection;
import com.csc413.team5.restaurantapiwrapper.Menus;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

/**
 * Display the menus for the {@link Restaurant} specified on instantiation.
 * <p>
 * Created on 7/11/2015.
 *
 * @author Eric C. Black
 */
public class DisplayRestaurantMenusFragment extends DialogFragment {
    private static DisplayRestaurantMenusFragment instance = null;

    Restaurant mRestaurant; // the restaurant for which to display the menu
    TextView txtMenusRestaurantName;
    ImageView imgPoweredByLocu;

    public DisplayRestaurantMenusFragment() { // defeat instantiation
    }

    /**
     * Use to instantiate a new dialog to display the specified restaurant's menu. The calling
     * activity should only open this dialog if a menus are available for this restaurant.
     * @param r a {@link Restaurant}
     * @return a new instance of DisplayRestaurantMenusFragment with the specified restaurant
     *         as its argument
     */
    public static DisplayRestaurantMenusFragment getInstance(Restaurant r) {
        if (r == null)
            return null;

        if (instance == null)
            instance = new DisplayRestaurantMenusFragment();

        Bundle args = new Bundle();
        args.putSerializable("restaurant", r);
        instance.setArguments(args);

        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the restaurant argument
        mRestaurant = (Restaurant) getArguments().getSerializable("restaurant");
    }

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     * <p/>
     * <p>This method will be called after {@link #onCreate(Bundle)} and
     * before {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.  The
     * default implementation simply instantiates and returns a {@link Dialog}
     * class.
     * <p/>
     * <p><em>Note: DialogFragment own the {@link Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em></p>
     *
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurant_menus, container, false);

        ImageButton btnBack = (ImageButton) v.findViewById(R.id.imageButtonRMenus);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        txtMenusRestaurantName = (TextView) v.findViewById(R.id.textViewRMenusRestaurantName);
//        txtMenusRestaurantName.setGravity(Gravity.CENTER);
        txtMenusRestaurantName.setText(mRestaurant.getBusinessName());

        imgPoweredByLocu = (ImageView) v.findViewById(R.id.imageViewRMenusPoweredByLocu);
        imgPoweredByLocu.setImageResource(R.drawable.poweredbylocu);
        LinearLayout.LayoutParams imgPoweredByLocuParams = new LinearLayout.LayoutParams(310, 50);
        imgPoweredByLocuParams.setMargins(0, 0, 0, 0);
        imgPoweredByLocu.setLayoutParams(imgPoweredByLocuParams);

        Menus menus = mRestaurant.getMenus();

        LinearLayout linearLayoutMenu = (LinearLayout) v.findViewById(R.id
                .linearLayoutRMenusContent);
        //ArrayList<TextView> tvList = new ArrayList<>();

        for (int i = 0; i < menus.size(); i++) {
            Menu menu = (Menu) menus.getMenu(i);

            // top divider line
            ImageView dividerTop = new ImageView(v.getContext());
            LinearLayout.LayoutParams paramsDivider = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 5);
            paramsDivider.setMargins(10, 50, 10, 5);
            dividerTop.setLayoutParams(paramsDivider);
            dividerTop.setBackgroundColor(Color.DKGRAY);
            linearLayoutMenu.addView(dividerTop);

            // Title of the individual menu
            TextView tvEachMenu = new TextView(v.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            params.setMargins(10, 0, 10, 0);
            tvEachMenu.setTextSize(22);
            tvEachMenu.setTextColor(Color.DKGRAY);
            tvEachMenu.setLayoutParams(params);
            tvEachMenu.setText(menu.getMenuName());
            tvEachMenu.setGravity(Gravity.CENTER);
            linearLayoutMenu.addView(tvEachMenu);

            // bottom divider line
            ImageView dividerBottom = new ImageView(v.getContext());
            LinearLayout.LayoutParams paramsDividerBottom = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 5);
            paramsDividerBottom.setMargins(10, 5, 10, 10);
            dividerBottom.setLayoutParams(paramsDividerBottom);
            dividerBottom.setBackgroundColor(Color.DKGRAY);
            linearLayoutMenu.addView(dividerBottom);

            // menu section
            for (int j = 0; j < menu.size(); j++) {
                MenuSection menuSection = (MenuSection) menu.getSection(j);

                // display section name if it is non-empty
                if (menuSection.getSectionName().compareTo("") != 0) {
                    TextView tvEachSection = new TextView(v.getContext());
                    LinearLayout.LayoutParams paramsSection = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
                    paramsSection.setMargins(25, 50, 25, 5);
                    tvEachSection.setTextSize(18);
                    tvEachSection.setTextColor(Color.DKGRAY);
                    tvEachSection.setLayoutParams(paramsSection);
                    tvEachSection.setText(menuSection.getSectionName());
                    linearLayoutMenu.addView(tvEachSection);

                    // divider
                    ImageView sectionDivider = new ImageView(v.getContext());
                    LinearLayout.LayoutParams paramsSectionDivider = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 5);
                    paramsSectionDivider.setMargins(20, 0, 20, 5);
                    sectionDivider.setBackgroundColor(Color.DKGRAY);
                    sectionDivider.setLayoutParams(paramsSectionDivider);
                    linearLayoutMenu.addView(sectionDivider);
                }


                // menu subsection (description)
                for (int k = 0; k < menuSection.size(); k++) {
                    MenuSubSection menuSubSection = (MenuSubSection) menuSection.getSubSection(k);

                    // display sub-section name if it is non-empty
                    if (menuSubSection.getSubSectionName().compareTo("") != 0) {
                        TextView tvEachSubSection = new TextView(v.getContext());
                        LinearLayout.LayoutParams paramsSubSection = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT );
                        paramsSubSection.setMargins(25, 20, 25, 0);
                        tvEachSubSection.setTextSize(16);
                        tvEachSubSection.setLayoutParams(paramsSubSection);
                        tvEachSubSection.setText(menuSubSection.getSubSectionName());
                        linearLayoutMenu.addView(tvEachSubSection);
                    }

                    // menu content (menu item or text)
                    for (int l = 0; l < menuSubSection.size(); l++) {
                        MenuContent menuContent = (MenuContent) menuSubSection.getContent(l);

                        TextView tvEachContent;

                        if (menuContent.getClass().equals(MenuItem.class)) {
                            MenuItem thisItem = (MenuItem) menuContent;

                            // name of item
                            tvEachContent = new TextView(v.getContext());
                            RelativeLayout.LayoutParams paramsItemName
                                    = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            paramsItemName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            // defer setting parameters until it's been determined the item
                            // has a price

                            // price
                            TextView tvEachItemPrice;
                            if (thisItem.getPrice().compareTo("") != 0) {
                                tvEachItemPrice = new TextView(v.getContext());
                                RelativeLayout.LayoutParams paramsItemPrice
                                        = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                paramsItemPrice.setMargins(25, 20, 25, 0);
                                paramsItemPrice.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                paramsItemPrice.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                tvEachItemPrice.setTextSize(12);
                                tvEachItemPrice.setLayoutParams(paramsItemPrice);
                                tvEachItemPrice.setText(menu.getCurrencySymbol()
                                        + thisItem.getPrice());
                            } else {
                                tvEachItemPrice = null;
                            }

                            // display item name and price side-by-side
                            RelativeLayout trNamePrice = new RelativeLayout(v.getContext());
                            RelativeLayout.LayoutParams paramsNamePrice
                                    = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            trNamePrice.setLayoutParams(paramsNamePrice);
                            trNamePrice.addView(tvEachContent);
                            // set width of item name depending on whether there's a price
                            if (tvEachItemPrice != null) {
                                paramsItemName.setMargins(25, 20, 150, 0);
                                tvEachContent.setLayoutParams(paramsItemName);
                                tvEachContent.setText(thisItem.getName());
                                trNamePrice.addView(tvEachItemPrice);
                            } else {
                                paramsItemName.setMargins(25, 20, 25, 0);
                                tvEachContent.setLayoutParams(paramsItemName);
                                tvEachContent.setText(thisItem.getName());
                            }
                            linearLayoutMenu.addView(trNamePrice);

                            // description
                            if (thisItem.getDescription().compareTo("") != 0) {
                                TextView tvEachItemDescription = new TextView(v.getContext());
                                LinearLayout.LayoutParams paramsDescription =
                                        new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT );
                                paramsDescription.setMargins(25, 0, 25, 0);
                                tvEachItemDescription.setTextSize(12);
                                tvEachItemDescription.setTypeface(null, Typeface.ITALIC);
                                tvEachItemDescription.setLayoutParams(paramsDescription);
                                tvEachItemDescription.setText(thisItem.getDescription());
                                linearLayoutMenu.addView(tvEachItemDescription);
                            }
                        } // end if (menuContent.getClass().equals(MenuItem.class))

                        // menu section text (inline with menu items)
                        else if (menuContent.getClass().equals(MenuSectionText.class)) {
                            TextView tvEachSectionText = new TextView(v.getContext());
                            LinearLayout.LayoutParams paramsContent =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT );
                            tvEachSectionText.setTextSize(12);
                            tvEachSectionText.setPadding(25, 0, 25, 0);
                            tvEachSectionText.setLayoutParams(paramsContent);
                            tvEachSectionText.setText(
                                    ((MenuSectionText)menuContent).getText() );
                            linearLayoutMenu.addView(tvEachSectionText);
                        }

                    } // end for each menu content (item / section text)
                } // end for each sub section
            } // end for each section
        } // end for each menu

        return v;
    }
}
