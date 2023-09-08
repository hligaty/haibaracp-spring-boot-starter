package io.github.hligaty.haibaracp.core;

/**
 * @author hligaty
 */
@FunctionalInterface
public interface SessionCallback<S extends SftpSession, T> {
    
    T doInSession(S sftpSession);
}
