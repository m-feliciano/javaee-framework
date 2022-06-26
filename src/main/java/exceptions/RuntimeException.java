package exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.io.Serial;

public class RuntimeException extends java.lang.RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    @Transient
    private final Logger logger = LoggerFactory.getLogger(RuntimeException.class);

    public RuntimeException(String msg) {
        super(msg);
        logger.error("Error: {}", msg);
    }
}
