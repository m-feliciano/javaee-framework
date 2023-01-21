package utils;

import com.mchange.util.AssertException;
import exceptions.RuntimeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatter {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private DateFormatter() {
        throw new AssertException("DateFormatter is a utility class and should not be instantiated");
    }

    /**
     * This method is used get a date from a string.
     * The date is formatted to a string.
     *
     * @param strDate
     * @return String with date format
     */

    public static Date From(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot convert data from: " + strDate);
        }
        return date;
    }

}
