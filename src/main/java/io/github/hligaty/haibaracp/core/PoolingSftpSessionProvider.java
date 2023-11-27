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

import io.github.hligaty.haibaracp.config.PoolProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;

import java.time.Duration;

/**
 * {@link SftpSessionProvider} with connection pooling support.
 * <p>
 * Each allocated connection is tracked and to be returned into the pool which created the connection.
 *
 * @author hligaty
 */
class PoolingSftpSessionProvider implements SftpSessionProvider, DisposableBean {

    private static final Log log = LogFactory.getLog(PoolingSftpSessionProvider.class);

    private final GenericObjectPool<SftpSession> pool;

    PoolingSftpSessionProvider(SftpSessionProvider sftpSessionProvider, PoolProperties poolProperties) {
        this.pool = new GenericObjectPool<>(new SftpPooledObjectFactory() {
            @Override
            public SftpSession create() {
                SftpSession sftpSession = sftpSessionProvider.getSftpClient();
                sftpSession.setSftpClientProvider(PoolingSftpSessionProvider.this);
                return sftpSession;
            }
        }, getPoolConfig(poolProperties));
        log.info("HaibaraCP: Created");
    }

    @Override
    public SftpSession getSftpClient() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new PoolException("Could not get a resource from the pool", e);
        }
    }

    @Override
    public void release(SftpSession sftpSession) {
        try {
            pool.returnObject(sftpSession);
        } catch (Exception e) {
            throw new PoolException("Could not return a resource from the pool", e);
        }
    }

    @Override
    public void destroy() {
        try {
            pool.close();
            log.info("HaibaraCP: Closed");
        } catch (Exception e) {
            throw new PoolException("Could not destroy the pool", e);
        }
    }

    private abstract static class SftpPooledObjectFactory extends BasePooledObjectFactory<SftpSession> {

        @Override
        public PooledObject<SftpSession> wrap(SftpSession sftpSession) {
            return new DefaultPooledObject<>(sftpSession);
        }

        @Override
        public boolean validateObject(PooledObject<SftpSession> p) {
            return p.getObject().test();
        }

        @Override
        public void destroyObject(PooledObject<SftpSession> p) {
            p.getObject().disconnect();
        }

    }

    private GenericObjectPoolConfig<SftpSession> getPoolConfig(PoolProperties poolProperties) {
        GenericObjectPoolConfig<SftpSession> config = new GenericObjectPoolConfig<>();
        config.setMaxWait(Duration.ofMillis(poolProperties.getMaxWait()));
        config.setTestOnBorrow(poolProperties.isTestOnBorrow());
        config.setTestOnReturn(poolProperties.isTestOnReturn());
        config.setTestWhileIdle(poolProperties.isTestWhileIdle());
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(poolProperties.getTimeBetweenEvictionRuns()));
        config.setMinEvictableIdleTime(Duration.ofMillis(poolProperties.getMinEvictableIdleTimeMillis()));
        config.setMinIdle(poolProperties.getMinIdle());
        config.setMaxIdle(poolProperties.getMaxIdle());
        config.setMaxTotal(poolProperties.getMaxActive());
        return config;
    }

}
