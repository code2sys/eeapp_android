package com.johnpepper.eeapp.util;

import com.johnpepper.eeapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by borysrosicky on 11/2/15.
 */
public class DateTimeUtil {

    private static final int SECOND = 1;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    private static final int WEEK = 7 * DAY;
    private static final int MONTH = 30 * DAY;

    /*
     * Date -> yyyyMMdd
     * Date -> MMdd
     * Date -> yyyy/MM/dd
     * Date -> dd/MM/yyyy
     */
    public static String dateToString(Date date, String strformat) {
        SimpleDateFormat format = new SimpleDateFormat(strformat);
        return format.format(date);
    }
    public static String dateToString(Date date, TimeZone tz)
            throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(tz);
        return sdf.format(date);
    }
    public static String dateToStringInUTC(Date date) throws ParseException{
        return dateToString(date, TimeZone.getTimeZone("UTC"));
    }
    public static Date twoPMOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 14);

        return new Date(calendar.getTimeInMillis());

    }
    public static Date lastSixMonthOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -6);

        return new Date(calendar.getTimeInMillis());

    }
    public static Date addOneDayToDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);

        return new Date(calendar.getTimeInMillis());
    }
    /*
     * yyyyMMdd -> Date
     * MMdd -> Date
     * yyyy/MM/dd -> Date
     * dd/MM/yyyy -> Date
     */
    public static Date stringToDate(String strDate, String strformat) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(strformat);
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

}
