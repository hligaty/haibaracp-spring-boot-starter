package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.springframework.beans.factory.DisposableBean;

/**
 * {@link SftpSession} factory, used for custom creation {@link com.jcraft.jsch.Session}
 *
 * @author hligaty
 */
public class SftpSessionFactory implements DisposableBean {

    private final SftpSessionProvider sftpSessionProvider;

    public SftpSessionFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        this.sftpSessionProvider = new SftpSessionProvider(() -> getSftpSession(clientProperties), poolProperties);
    }

    public SftpSession getSftpSession(ClientProperties clientProperties) {
        return new SftpSession(clientProperties);
    }

    SftpSession getSftpSession() {
        return sftpSessionProvider.getSftpClient();
    }

    @Override
    public void destroy() {
        sftpSessionProvider.destroy();
    }

}
