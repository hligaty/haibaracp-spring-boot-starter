package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.Session;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.springframework.beans.factory.DisposableBean;

/**
 * {@link SftpClient} factory, used for custom creation {@link com.jcraft.jsch.Session}
 *
 * @author hligaty
 */
public class SftpClientFactory implements DisposableBean {

    private final SftpClientProvider sftpClientProvider;
    
    private final JschSessionFactory jschSessionFactory;

    public SftpClientFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        this(clientProperties, poolProperties, new JschSessionFactory());
    }

    public SftpClientFactory(ClientProperties clientProperties, PoolProperties poolProperties, JschSessionFactory jschSessionFactory) {
        this.sftpClientProvider = new SftpClientProvider(() -> getSftpClient(clientProperties), poolProperties);
        this.jschSessionFactory = jschSessionFactory;
    }
    
    SftpClient getSftpClient(ClientProperties clientProperties) {
        return SftpClient.create(clientProperties, sftpClientProvider, jschSessionFactory);
    }
    
    public SftpClient getSftpClient() {
        return sftpClientProvider.getSftpClient();
    }

    @Override
    public void destroy() {
        sftpClientProvider.destroy();
    }

}
