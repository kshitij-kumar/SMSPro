package com.kshitij.android.smspro.util;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kshitij.kumar on 30-06-2015.
 */

public class TimeFormatter {

    public static String getCustomisedTimeLabel(long longDate) {
        Date postDate = new Date(longDate);
        String label = "";

        if (longDate < System.currentTimeMillis()) {

            if (DateUtils.isToday(longDate)) {
                label = DateUtils
                        .getRelativeTimeSpanString(postDate.getTime(),
                                (System.currentTimeMillis()),
                                DateUtils.SECOND_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString();
            } else if (isYesterday(longDate)) {
                label = "Yesterday";
            } else {
                label = TimeFormatter.getDateFormatMddYYYYhhmma(longDate);
            }
            return label;
        } else {
            // find the time span for tomorrow
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd",
                    Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            @SuppressWarnings("deprecation")
            Date date = new Date(dateFormat.format(cal.getTime()));

            // compare feed timespan with tomorrow's timespan
            if ((longDate - System.currentTimeMillis()) <= date.getTime()
                    - System.currentTimeMillis()) {
                return DateUtils
                        .getRelativeTimeSpanString(postDate.getTime(),
                                (System.currentTimeMillis()),
                                DateUtils.SECOND_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString();
            } else {
                return TimeFormatter.getDateFormatMddYYYYhhmma(longDate);
            }

        }

    }

    public static boolean isYesterday(long when) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int millisec = cal.get(Calendar.MILLISECOND);
        long millisTillYesterday = millisec + (sec * 1000) + (min * 60 * 1000)
                + (hour * 60 * 60 * 1000);
        long yesterdayEnd = System.currentTimeMillis() - millisTillYesterday
                - (60 * 1000);
        long yesterdayStart = System.currentTimeMillis() - millisTillYesterday
                - (24 * 60 * 60 * 1000);

        if ((when >= yesterdayStart) && (when < yesterdayEnd))
            return true;
        else
            return false;
    }

    public static boolean isSameYear(long when) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(when);
        return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR);
    }

    public static String getDateFormatMddYYYYhhmma(long longDate) {

        SimpleDateFormat dateFormat = null;
        if (DateUtils.isToday(longDate)) {
            dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        } else if (isSameYear(longDate)) {
            dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.ENGLISH);
        } else {
            dateFormat = new SimpleDateFormat("MMM dd yyyy, hh:mm a", Locale.ENGLISH);
        }
        return dateFormat.format(new Date(longDate));
    }

}
