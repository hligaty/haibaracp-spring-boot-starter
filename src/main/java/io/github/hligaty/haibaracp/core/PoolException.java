package io.github.hligaty.haibaracp.core;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * Exception thrown when there are issues with a connect pool.
 *
 * @author hligaty
 */
@SuppressWarnings("serial")
public class PoolException extends NestedRuntimeException {

    /**
     * Constructs a new SessionException instance.
     *
     * @param msg the detail message.
     * @param cause the nested exception.
     */
    public PoolException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
    
}
