package com.huhapp.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by igbopie on 5/17/15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("GcmBroadcastReceiver", "onReceive");
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        if (startWakefulService(context, (intent.setComponent(comp))) == null){
            Log.e("GcmBroadcastReceiver", "ComponentName init failed");
        }
        setResultCode(Activity.RESULT_OK);
    }
}