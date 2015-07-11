package com.huhapp.android.util;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by igbopie on 4/12/15.
 */
public class DateUtil {
    private final static double AVERAGE_MILLIS_PER_MONTH = 365.24 * 24 * 60 * 60 * 1000 / 12;

    public static CharSequence getDateInMillis(Date date) {
        /*return DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                new Date().getTime(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);*/
        return getTimeString(date);
    }

    //http://stackoverflow.com/questions/19409035/custom-format-for-relative-time-span
    public static String getTimeString(Date fromdate) {

        long then;
        then = fromdate.getTime();
        Date date = new Date(then);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int months = monthsBetween(calendar.getTime(), now.getTime());
        int weeks = weeksBetween(calendar.getTime(), now.getTime());
        int days = daysBetween(calendar.getTime(), now.getTime());
        int minutes = minuteBetween(calendar.getTime(), now.getTime());
        int hours = hoursBetween(calendar.getTime(), now.getTime());
        int second = minuteBetween(calendar.getTime(), now.getTime());

        if (second <= 10) {
            dateStr.append("Now");
        } else if (second > 10 && second <= 30) {
            dateStr.append("few seconds ago");
        } else if (second > 30 && minutes <= 0) {
            dateStr.append(second).append("s");
        } else if (minutes > 0 && hours <= 0) {
            dateStr.append(minutes).append("m");
        } else if (hours > 0 && days <= 0) {
            dateStr.append(hours).append("h");
        } else if (days > 0 && weeks <= 0) {
            dateStr.append(days).append("d");
        } else if (weeks > 0 && months <= 0) {
            dateStr.append(weeks).append("w");
        } else {
            dateStr.append(months).append("M");
        }
        return dateStr.toString();
    }

    public static int secondsBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
    }

    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.HOUR_IN_MILLIS);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    public static int weeksBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.WEEK_IN_MILLIS);
    }

    public static int monthsBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / AVERAGE_MILLIS_PER_MONTH);
    }
}
