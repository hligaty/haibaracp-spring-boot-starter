package io.github.hligaty.haibaracp.core;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

import java.io.Serial;

/**
 * Exception thrown when there are issues with a connect pool.
 *
 * @author hligaty
 */
public class PoolException extends NestedRuntimeException {

    @Serial
    private static final long serialVersionUID = -2408042971611986977L;

    public PoolException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
