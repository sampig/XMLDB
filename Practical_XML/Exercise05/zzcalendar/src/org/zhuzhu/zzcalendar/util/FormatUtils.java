/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Chenfeng Zhu
 *
 */
public class FormatUtils {

    public static enum DateType {
        YEAR, MONTH, DAY
    }

    /**
     * Check date format (yyyy-mm-dd).
     *
     * @param strDate
     * @return
     */
    public static boolean checkDateFormat(String strDate) {
        String strRegex = "[0-9]{4}-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(strDate);
        return matcher.matches();
    }

    /**
     * Check date format.
     * <ul>
     * <li>year: yyyy</li>
     * <li>month: m | mm</li>
     * <li>day: d | dd</li>
     * </ul>
     *
     * @param strDate
     * @param datetype
     * @return
     */
    public static boolean checkDateFormat(String strDate, DateType datetype) {
        String strRegex = "[0-9]{4}-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])";
        switch (datetype) {
        case YEAR:
            strRegex = "[0-9]{4}";
            break;
        case MONTH:
            strRegex = "([1-9]|1[0-2]|0[1-9])";
            break;
        case DAY:
            strRegex = "([1-9]|3[01]|[12][0-9]|0[1-9])";
            break;
        }
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(strDate);
        return matcher.matches();
    }

    /**
     * Check time format (hh:mi:ss).
     *
     * @param strTime
     * @return
     */
    public static boolean checkTimeFormat(String strTime) {
        String strRegex = "([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(strTime);
        return matcher.matches();
    }

    /**
     * Check duration format (hh:mi).
     *
     * @param strDuration
     * @return
     */
    public static boolean checkDurationFormat(String strDuration) {
        String strRegex = "([01][0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(strDuration);
        return matcher.matches();
    }

    /**
     * Check hour format (h or hh).
     *
     * @param strHour
     * @return
     */
    public static boolean checkHourFormat(String strHour) {
        String strRegex = "([0-9]|[01][0-9]|2[0-3])";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(strHour);
        return matcher.matches();
    }

    /**
     * Get JavaScript content.
     *
     * @param msg
     * @return
     */
    public static String getScript(String msg) {
        return "<script>alert('" + msg + "');</script>";
    }

}
