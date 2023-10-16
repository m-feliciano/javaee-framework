package com.dev.servlet.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CurrencyFormatter {

	private CurrencyFormatter() {
	}

	/**
	 * This method is used to format a string to a currency value. The value is
	 * formatted to 2 decimal places. The value is rounded to the nearest integer.
	 * The value is formatted to a currency format.
	 *
	 * @param value
	 * @return String with currency format
	 */

	public static BigDecimal stringToBigDecimal(String value) {
		return new BigDecimal(value.replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Formats a currency value to a string.
	 *
	 * @param value The value to format.
	 * @return The formatted value.
	 */

	public static String bigDecimalToString(BigDecimal value) {
		return value.toString().replace('.', ',');
	}

}
