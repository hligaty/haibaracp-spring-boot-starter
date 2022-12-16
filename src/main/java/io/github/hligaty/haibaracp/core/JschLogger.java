package io.github.hligaty.haibaracp.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Logger;

public class JschLogger implements Logger {
  private static final Log log = LogFactory.getLog("com.jcraft.jsch");
  private final boolean enabled;

  public JschLogger(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean isEnabled(int level) {
    if (!enabled) {
      return false;
    }
    return switch (level) {
      case Logger.INFO -> log.isInfoEnabled();
      case Logger.WARN -> log.isWarnEnabled();
      case Logger.DEBUG -> log.isDebugEnabled();
      case Logger.ERROR -> log.isErrorEnabled();
      case Logger.FATAL -> log.isFatalEnabled();
      default -> false;
    };
  }

  @Override
  public void log(int level, String message) {
    switch (level) {
      case Logger.INFO -> log.info(message);
      case Logger.WARN -> log.warn(message);
      case Logger.DEBUG -> log.debug(message);
      case Logger.ERROR -> log.error(message);
      case Logger.FATAL -> log.fatal(message);
      default -> {
      }
    }
  }

}
