package com.csc413.team5.fud5.utils;

import android.content.Context;
import android.widget.Toast;
/**
 * Toast utility made to make debugging easier and more visual
 */
public class ToastUtil {
    /**
     * Shows a long toast
     *
     * @param context: The current context
     * @param msg: The toast's message
     */
    public static void showLongToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a short toast
     *
     * @param context: The current context
     * @param msg: The toast's message
     */
    public static void showShortToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
