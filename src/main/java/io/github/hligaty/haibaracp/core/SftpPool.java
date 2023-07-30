package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;
import org.springframework.beans.factory.DisposableBean;

import java.time.Duration;

/**
 * SFTP connect pool.
 *
 * @author hligaty
 */
public class SftpPool implements DisposableBean {
    private static final Log log = LogFactory.getLog(SftpPool.class);
    private final GenericObjectPool<SftpClient> internalPool;

    public SftpPool(ClientProperties clientProperties, PoolProperties poolProperties) {
        this.internalPool = new GenericObjectPool<>(new PooledClientFactory(clientProperties), getPoolConfig(poolProperties));
        log.info("HaibaraCP: Created");
    }

    public SftpClient borrowObject() {
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            throw new PoolException("Could not get a resource from the pool", e);
        }
    }

    public void invalidateObject(SftpClient sftpClient) {
        try {
            internalPool.invalidateObject(sftpClient);
        } catch (Exception e) {
            throw new PoolException("Could not invalidate the broken resource", e);
        }
    }

    public void returnObject(SftpClient sftpClient) {
        try {
            internalPool.returnObject(sftpClient);
        } catch (Exception e) {
            throw new PoolException("Could not return a resource from the pool", e);
        }
    }

    @Override
    public void destroy() {
        try {
            internalPool.close();
            log.info("HaibaraCP: Closed");
        } catch (Exception e) {
            throw new PoolException("Could not destroy the pool", e);
        }
    }

    private static class PooledClientFactory extends BasePooledObjectFactory<SftpClient> {

        private final ClientProperties clientProperties;

        public PooledClientFactory(ClientProperties clientProperties) {
            this.clientProperties = clientProperties;
        }

        @Override
        public SftpClient create() {
            return new SftpClient(clientProperties);
        }

        @Override
        public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
            return new DefaultPooledObject<>(sftpClient);
        }

        @Override
        public boolean validateObject(PooledObject<SftpClient> p) {
            return p.getObject().test();
        }

        @Override
        public void destroyObject(PooledObject<SftpClient> p) {
            p.getObject().disconnect();
        }

    }

    private GenericObjectPoolConfig<SftpClient> getPoolConfig(PoolProperties poolProperties) {
        GenericObjectPoolConfig<SftpClient> config = new GenericObjectPoolConfig<>();
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
