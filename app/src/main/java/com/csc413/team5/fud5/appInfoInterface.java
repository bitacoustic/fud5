package com.csc413.team5.fud5;

public interface appInfoInterface {
    String getAppName();
    double getAppVersion();
    String getAppLogoPath();
    void setAppName(String name);
    void setAppVersion(double appVersion);
    void setAppLogoPath(String logoPath);
}
