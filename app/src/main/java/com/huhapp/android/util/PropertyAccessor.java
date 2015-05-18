package com.huhapp.android.util;

import android.content.Context;

/**
 * Created by igbopie on 5/17/15.
 */
public class PropertyAccessor {

    private static Context _context;

    public static void init(Context context){
        _context = context;
    }

    public static String getUserId() {
        return PrefUtils.getFromPrefs(_context, PrefUtils.PREFS_USER_ID, "");
    }

    public static void setUserId(String userId) {
        PrefUtils.saveToPrefs(_context, PrefUtils.PREFS_USER_ID, userId);
    }

    public static double getUserLongitude() {
        return PrefUtils.getFloatFromPrefs(_context, PrefUtils.PREFS_LAST_LONG,  0);
    }

    public static void setUserLongitude(double longitude){
        PrefUtils.saveFloatToPrefs(_context, PrefUtils.PREFS_LAST_LONG, (float) longitude);
    }

    public static double getUserLatitude() {
        return PrefUtils.getFloatFromPrefs(_context, PrefUtils.PREFS_LAST_LAT,  0);
    }

    public static void setUserLatitude(double longitude){
        PrefUtils.saveFloatToPrefs(_context, PrefUtils.PREFS_LAST_LAT, (float) longitude);
    }

}
