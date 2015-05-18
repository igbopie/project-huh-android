package com.huhapp.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {
    public static final String PREFS_USER_ID = "__USER_ID__" ;
    public static final String PREFS_LAST_LONG = "__LAST_LONG__" ;
    public static final String PREFS_LAST_LAT = "__LAST_LAT__" ;
    public static final String PREFS_REG_ID = "__REG_ID__";
    public static final String PREFS_APP_VERSION = "__APP_ID__";


    /**
     * Called to save supplied value in shared preferences against given key.
     * @param context Context of caller activity
     * @param key Key of value to save against
     * @param value Value to save
     */
    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void saveFloatToPrefs(Context context, String key, float value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void saveIntToPrefs(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Called to retrieve required value from shared preferences, identified by given key.
     * Default value will be returned of no value found or error occurred.
     * @param context Context of caller activity
     * @param key Key to find value against
     * @param defaultValue Value to return if no data found against given key
     * @return Return the value found against given key, default if not found or any error occurs
     */
    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static float getFloatFromPrefs(Context context, String key, float defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getFloat(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getIntFromPrefs(Context context, String key, int defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}