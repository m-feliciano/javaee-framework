package utils;

import com.mchange.util.AssertException;
import exceptions.CustomRuntimeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatter {

    private DateFormatter() {
        throw new AssertException("DateFormatter is a utility class and should not be instantiated");
    }

    /**
     * This method is used to format a date to a string.
     * The date is formatted to a string.
     *
     * @param date
     * @return String with date format
     */

    public static Date From(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new CustomRuntimeException("Cannot convert data from: " + date);
        }
        return date;
    }

}
