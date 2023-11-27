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

package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.ClassUtils;

import java.util.Optional;

/**
 * {@link SftpSession} factory, used for custom creation {@link SftpSession}
 *
 * @author hligaty
 * @see SftpSession
 */
public class SftpSessionFactory implements DisposableBean {

    private static final Log log = LogFactory.getLog(SftpSessionFactory.class);

    private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool",
            SftpSessionFactory.class.getClassLoader());

    private final SftpSessionProvider sftpSessionProvider;

    public SftpSessionFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        SftpSessionProvider sessionProvider = new StandaloneSftpSessionProvider(() -> getSftpSession(clientProperties));
        this.sftpSessionProvider = Optional.ofNullable(poolProperties.getEnabled()).orElse(COMMONS_POOL2_AVAILABLE)
                ? new PoolingSftpSessionProvider(sessionProvider, poolProperties)
                : sessionProvider;
    }

    /**
     * Obtain a SftpSession
     *
     * @param clientProperties sftp session property
     * @return sftp session
     */
    public SftpSession getSftpSession(ClientProperties clientProperties) {
        return new SftpSession(clientProperties);
    }

    SftpSession getSftpSession() {
        SftpSession sftpClient = sftpSessionProvider.getSftpClient();
        sftpClient.setSftpClientProvider(sftpSessionProvider);
        return sftpClient;
    }

    @Override
    public void destroy() {
        if (sftpSessionProvider instanceof DisposableBean) {
            try {
                ((DisposableBean) sftpSessionProvider).destroy();
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn(sftpSessionProvider + " did not shut down gracefully.", e);
                }
            }
        }
    }

}
