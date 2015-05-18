package com.huhapp.android;

import com.huhapp.android.huhapp.R;
import com.huhapp.android.util.PropertyAccessor;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by igbopie on 5/4/15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/VarelaRound-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        PropertyAccessor.init(getApplicationContext());
    }
}
