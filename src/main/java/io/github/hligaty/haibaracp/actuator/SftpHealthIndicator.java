package io.github.hligaty.haibaracp.actuator;

import io.github.hligaty.haibaracp.core.SftpSession;
import io.github.hligaty.haibaracp.core.SftpSessionFactory;
import io.github.hligaty.haibaracp.core.SftpSessionUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.Assert;

/**
 * Simple implementation of a HealthIndicator returning status information for Sftp file stores.
 *
 * @author hligaty
 */
public class SftpHealthIndicator extends AbstractHealthIndicator {

    private final SftpSessionFactory sftpSessionFactory;

    public SftpHealthIndicator(SftpSessionFactory sessionFactory) {
        super("Sftp health check failed");
        Assert.notNull(sessionFactory, "SessionFactory must not be null");
        this.sftpSessionFactory = sessionFactory;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        SftpSession session = SftpSessionUtils.getSession(sftpSessionFactory);
        try {
            boolean result = SftpSessionUtils.testSession(session);
            builder.status(result ? Status.UP : Status.DOWN);
        } finally {
            SftpSessionUtils.releaseSession(session);
        }
    }
}
