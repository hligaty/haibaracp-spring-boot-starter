package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;

/**
 * {@link SftpCallback} with no result is returned.
 *
 * @author hligaty
 * @see SftpTemplate
 */
@FunctionalInterface
public interface SessionCallbackWithoutResult<S extends SftpSession> {

    /**
     * Gets called by {@link SftpTemplate} with an active Sftp session. Does not need to care about activating or
     * closing the connection or handling exceptions.
     *
     * @param sftpSession active Sftp session.
     * @throws SftpException a sftp exception during remote interaction.
     * @throws JSchException a sftp session exception during remote interaction.
     * @throws IOException a sftp session exception during remote interaction.
     */
    void doInSession(S sftpSession) throws SftpException, JSchException, IOException;

}
