package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.PoolProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.function.Supplier;

class SftpClientProvider {

    private static final Log log = LogFactory.getLog(SftpClientProvider.class);

    private final GenericObjectPool<SftpClient> pool;

    public SftpClientProvider(Supplier<SftpClient> sftpClientSupplier, PoolProperties poolProperties) {
        this.pool = new GenericObjectPool<>(new SftpPooledObjectFactory() {
            @Override
            public SftpClient create() {
                return sftpClientSupplier.get();
            }
        }, getPoolConfig(poolProperties));
        log.info("HaibaraCP: Created");
    }

    public SftpClient getSftpClient() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new PoolException("Could not get a resource from the pool", e);
        }
    }
    
    public void release(SftpClient sftpClient) {
        try {
            pool.returnObject(sftpClient);
        } catch (Exception e) {
            throw new PoolException("Could not return a resource from the pool", e);
        }
    }
    
    public void destroy() {
        try {
            pool.close();
            log.info("HaibaraCP: Closed");
        } catch (Exception e) {
            throw new PoolException("Could not destroy the pool", e);
        }
    }

    private static abstract class SftpPooledObjectFactory extends BasePooledObjectFactory<SftpClient> {

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
