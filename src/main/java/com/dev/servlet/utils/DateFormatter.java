package com.dev.servlet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatter {

    private DateFormatter() {
    }

    /**
     * @param strDate - the string yyyy-MM-dd
     * @return Date
     */
    public static Date from(String strDate) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot convert data from: " + strDate);
        }
        return date;
    }
}
