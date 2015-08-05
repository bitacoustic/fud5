package com.csc413.team5.restaurantapiwrapper;

import android.content.ComponentName;
import android.content.Intent;
import android.view.*;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An individual menu of a restaurant, e.g. the lunch menu, possibly comprised of multiple
 * {@link MenuSection}s.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class Menu<MenuSection> extends ArrayList<MenuSection> implements Serializable, android.view.Menu {
    protected String menuName;
    protected String currencySymbol;

    public Menu(String menuName, String currencySymbol) {
        super();
        this.menuName = menuName;
        this.currencySymbol = currencySymbol;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public MenuSection getSection(int index) {
        return get(index);
    }

    public void addMenuSection(MenuSection m) {
        add(m);
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "Menu {" +
                "\nmenuName='" + menuName + '\'' +
                ",\n currencySymbol='" + currencySymbol + '\'' +
                ",\n sections=" + super.toString() +
                "\n}";
    }

    @Override
    public MenuItem add(CharSequence title) {
        return null;
    }

    @Override
    public MenuItem add(int titleRes) {
        return null;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int titleRes) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return null;
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        return 0;
    }

    @Override
    public void removeItem(int id) {

    }

    @Override
    public void removeGroup(int groupId) {

    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {

    }

    @Override
    public void setGroupVisible(int group, boolean visible) {

    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {

    }

    @Override
    public boolean hasVisibleItems() {
        return false;
    }

    @Override
    public MenuItem findItem(int id) {
        return null;
    }

    @Override
    public MenuItem getItem(int index) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        return false;
    }

    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean performIdentifierAction(int id, int flags) {
        return false;
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {

    }
}
