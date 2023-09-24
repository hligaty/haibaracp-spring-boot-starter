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

package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import io.github.hligaty.haibaracp.config.ClientProperties;
import org.springframework.lang.NonNull;

/**
 * A session to a sftp server. You can override methods {@link #createJschSession(ClientProperties)},
 * {@link #test()}, and {@link #reset()} to modify or obtain capabilities beyond sftp.
 *
 * @author hligaty
 */
public class SftpSession {
    private final ChannelSftp channelSftp;
    private final Session session;
    /**
     * The original directory when the connection was established.
     */
    private final String originalDir;

    private SftpSessionProvider sftpSessionProvider;

    static {
        // disable Kerberos
        JSch.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
    }

    public SftpSession(ClientProperties clientProperties) {
        try {
            session = createJschSession(clientProperties);
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            originalDir = channelSftp.pwd();
        } catch (Exception e) {
            disconnect();
            throw new IllegalStateException("Failed to create sftp Client", e);
        }
    }

    /**
     * Create a Jsch Session. Subclasses can override this method to define the creation process of JschSession and
     * create more types of channels(
     * {@code channelExec etc....}.
     *
     * @param clientProperties properties for sftp.
     * @return {@link com.jcraft.jsch.Session}
     * @throws Exception an exception during create session.
     */
    @NonNull
    protected Session createJschSession(ClientProperties clientProperties) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(clientProperties.getUsername(), clientProperties.getHost(), clientProperties.getPort());
        if (clientProperties.isStrictHostKeyChecking()) {
            session.setConfig("StrictHostKeyChecking", "ask");
            session.setUserInfo(new UserInfoImpl(clientProperties.getPassword()));
            jsch.addIdentity(clientProperties.getKeyPath());
        } else {
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(clientProperties.getPassword());
        }
        if (clientProperties.getKex() != null) {
            session.setConfig("kex", clientProperties.getKex());
        }
        session.connect(clientProperties.getConnectTimeout());
        return session;
    }

    private static class UserInfoImpl implements UserInfo {
        private final String passphrase;

        public UserInfoImpl(String passphrase) {
            this.passphrase = passphrase;
        }

        @Override
        public String getPassphrase() {
            return passphrase;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassword(String s) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return true;
        }

        @Override
        public boolean promptYesNo(String s) {
            return true;
        }

        @Override
        public void showMessage(String s) {
        }
    }

    void setSftpClientProvider(SftpSessionProvider sftpSessionProvider) {
        this.sftpSessionProvider = sftpSessionProvider;
    }

    /**
     * Test connect. Subclasses can override this method to define more validations.
     *
     * @return true if connect available.
     */
    protected boolean test() {
        try {
            if (channelSftp.isConnected() && originalDir.equals(channelSftp.pwd())) {
                channelSftp.lstat(originalDir);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Reset connect. Subclasses can override this method to define more resets.
     *
     * @return true If the reset is successful.
     */
    protected boolean reset() {
        try {
            channelSftp.cd(originalDir);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    void release() {
        try {
            if (!reset()) {
                disconnect();
            }
        } finally {
            sftpSessionProvider.release(this);
        }
    }

    /**
     * @return jsch session
     */
    public Session jschSession() {
        return session;
    }

    /**
     * @return jsch channelSftp
     */
    public ChannelSftp channelSftp() {
        return channelSftp;
    }

}
