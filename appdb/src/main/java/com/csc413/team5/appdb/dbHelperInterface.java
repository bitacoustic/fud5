package com.csc413.team5.appdb;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public interface dbHelperInterface {
    //create
    boolean insertRestaurantToList(Restaurant new_restaurant, int listClass);

    // update
    boolean migrateRestaurantListItem(Restaurant my_restaurant, int fromList, int toList);

    // delete
    boolean deleteRestaurantFromGreenList(Restaurant my_restaurant);
    boolean deleteRestaurantFromYellowList(Restaurant my_restaurant);
    boolean deleteRestaurantFromRedList(Restaurant my_restaurant);

    // helpers
    int getRestaurantList(Restaurant my_restaurant);
    boolean isRestaurantInGreenList(Restaurant my_restaurant);
    boolean isRestaurantInYellowList(Restaurant my_restaurant);
    boolean isRestaurantInRedList(Restaurant my_restaurant);


    ArrayList<String> showListContents(int listClassification);
    boolean checkDbExist(String path);
    String showContents(int listClassification);
    String getName();
    String getDBPath();
}
