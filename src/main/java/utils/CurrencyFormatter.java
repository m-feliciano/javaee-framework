package utils;

import com.mchange.util.AssertException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CurrencyFormatter {

	private CurrencyFormatter() {
		throw new AssertException("This class must not be instantiated.");
	}

	public static BigDecimal stringToBigDecimal(String value) {
		return new BigDecimal(value.replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
	}

	public static String bigDecimalToString(BigDecimal value) {
		return value.toString().replace('.', ',');
	}

}
