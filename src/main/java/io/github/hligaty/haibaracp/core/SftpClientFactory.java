package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;

/**
 * {@link SftpClient} factory, used for custom creation {@link com.jcraft.jsch.Session}
 *
 * @author hligaty
 */
public interface SftpClientFactory {

    /**
     * Provides a {@link SftpClient} for interacting with Sftp.
     * 
     * @return SftpClient
     * @see SftpClient#createJschSession(ClientProperties)
     */
    SftpClient getSftpClient();
    
}
