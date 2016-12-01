package core.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import core.model.ReportingPeriod;

public class DateUtils
{
    public static Date deserialize(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException exp) {
//            System.err.println("Failed to parse Date:", exp);
            return null;
        }
    }

    public static Date deserializeWithTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException exp) {
//            System.err.println("Failed to parse Date:", exp);
            return null;
        }
    }


//    public static Date getDateWithoutTime(Date date) {
//        // Get Calendar object set to the date and time of the given Date object
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//
//        // Set time fields to zero
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//
//        // Put it back in the Date object
//        return cal.getTime();
//    }


    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);  // number of days to add
        return c.getTime();
    }


    public static Date substractDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -days);  // number of days to add
        return c.getTime();
    }


    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sdf.format(date);
    }

//    public static Date stringToDate(String strDate) {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            return df.parse(strDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    //24.02.15 - day/month/year
    public static String dateToDotsString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String dateToTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mma", Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String dateToDayString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String dateToDayOfWeekString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String dateToMonthString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String dateToYearString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }


    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }


    public static String getFirstDateOfWeek(Date date) {
        Locale l = new Locale("en", "US");
        Calendar c = GregorianCalendar.getInstance(l);
        c.setTime(date);
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DATE, -2);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", l);
        return df.format(c.getTime());
    }


    public static String getLastDateOfWeek(Date date) {
        Locale l = new Locale("en", "US");
        Calendar c = GregorianCalendar.getInstance(l);
        c.setTime(date);
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DATE, 5);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", l);
        return df.format(c.getTime());
    }


    public static List<ReportingPeriod> getDatesInRange(Date start, Date finish) {
        if (start != null && finish != null) {
            Locale l = new Locale("en", "US");

            List<ReportingPeriod> list = new ArrayList<>();

            while (start.before(finish)) {
                Calendar c = GregorianCalendar.getInstance(l);
                c.setTime(start);
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                c.add(Calendar.DATE, 4);
                Date weekFinish = c.getTime();
                ReportingPeriod period = new ReportingPeriod();
                period.setStart(start);
                period.setFinish(weekFinish);
                list.add(period);

                c.add(Calendar.DATE, 3);
                Date nextWeek = c.getTime();

                //increase start
                start = nextWeek;
            }
            
            return list;
        }

        return null;
    }


//    public static String getWeekDateRange(Date date) {
//        Calendar c = GregorianCalendar.getInstance();
//        c.setTime(date);
//        // Set the calendar to monday of the current week
//        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//
//        // Print dates of the current week starting on Monday
//        DateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
//        String startDate = "", endDate = "";
//
//        startDate = df.format(c.getTime());
//        c.add(Calendar.DATE, 5);
//        endDate = df.format(c.getTime());
//
//        return MessageFormat.format("{0} - {1}", startDate, endDate);
//    }


    public static String getWeekDateRange(Date start, Date finish) {
        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return MessageFormat.format("{0} - {1}", df.format(start), df.format(finish));
    }


    public static String getPeriodWeekDateRange(Date date) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(date);
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = "", endDate = "";

        startDate = df.format(c.getTime());
        c.add(Calendar.DATE, 5);
        endDate = df.format(c.getTime());

        return MessageFormat.format("{0} - {1}", startDate, endDate);
    }


    public static int calculateDifference(Date a, Date b) {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0) {
            earlier.setTime(a);
            later.setTime(b);
        } else {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
            tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        return difference;
    }
}