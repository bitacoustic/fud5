package com.csc413.team5.fud5;

/**
 * Created by ivanG on 7/8/15.
 */
public interface Selector {
    boolean isOpen();
    String checkTimeSamp();
    boolean isInRedList();
    void weighGreenAndYellows();
    String getSelection();


}
