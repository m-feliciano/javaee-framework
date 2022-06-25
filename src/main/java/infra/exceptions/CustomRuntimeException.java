package infra.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.io.Serial;

public class CustomRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    @Transient
    final Logger logger = LoggerFactory.getLogger(CustomRuntimeException.class);

    public CustomRuntimeException(String msg) {
        super(msg);
        logger.error(msg);
    }
}
