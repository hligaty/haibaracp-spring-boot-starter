/*
 * Copyright 2021-2023 hligaty
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

package io.github.hligaty.haibaracp.config;

import com.jcraft.jsch.JSch;
import io.github.hligaty.haibaracp.core.JschLogger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.StringJoiner;

/**
 * Configuration properties for SFTP.
 *
 * @author hligaty
 */
@ConfigurationProperties("sftp")
public class ClientProperties {
    /**
     * SFTP server host.
     */
    private String host = "localhost";
    /**
     * SFTP server port.
     */
    private int port = 22;
    /**
     * Login username of the sftp server.
     */
    private String username;
    /**
     * Whether to enable host key login.
     */
    private boolean strictHostKeyChecking = false;
    /**
     * host key.
     */
    private String keyPath;
    /**
     * Login password or host key passphrase of the sftp server.
     */
    private String password = "";
    /**
     * Specifies the timeout period for new session creation, in milliseconds.
     */
    private int connectTimeout = 0;
    /**
     * SSH kex algorithms.
     */
    private String kex;
    /**
     * Enable jsch log, Cannot be individually turned on or off for one of multiple hosts.
     */
    private boolean enabledLog = false;

    /**
     * Jsch extensions properties
     */
    private Map<String, Object> extensions;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public void setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKex() {
        return kex;
    }

    public void setKex(String kex) {
        this.kex = kex;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ClientProperties.class.getSimpleName() + "[", "]")
                .add("host='" + host + "'")
                .add("port=" + port)
                .add("username='" + username + "'")
                .add("strictHostKeyChecking=" + strictHostKeyChecking)
                .add("keyPath='" + keyPath + "'")
                .add("password='" + password + "'")
                .add("connectTimeout=" + connectTimeout)
                .add("kex='" + kex + "'")
                .add("enabledLog=" + enabledLog)
                .add("extensions=" + extensions)
                .toString();
    }

    public boolean isEnabledLog() {
        return enabledLog;
    }

    public void setEnabledLog(boolean enabledLog) {
        this.enabledLog = enabledLog;
        JSch.setLogger(new JschLogger(enabledLog));
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
}
