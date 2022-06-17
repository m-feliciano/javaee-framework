package infra.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	final Logger logger = LoggerFactory.getLogger(CustomRuntimeException.class);

	public CustomRuntimeException(String msg) {
		super(msg);
		logger.error(msg);
	}
}
