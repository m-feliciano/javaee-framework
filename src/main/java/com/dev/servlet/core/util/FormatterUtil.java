package com.dev.servlet.core.util;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class FormatterUtil {
    public static BigDecimal parseCurrency(String value) {
        if (value == null) return null;
        return new BigDecimal(value.replace(',', '.'))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static String toString(BigDecimal value) {
        if (value == null) return null;
        return value.toString().replace('.', ',');
    }

    public static Date toDate(String value) {
        if (value == null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}
