package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * {@link SftpCallback} with no result is returned.
 *
 * @author hligaty
 * @see SftpTemplate
 */
@FunctionalInterface
public interface SftpCallbackWithoutResult {
    /**
     * Gets called by {@link SftpTemplate} with an active Sftp connection. Does not need to care about activating or
     * closing the connection or handling exceptions.
     *
     * @param channelSftp active Sftp channel.
     * @throws SftpException a sftp exception during remote interaction.
     */
    void doInSftp(ChannelSftp channelSftp) throws SftpException;
}
