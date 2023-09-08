package io.github.hligaty.haibaracp.core;

/**
 * @author hligaty
 */
@FunctionalInterface
public interface SessionCallbackWithoutResult<S extends SftpSession> {

    void doInSession(S sftpSession);
}
