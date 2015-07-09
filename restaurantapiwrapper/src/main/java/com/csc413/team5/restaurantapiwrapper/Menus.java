package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The {@link Menu}s of a restaurant.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class Menus<Menu> extends ArrayList<Menu> implements Serializable {

    public Menu getMenu(int index) throws IndexOutOfBoundsException {
        return get(index);
    }
}

