package com.dev.servlet.core.util;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class DateUtil {
    public static final String DD_MM_MYYYY = "yyyy-MM-dd";

    public static BigDecimal parseCurrency(String value) {
        if (value == null) return null;
        return new BigDecimal(value.replace(',', '.'))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static String toString(BigDecimal value) {
        if (value == null) return null;
        return value.toString().replace('.', ',');
    }

    public static Date toDateInitial(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date toDateFinal(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date toDateInitial(String value) {
        return toDateInitial(value, "dd/MM/yyyy");
    }

    public static Date toDateInitial(String value, String format) {
        if (value == null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDateFinal(String value, String format) {
        Date date = toDateInitial(value, format);
        return new Date(date.getTime() + 86399000L);
    }

    public static String getTodayEndDateStringYYYYMMDD() {
        Date dateFinal = toDateFinal(new Date());
        return new SimpleDateFormat(DD_MM_MYYYY).format(dateFinal.getTime());
    }

    public static String getTodayStartDateStringYYYYMMDD() {
        Date dateInitial = toDateInitial(new Date());
        return new SimpleDateFormat(DD_MM_MYYYY).format(dateInitial.getTime());
    }
}
