package io.github.hligaty.haibaracp.core;

import java.util.function.Supplier;

/**
 * {@link SftpSessionProvider} implementation for a standalone Sftp setup.
 *
 * @author hligaty
 */
class StandaloneSftpSessionProvider implements SftpSessionProvider {

    private final Supplier<SftpSession> sftpSessionSupplier;

    StandaloneSftpSessionProvider(Supplier<SftpSession> sftpSessionSupplier) {
        this.sftpSessionSupplier = sftpSessionSupplier;
    }

    @Override
    public SftpSession getSftpClient() {
        SftpSession sftpSession = sftpSessionSupplier.get();
        sftpSession.setSftpClientProvider(this);
        return sftpSession;
    }

}
