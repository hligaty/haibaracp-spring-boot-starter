package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.*;
import io.github.hligaty.haibaracp.autoconfig.SftpAutoConfiguration;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
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

    private final SftpClientProvider sftpClientProvider;

    static {
        // disable Kerberos
        JSch.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
    }

    private SftpClient(ClientProperties clientProperties, JschSessionFactory jschSessionFactory,
                       SftpClientProvider sftpClientProvider) {
        try {
            this.session = jschSessionFactory.get(clientProperties);
            Assert.notNull(session, "session is required; it must not be null");
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            originalDir = channelSftp.pwd();
            this.sftpClientProvider = sftpClientProvider;
        } catch (Exception e) {
            disconnect();
            throw new IllegalStateException("failed to create sftp Client", e);
        }
    }

    static SftpClient create(ClientProperties clientProperties, SftpClientProvider sftpClientProvider,
                             JschSessionFactory jschSessionFactory) {
        return new SftpClient(clientProperties, jschSessionFactory, sftpClientProvider);
    }

    /**
     * Disconnect the connection.
     */
    void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * Test connect.
     *
     * @return true if connect available.
     */
    boolean test() {
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
    boolean reset() {
        try {
            channelSftp.cd(originalDir);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    void release() {
        if (!reset()) {
            disconnect();
        }
        sftpClientProvider.release(this);
    }

    ChannelSftp getChannelSftp() {
        return channelSftp;
    }

}
