package com.huhapp.android.util;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by igbopie on 4/12/15.
 */
public class DateUtil {
    public static CharSequence getDateInMillis(Date date) {
        return DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS);
    }
}
