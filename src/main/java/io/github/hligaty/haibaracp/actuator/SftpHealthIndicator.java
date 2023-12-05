/*
 * Copyright 2021-2023 hligaty
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
