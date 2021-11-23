package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.SftpProperties;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;

/**
 * @author hligaty
 */
public class SftpPool implements ObjectPool<SftpClient> {
  private static final Logger log = LoggerFactory.getLogger(SftpPool.class);
  private final GenericObjectPool<SftpClient> internalPool;

  public SftpPool(SftpProperties sftpProperties) {
    this.internalPool = new GenericObjectPool<SftpClient>(new SftpFactory(sftpProperties), getPoolConfig(sftpProperties.getPool())) {
      @Override
      public void returnObject(SftpClient sftpClient) {
        if (sftpClient.rollback()) {
          super.returnObject(sftpClient);
          return;
        }
        try {
          super.invalidateObject(sftpClient);
        } catch (Exception ignored) {
        }
      }
    };
    log.info("HaibaraCP: Created");
  }

  @Override
  public void addObject() throws Exception {
    internalPool.addObject();
  }

  @Override
  public SftpClient borrowObject() throws Exception {
    return internalPool.borrowObject();
  }

  @Override
  public void clear() {
    internalPool.clear();
  }

  @PreDestroy
  @Override
  public void close() {
    internalPool.close();
    log.info("HaibaraCP: Closed");
  }

  @Override
  public int getNumActive() {
    return internalPool.getNumActive();
  }

  @Override
  public int getNumIdle() {
    return internalPool.getNumIdle();
  }

  @Override
  public void invalidateObject(SftpClient obj) throws Exception {
    internalPool.invalidateObject(obj);
  }

  @Override
  public void returnObject(SftpClient obj) {
    internalPool.returnObject(obj);
  }

  private static class SftpFactory extends BasePooledObjectFactory<SftpClient> {

    private final SftpProperties sftpProperties;

    public SftpFactory(SftpProperties sftpProperties) {
      this.sftpProperties = sftpProperties;
    }

    @Override
    public SftpClient create() throws Exception {
      return new SftpClient(sftpProperties);
    }

    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
      return new DefaultPooledObject<>(sftpClient);
    }

    @Override
    public boolean validateObject(PooledObject<SftpClient> p) {
      return p.getObject().validateConnect();
    }

    @Override
    public void destroyObject(PooledObject<SftpClient> p) {
      p.getObject().disconnect();
    }

  }

  private GenericObjectPoolConfig<SftpClient> getPoolConfig(SftpProperties.Pool properties) {
    if (properties == null) {
      properties = new SftpProperties.Pool();
    }
    GenericObjectPoolConfig<SftpClient> config = new GenericObjectPoolConfig<>();
    config.setMinIdle(properties.getMinIdle());
    config.setMaxIdle(properties.getMaxIdle());
    config.setMaxTotal(properties.getMaxActive());
    config.setMaxWaitMillis(properties.getMaxWait());
    config.setTestOnBorrow(properties.isTestOnBorrow());
    config.setTestOnReturn(properties.isTestOnReturn());
    config.setTestWhileIdle(properties.isTestWhileIdle());
    config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns());
    log.info("HaibaraCP :" + properties);
    return config;
  }
}
