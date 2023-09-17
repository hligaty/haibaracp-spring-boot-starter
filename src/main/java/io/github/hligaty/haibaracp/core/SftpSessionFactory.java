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
import org.springframework.beans.factory.DisposableBean;

/**
 * {@link SftpSession} factory, used for custom creation {@link SftpSession}
 *
 * @author hligaty
 * @see SftpSession
 */
public class SftpSessionFactory implements DisposableBean {

    private final SftpSessionProvider sftpSessionProvider;

    public SftpSessionFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        this.sftpSessionProvider = new SftpSessionProvider(() -> getSftpSession(clientProperties), poolProperties);
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
        return sftpSessionProvider.getSftpClient();
    }

    @Override
    public void destroy() {
        sftpSessionProvider.destroy();
    }

}
