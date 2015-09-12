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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Notification;
import com.huhapp.android.common.activities.SampleActivityBase;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.util.MyLocationListener;
import com.huhapp.android.util.NotificationUpdateListener;
import com.huhapp.android.util.PropertyAccessor;
import com.huhapp.android.welcome.WelcomeActivity;

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
public class MainActivity extends SampleActivityBase implements ImageButton.OnClickListener, NotificationUpdateListener {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static final String TAG = "MainActivity";

    private Fragment currentFragment;
    private boolean firstTime = false;
    private boolean noFragment = true;

    public static final int CREATE_QUESTION_CODE = 1816;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First time
        if (PropertyAccessor.getUsername() == null || PropertyAccessor.getUsername().trim().length() == 0) {
            Log.i("INIT", "FIRST TIME");
            startActivity(new Intent(this, WelcomeActivity.class));
            firstTime = true;
        }

        this.initLocation();
        new SignUp().execute();

        ImageButton search = (ImageButton) findViewById(R.id.search);
        search.setOnClickListener(this);
        search.setBackgroundColor(getResources().getColor(R.color.buttonTabActiveColor));
        ImageButton me = (ImageButton) findViewById(R.id.me);
        me.setOnClickListener(this);
        ImageButton add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(this);
        ImageButton notifications = (ImageButton) findViewById(R.id.notifications);
        notifications.setOnClickListener(this);
        ImageButton more = (ImageButton) findViewById(R.id.more);
        more.setOnClickListener(this);

        findViewById(R.id.notificationBadge).setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GcmIntentService.NOTIFICATION_RECEIVED);
        registerReceiver(receiver, filter);
        checkPlayServices();

        if (this.noFragment) {
            this.setSearchTabActive();
        }
    }

    private void initLocation() {
        MyLocationListener.getInstance((LocationManager) getSystemService(LOCATION_SERVICE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void resetTabColors(){
        ImageButton search = (ImageButton) findViewById(R.id.search);
        ImageButton me = (ImageButton) findViewById(R.id.me);
        ImageButton more = (ImageButton) findViewById(R.id.more);
        ImageButton notifications = (ImageButton) findViewById(R.id.notifications);
        search.setBackgroundColor(getResources().getColor(R.color.buttonTabColor));
        me.setBackgroundColor(getResources().getColor(R.color.buttonTabColor));
        more.setBackgroundColor(getResources().getColor(R.color.buttonTabColor));
        notifications.setBackgroundColor(getResources().getColor(R.color.buttonTabColor));
    }

    @Override
    public void onClick(View v) {
        //I don't want to change the others icon
        if (v.getId() == R.id.add) {
            this.createQuestion();
            return;
        }
        this.resetTabColors();
        v.setBackgroundColor(getResources().getColor(R.color.buttonTabActiveColor));
        switch(v.getId()) {
            case R.id.search:
                //item.setIcon(faSearchActive);
                this.setSearchTabActive();
                return;
            case R.id.me:
                //item.setIcon(faUserActive);
                this.setMeTabActive();
                return;
            case R.id.notifications:
                //item.setIcon(faBellOActive);
                this.setNotificationTabActive();
                return;
            case R.id.more:
                //item.setIcon(faEllipsisHActive);
                this.setSettingsTabActive();
                return;
        }
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

    private void setSettingsTabActive() {
        this.setFragment(new SettingsFragment());
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
        noFragment = false;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();

        currentFragment = fragment;
    }

    private class SignUp extends AsyncTask<Void,Void,Void> {

        private SignUp() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //this will create a user
            Api.testToken();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (PropertyAccessor.getUsername() != null && PropertyAccessor.getUsername().length() > 0) {
                if (!firstTime) {
                    MainActivity.this.setSearchTabActive();
                }
                initNotification();
                new UpdateNotificationNumberFromServer().execute();
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
            Api.addGCMToken(token);
            Log.i(TAG, "GCM Registered Backend " + token);
            return null;
        }
    }


    private class UpdateNotificationNumberFromServer extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int numberUnread = 0;

            List<Notification> notifications = Api.notificationList();

            for(Notification notification: notifications) {
                if (!notification.isRead()) {
                    numberUnread ++;
                }
            }

            return numberUnread;
        }

        @Override
        protected void onPostExecute(Integer numberUnread) {
            TextView textView = (TextView) findViewById(R.id.notificationBadge);
            if (numberUnread > 0) {
                textView.setText(numberUnread + "");
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.INVISIBLE);
            }

            super.onPostExecute(numberUnread);
        }
    }


    @Override
    public void notificationUpdated() {
        new UpdateNotificationNumberFromServer().execute();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notificationUpdated();
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }
}
