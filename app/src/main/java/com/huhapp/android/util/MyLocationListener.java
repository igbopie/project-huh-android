package com.huhapp.android.util;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.huhapp.android.api.Api;
import com.huhapp.android.common.logger.Log;

/**
 * Created by igbopie on 5/17/15.
 */
public class MyLocationListener implements LocationListener {

    private static long LOCATION_REFRESH_TIME = 60 * 1000; //millis
    private static float LOCATION_REFRESH_DISTANCE = 100; //meters
    private static MyLocationListener singleton;

    public static MyLocationListener getInstance(LocationManager mLocationManager){
        if (singleton == null){
            singleton = new MyLocationListener(mLocationManager);
        }
        return singleton;
    }

    private MyLocationListener(LocationManager mLocationManager) {
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE,
                this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        PropertyAccessor.setUserLatitude(latitude);
        PropertyAccessor.setUserLongitude(longitude);
        new RegisterLocation(longitude, latitude).execute();
        Log.i("LOCATIONMANAGER", "Lat "+ latitude+" Long "+longitude);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class RegisterLocation extends AsyncTask<Void, Void, Void> {
        double longitude;
        double latitude;

        private RegisterLocation(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Api.addLocation(PropertyAccessor.getUserId(), longitude, latitude);
            return null;
        }
    }
}
