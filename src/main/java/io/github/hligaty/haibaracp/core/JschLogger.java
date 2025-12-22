/*
 * Copyright 2021-2025 hligaty
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        log(level, message, null);
    }

    @Override
    public void log(int level, String message, Throwable cause) {
        switch (level) {
            case Logger.INFO -> log.info(message, null);
            case Logger.WARN -> log.warn(message, null);
            case Logger.DEBUG -> log.debug(message, null);
            case Logger.ERROR -> log.error(message, null);
            case Logger.FATAL -> log.fatal(message, null);
            default -> {
            }
        }
    }

}
