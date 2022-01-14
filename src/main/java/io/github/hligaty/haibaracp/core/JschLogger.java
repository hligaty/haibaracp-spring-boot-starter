package io.github.hligaty.haibaracp.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Logger;

public class JschLogger implements Logger {
  private static final Log LOGGER = LogFactory.getLog("com.jcraft.jsch");
  private final boolean enabled;

  public JschLogger(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled(int level) {
    if (!enabled) {
      return false;
    }
    switch (level) {
      case Logger.INFO:
        return LOGGER.isInfoEnabled();
      case Logger.WARN:
        return LOGGER.isWarnEnabled();
      case Logger.DEBUG:
        return LOGGER.isDebugEnabled();
      case Logger.ERROR:
        return LOGGER.isErrorEnabled();
      case Logger.FATAL:
        return LOGGER.isFatalEnabled();
      default:
        return false;
    }
  }

  public void log(int level, String message) {
    switch (level) {
      case Logger.INFO:
        LOGGER.info(message);
        break;
      case Logger.WARN:
        LOGGER.warn(message);
        break;
      case Logger.DEBUG:
        LOGGER.debug(message);
        break;
      case Logger.ERROR:
        LOGGER.error(message);
        break;
      case Logger.FATAL:
        LOGGER.fatal(message);
        break;
      default:
        break;
    }
  }

}
