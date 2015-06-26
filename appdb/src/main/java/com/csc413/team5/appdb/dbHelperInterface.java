package com.csc413.team5.appdb;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public interface dbHelperInterface {
    boolean insertList(Restaurant new_restaurant);
    ArrayList<String> showListContents(int listCategory);
    String getName();
}
