package io.github.hligaty.haibaracp.core;

import org.springframework.core.NestedRuntimeException;

/**
 * Exception to be thrown on session operation failure. This could have different
 * causes depending on the Jsch API in use but most likely thrown after the
 * session interrupts.
 * <p> 
 * As this class is a runtime exception, there is no need for user code to catch
 * it or subclasses if any error is to be considered fatal (the usual case).
 * 
 * @author hligaty
 */
@SuppressWarnings("serial")
public class SessionException extends NestedRuntimeException {

    /**
     * Constructs a new SessionException instance.
     *
     * @param msg the detail message.
     */
    public SessionException(String msg) {
        super(msg);
    }

    /**
     * Constructor for SessionException.
     * @param msg the detail message
     * @param cause the root cause from the data access API in use
     */
    public SessionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
