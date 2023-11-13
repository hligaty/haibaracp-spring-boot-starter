package io.github.hligaty.haibaracp.core;

/**
 * Used internally by Haibaracp. Cannot be used directly in application code.
 *
 * @author hligaty
 */
public final class SftpSessionUtils {

    private SftpSessionUtils() {
    }

    public static SftpSession getSession(SftpSessionFactory factory) {
        return factory.getSftpSession();
    }

    public static void releaseSession(SftpSession session) {
        session.release();
    }

    public static boolean testSession(SftpSession session) {
        return session.test();
    }
}
