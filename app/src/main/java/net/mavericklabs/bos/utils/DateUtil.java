/*
 * Copyright (c) 2019. Maverick Labs
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as,
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package net.mavericklabs.bos.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtil {
    private static String longFormTZDateString = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static Date getDateFromString(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDateTimeFromString(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(longFormTZDateString, Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date, String strFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.UK);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String getTZDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(longFormTZDateString, Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.UK);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String formatDate(String startDateString, String endDateString) {
        Date startDate = getDateTimeFromString(startDateString);
        Date endDate = getDateTimeFromString(endDateString);
        String dayNumberSuffix = getDayOfMonthSuffix(startDate.getDate());
        String format = "E, logDebug'" + dayNumberSuffix + "' MMMM yyyy, h:mm a";
        String startDateTime = dateToString(startDate, format);
        String endDateTime = dateToString(endDate, "h:mm a");
        return startDateTime + " to " + endDateTime;
    }

    private static String getDayOfMonthSuffix(final int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }
}
