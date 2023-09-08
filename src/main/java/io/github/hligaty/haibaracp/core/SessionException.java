package io.github.hligaty.haibaracp.core;

import org.springframework.core.NestedRuntimeException;

import java.io.Serial;

/**
 * @author hligaty
 */
public class SessionException extends NestedRuntimeException {
    
    @Serial
    private static final long serialVersionUID = 8110370897654903831L;

    public SessionException(String msg) {
        super(msg);
    }

    public SessionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
