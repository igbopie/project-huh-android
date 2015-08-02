package com.huhapp.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.huhapp.android.common.logger.Log;

/**
 * Created by igbopie on 5/17/15.
 */
public class PropertyAccessor {

    private static Context _context;

    public static void init(Context context){
        _context = context;
    }

    public static String getUsername() {
        return PrefUtils.getFromPrefs(_context, PrefUtils.PREFS_USERNAME, "");
    }

    public static void setUsername(String username) {
        PrefUtils.saveToPrefs(_context, PrefUtils.PREFS_USERNAME, username);
    }

    public static String getPassword() {
        return PrefUtils.getFromPrefs(_context, PrefUtils.PREFS_PASSWORD, "");
    }

    public static void setPassword(String password) {
        PrefUtils.saveToPrefs(_context, PrefUtils.PREFS_PASSWORD, password);
    }

    public static String getToken() {
        return PrefUtils.getFromPrefs(_context, PrefUtils.PREFS_TOKEN, "");
    }

    public static void setToken(String token) {
        PrefUtils.saveToPrefs(_context, PrefUtils.PREFS_TOKEN, token);
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

    private static String getRegId() {
        return PrefUtils.getFromPrefs(_context, PrefUtils.PREFS_REG_ID,  "");
    }

    public static int getAppVersion() {
        return PrefUtils.getIntFromPrefs(_context, PrefUtils.PREFS_APP_VERSION,  Integer.MIN_VALUE);
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId() {
        String registrationId = getRegId();
        if (registrationId.isEmpty()) {
            Log.i("PropertyAccessor", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = getAppVersion();
        int currentVersion = computeAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i("PropertyAccessor", "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static void setRegistrationId(String registrationId) {
        PrefUtils.saveToPrefs(_context, PrefUtils.PREFS_REG_ID,  registrationId);
        PrefUtils.saveIntToPrefs(_context, PrefUtils.PREFS_APP_VERSION,  computeAppVersion());
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int computeAppVersion() {
        try {
            PackageInfo packageInfo = _context.getPackageManager()
                    .getPackageInfo(_context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
