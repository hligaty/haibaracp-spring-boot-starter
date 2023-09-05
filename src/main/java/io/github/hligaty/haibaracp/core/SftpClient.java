package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.*;
import io.github.hligaty.haibaracp.autoconfig.SftpAutoConfiguration;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Sftp connect object.
 *
 * @author hligaty
 */
public class SftpClient {
    private final ChannelSftp channelSftp;
    private final Session session;
    /**
     * The original directory when the connection was established.
     */
    private final String originalDir;

    static {
        // disable Kerberos
        JSch.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
    }

    public SftpClient(ClientProperties clientProperties) {
        try {
            this.session = createJschSession(clientProperties);
            Assert.notNull(session, "session is required; it must not be null");
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            originalDir = channelSftp.pwd();
        } catch (Exception e) {
            disconnect();
            throw new IllegalStateException("failed to create sftp Client", e);
        }
    }

    /**
     * Create a Jsch Session.
     * 
     * @param clientProperties properties for SFTP
     * @return {@link com.jcraft.jsch.Session}
     * @throws Exception an exception during create session.
     * @see SftpClientFactory
     * @see SftpAutoConfiguration#sftpPool(ClientProperties, PoolProperties, ObjectProvider)
     */
    @NonNull
    public Session createJschSession(ClientProperties clientProperties) throws Exception {
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
        if (StringUtils.hasText(clientProperties.getKex())) {
            session.setConfig("kex", clientProperties.getKex());
        }
        session.connect(clientProperties.getConnectTimeout());
        return session;
    }

    /**
     * Disconnect the connection.
     */
    protected final void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * Test connect.
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
     * Reset connect.
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

    public ChannelSftp getChannelSftp() {
        return channelSftp;
    }

    private record UserInfoImpl(String passphrase) implements UserInfo {

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
}
