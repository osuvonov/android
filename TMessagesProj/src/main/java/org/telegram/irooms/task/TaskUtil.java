package org.telegram.irooms.task;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskUtil {

    static String utcIsoFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static String getEndOfTheDay() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        calendar.set(year, month, day, 18, 00, 00);

        DateFormat df = new SimpleDateFormat(utcIsoFormat);

        return df.format(calendar.getTime());
    }

    public static String getEndOfTomorrow() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int tomorrow = calendar.get(Calendar.DATE) + 1;

        calendar.set(year, month, tomorrow, 18, 00, 00);

        DateFormat df = new SimpleDateFormat(utcIsoFormat);

        return df.format(calendar.getTime());
    }

    public static String getISODate(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(utcIsoFormat);

        return df.format(date);
    }

    public static Date getDateFromISO(String dateString) {
        try {
            if (dateString == null || dateString.equals("")) {
                return null;
            }
            return ISO8601Utils.parse(dateString, new ParsePosition(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedStringFromISO(String iso) {
        if (iso == null || iso.equals("null") || iso.equals("")) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateFromISO(iso));
        return android.text.format.DateFormat.format("MMM", calendar.getTime()).toString() + " " + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getMaxDate() {

        return null;
//        Calendar calendar = Calendar.getInstance();
//
//        calendar.set(9998, 12, 31, 23, 59, 59);
//
//        DateFormat df = new SimpleDateFormat(utcIsoFormat);
//
//        return df.format(calendar.getTime());
    }

}
