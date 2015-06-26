package com.csc413.team5.appdb;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public interface dbHelperInterface {
    boolean insertRestaurantToList(Restaurant new_restaurant, int listClassification);
    ArrayList<String> showListContents(int listClassification);
    String showContents(int listClassification);
    String getName();
}
