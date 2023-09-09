package io.github.hligaty.haibaracp.core;

import org.springframework.lang.Nullable;

/**
 * Callback interface for Session 'low level' code. To be used with {@link SftpTemplate} execution methods, often as
 * anonymous classes within a method implementation. Usually, used for chaining several operations of {@link SftpSession}
 * or its subclasses together ( {@code jschSession/channelSftp etc...}.
 *
 * @param <T> the result type
 * @author hligaty
 * @see SftpTemplate
 */
@FunctionalInterface
public interface SessionCallback<S extends SftpSession, T> {

    /**
     * Gets called by {@link SftpTemplate} with an active Sftp session. Does not need to care about activating or
     * closing the connection or handling exceptions.
     *
     * @param sftpSession active Sftp session.
     * @return a result object or null if none.
     * @throws Exception a sftp session exception during remote interaction.
     */
    @Nullable
    T doInSession(S sftpSession) throws Exception;
    
}
