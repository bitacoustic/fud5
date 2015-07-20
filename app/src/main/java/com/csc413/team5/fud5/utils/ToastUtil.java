package com.csc413.team5.fud5.utils;

import android.content.Context;
import android.widget.Toast;
/**
 * Toast utility made to make debugging easier and more visual
 */
public class ToastUtil {
    /**
     * Long toast
     *
     * @param context
     * @param msg
     */
    public static void showLongToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Short toast
     *
     * @param context
     * @param msg
     */
    public static void showShortToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
