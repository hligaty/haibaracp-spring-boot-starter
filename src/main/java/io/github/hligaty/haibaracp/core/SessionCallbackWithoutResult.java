package io.github.hligaty.haibaracp.core;

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
     * @throws Exception a sftp session exception during remote interaction.
     */
    void doInSession(S sftpSession) throws Exception;

}
