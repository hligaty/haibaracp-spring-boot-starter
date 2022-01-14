package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * SFTP connect pool.
 *
 * @author hligaty
 */
public class SftpPool {
  private static final Log log = LogFactory.getLog(SftpPool.class);
  private GenericObjectPool<SftpClient> internalPool;
  private GenericKeyedObjectPool<String, SftpClient> internalKeyedPool;

  public SftpPool(ClientProperties clientProperties, PoolProperties poolProperties) {
    this.internalPool = new GenericObjectPool<>(new PooledClientFactory(clientProperties), getPoolConfig(poolProperties));
    log.info("HaibaraCP: Created");
  }

  public SftpPool(Map<String, ClientProperties> clientPropertiesMap, PoolProperties poolProperties) {
    this.internalKeyedPool = new GenericKeyedObjectPool<>(new KeyedPooledClientFactory(clientPropertiesMap), getKeyedPoolConfig(poolProperties));
    log.info("HaibaraCP: Created");
  }

  public SftpClient borrowObject(String key) {
    try {
      return key == null ? internalPool.borrowObject() : internalKeyedPool.borrowObject(key);
    } catch (Exception e) {
      throw new PoolException("Could not get a resource from the pool", e);
    }
  }

  public void invalidateObject(String key, SftpClient sftpClient) {
    try {
      if (key == null) {
        internalPool.invalidateObject(sftpClient);
      } else {
        internalKeyedPool.invalidateObject(key, sftpClient);
      }
    } catch (Exception e) {
      throw new PoolException("Could not invalidate the broken resource", e);
    }
  }

  public void returnObject(String key, SftpClient sftpClient) {
    try {
      if (key == null) {
        internalPool.returnObject(sftpClient);
      } else {
        internalKeyedPool.returnObject(key, sftpClient);
      }
    } catch (Exception e) {
      throw new PoolException("Could not return a resource from the pool", e);
    }
  }

  @PreDestroy
  public void close() {
    try {
      if (isUniqueHost()) {
        internalPool.close();
      } else {
        internalKeyedPool.close();
      }
      log.info("HaibaraCP: Closed");
    } catch (Exception e) {
      throw new PoolException("Could not destroy the pool", e);
    }
  }

  public boolean isUniqueHost() {
    return internalPool != null;
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

  private static class KeyedPooledClientFactory extends BaseKeyedPooledObjectFactory<String, SftpClient> {

    private final Map<String, ClientProperties> clientPropertiesMap;

    private KeyedPooledClientFactory(Map<String, ClientProperties> clientPropertiesMap) {
      this.clientPropertiesMap = clientPropertiesMap;
    }

    @Override
    public SftpClient create(String key) {
      return new SftpClient(clientPropertiesMap.get(key));
    }

    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
      return new DefaultPooledObject<>(sftpClient);
    }

    @Override
    public boolean validateObject(String key, PooledObject<SftpClient> p) {
      return p.getObject().test();
    }

    @Override
    public void destroyObject(String key, PooledObject<SftpClient> p) {
      p.getObject().disconnect();
    }
  }

  private GenericObjectPoolConfig<SftpClient> getPoolConfig(PoolProperties poolProperties) {
    GenericObjectPoolConfig<SftpClient> config = commonPoolConfig(new GenericObjectPoolConfig<>(), poolProperties);
    config.setMinIdle(poolProperties.getMinIdle());
    config.setMaxIdle(poolProperties.getMaxIdle());
    config.setMaxTotal(poolProperties.getMaxActive());
    return config;
  }

  private GenericKeyedObjectPoolConfig<SftpClient> getKeyedPoolConfig(PoolProperties poolProperties) {
    GenericKeyedObjectPoolConfig<SftpClient> config = commonPoolConfig(new GenericKeyedObjectPoolConfig<>(), poolProperties);
    config.setMinIdlePerKey(poolProperties.getMinIdle());
    config.setMaxIdlePerKey(poolProperties.getMaxIdle());
    config.setMaxTotalPerKey(poolProperties.getMaxActivePerKey());
    config.setMaxTotal(poolProperties.getMaxActive());
    return config;
  }

  private <T extends BaseObjectPoolConfig<SftpClient>> T commonPoolConfig(T config, PoolProperties poolProperties) {
    config.setMaxWaitMillis(poolProperties.getMaxWait());
    config.setTestOnBorrow(poolProperties.isTestOnBorrow());
    config.setTestOnReturn(poolProperties.isTestOnReturn());
    config.setTestWhileIdle(poolProperties.isTestWhileIdle());
    config.setTimeBetweenEvictionRunsMillis(poolProperties.getTimeBetweenEvictionRuns());
    config.setMinEvictableIdleTimeMillis(poolProperties.getMinEvictableIdleTimeMillis());
    return config;
  }
}
