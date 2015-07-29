package com.csc413.team5.fud5.utils;

import java.util.Random;

/** Generates random texts
 * Created by niculistana on 7/29/15.
 */
public class TextGeneratorUtil {
    /**
     * Generates a random string based on a string resource value
     * @param arrayRes an array xml resource usually defined under res/values
     */
    public static String randomizeFromArray(String[] arrayRes) {
        Random randomGenerator = new Random();
        int arrayIndex = randomGenerator.nextInt(arrayRes.length);

        return arrayRes[arrayIndex];
    }
}
