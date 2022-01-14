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

  public PoolException(@Nullable String msg, @Nullable Throwable cause) {
    super(msg, cause);
  }
}
