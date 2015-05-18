/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.huhapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.activities.SampleActivityBase;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.util.MyLocationListener;
import com.huhapp.android.util.PrefUtils;
import com.huhapp.android.util.PropertyAccessor;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.io.IOException;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static final String TAG = "MainActivity";

    private Drawable faSearchDeactive;
    private Drawable faSearchActive;
    private Drawable faUserDeactive;
    private Drawable faUserActive;
    private Drawable faBellODeactive;
    private Drawable faBellOActive;
    private Drawable faEllipsisHDeactive;
    private Drawable faEllipsisHActive;
    private Drawable faAddDeactive;
    private Drawable faAddActive;
    private Menu menu;

    private Fragment currentFragment;

    public static final int CREATE_QUESTION_CODE = 1816;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.initLocation();
        this.initRes();
        new SignUp().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }


    private void initLocation() {
        MyLocationListener.getInstance((LocationManager) getSystemService(LOCATION_SERVICE));
    }

    private void initRes(){
        faSearchDeactive = new IconDrawable(this, Iconify.IconValue.fa_search).colorRes(R.color.deactive).actionBarSize();
        faSearchActive = new IconDrawable(this, Iconify.IconValue.fa_search).colorRes(R.color.active).actionBarSize();
        faUserDeactive = new IconDrawable(this, Iconify.IconValue.fa_user).colorRes(R.color.deactive).actionBarSize();
        faUserActive = new IconDrawable(this, Iconify.IconValue.fa_user).colorRes(R.color.active).actionBarSize();
        faBellODeactive = new IconDrawable(this, Iconify.IconValue.fa_bell_o).colorRes(R.color.deactive).actionBarSize();
        faBellOActive = new IconDrawable(this, Iconify.IconValue.fa_bell_o).colorRes(R.color.active).actionBarSize();
        faEllipsisHDeactive = new IconDrawable(this, Iconify.IconValue.fa_ellipsis_h).colorRes(R.color.deactive).actionBarSize();
        faEllipsisHActive = new IconDrawable(this, Iconify.IconValue.fa_ellipsis_h).colorRes(R.color.active).actionBarSize();
        faAddDeactive = new IconDrawable(this, Iconify.IconValue.fa_plus_circle).colorRes(R.color.active).actionBarSize();
        faAddActive = new IconDrawable(this, Iconify.IconValue.fa_plus_circle).colorRes(R.color.active).actionBarSize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        this.setDeactiveIcons();
        this.menu.findItem(R.id.search).setIcon(faSearchActive);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setDeactiveIcons() {
        getActionBar().setIcon(android.R.color.transparent);
        this.menu.findItem(R.id.search).setIcon(faSearchDeactive);
        this.menu.findItem(R.id.me).setIcon(faUserDeactive);
        this.menu.findItem(R.id.notifications).setIcon(faBellODeactive);
        this.menu.findItem(R.id.more).setIcon(faEllipsisHDeactive);
        this.menu.findItem(R.id.add).setIcon(faAddDeactive);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //I don't want to change the others icon
        if (item.getItemId() == R.id.add) {
            this.createQuestion();
            return true;
        }

        this.setDeactiveIcons();
        //item.getIcon().setC
        switch(item.getItemId()) {
            case R.id.search:
                item.setIcon(faSearchActive);
                this.setSearchTabActive();
                return true;
            case R.id.me:
                item.setIcon(faUserActive);
                this.setMeTabActive();
                return true;
            case R.id.notifications:
                item.setIcon(faBellOActive);
                this.setNotificationTabActive();
                return true;
            case R.id.more:
                item.setIcon(faEllipsisHActive);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSearchTabActive() {
        this.setFragment(new SearchFragment());
    }

    private void setMeTabActive() {
        this.setFragment(new MeFragment());
    }

    private void setNotificationTabActive() {
        this.setFragment(new NotificationsFragment());
    }

    private void createQuestion() {
        Intent intent = new Intent(this, CreateQuestionActivity.class);
        startActivityForResult(intent, CREATE_QUESTION_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_QUESTION_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Question Created", Toast.LENGTH_SHORT).show();

            if (currentFragment != null) {
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MAINACTIVITY", requestCode + " " + resultCode);

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();

        currentFragment = fragment;
    }

    private class SignUp extends AsyncTask<Void,Void,String> {

        private SignUp() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String userId = PropertyAccessor.getUserId();
            if (userId == null || userId.length() == 0) {
                return Api.createUser();
            } else {
                return userId;
            }
        }

        @Override
        protected void onPostExecute(String userId) {
            super.onPostExecute(userId);
            if (userId != null && userId.length() > 0) {
                PropertyAccessor.setUserId(userId);
                MainActivity.this.setSearchTabActive();
                initNotification();
                //Toast toast = Toast.makeText(MainActivity.this, "Signed up", Toast.LENGTH_SHORT);
                //toast.show();
            } else {
                //
                Toast toast = Toast.makeText(MainActivity.this, "Error login in", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    // ANDROID NOTIFICATIONS
    // TODO create a service or something for this...

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    private String SENDER_ID = "291110698380";
    private GoogleCloudMessaging gcm;
    private void initNotification() {
        // Check device for Play Services APK.
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            gcm = GoogleCloudMessaging.getInstance(this);
            String regid = PropertyAccessor.getRegistrationId();

            Log.i(TAG, "Your regId:"+ regid);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new RegisterGCM().execute(null, null, null);
    }

    private class RegisterGCM extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }
                String regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                sendRegistrationIdToBackend(regid);

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the registration ID - no need to register again.
                PropertyAccessor.setRegistrationId(regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.i(TAG, "GCM Registered");
        }

    }

    private void sendRegistrationIdToBackend(String regId){
        new RegisterGCMBackend(regId).execute();
    }


    private class RegisterGCMBackend extends AsyncTask<Void, Void, Void> {
        String token;

        private RegisterGCMBackend(String token) {
            this.token = token;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Api.addGCMToken(PropertyAccessor.getUserId(), token);
            Log.i(TAG, "GCM Registered Backend " + token);
            return null;
        }
    }

}
