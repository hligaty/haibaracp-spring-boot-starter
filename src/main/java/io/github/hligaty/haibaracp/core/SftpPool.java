package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * SFTP connect pool.
 *
 * @author hligaty
 */
public class SftpPool {
  private static final Logger log = LoggerFactory.getLogger(SftpPool.class);
  private boolean uniqueHost;
  private GenericObjectPool<SftpClient> internalPool;
  private GenericKeyedObjectPool<String, SftpClient> internalKeyedPool;

  public SftpPool(ClientProperties clientProperties, PoolProperties poolProperties) {
    uniqueHost = true;
    this.internalPool = new GenericObjectPool<>(new PooledClientFactory(clientProperties), getPoolConfig(poolProperties));
    log.info("HaibaraCP: Created");
  }

  public SftpPool(Map<String, ClientProperties> clientPropertiesMap, PoolProperties poolProperties) {
    this.internalKeyedPool = new GenericKeyedObjectPool<>(new KeyedPooledClientFactory(clientPropertiesMap), getKeyedPoolConfig(poolProperties));
    log.info("HaibaraCP: Created");
  }

  public SftpClient borrowObject() throws Exception {
    return uniqueHost ? internalPool.borrowObject() : internalKeyedPool.borrowObject(HostHolder.getHostKey());
  }

  public void invalidateObject(SftpClient sftpClient) throws Exception {
    if (uniqueHost) {
      internalPool.invalidateObject(sftpClient);
    } else {
      String hostKey = HostHolder.getHostKey();
      HostHolder.clear();
      internalKeyedPool.invalidateObject(hostKey, sftpClient);
    }
  }

  public void returnObject(SftpClient sftpClient) {
    if (uniqueHost) {
      internalPool.returnObject(sftpClient);
    } else {
      String hostKey = HostHolder.getHostKey();
      HostHolder.clear();
      internalKeyedPool.returnObject(hostKey, sftpClient);
    }
  }

  @PreDestroy
  public void close() {
    if (uniqueHost) {
      internalPool.close();
    } else {
      internalKeyedPool.close();
    }
    log.info("HaibaraCP: Closed");
  }

  private static class PooledClientFactory extends BasePooledObjectFactory<SftpClient> {

    private final ClientProperties clientProperties;

    public PooledClientFactory(ClientProperties clientProperties) {
      this.clientProperties = clientProperties;
    }

    @Override
    public SftpClient create() throws Exception {
      return new SftpClient(clientProperties);
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

  private static class KeyedPooledClientFactory extends BaseKeyedPooledObjectFactory<String, SftpClient> {

    private final Map<String, ClientProperties> clientPropertiesMap;

    private KeyedPooledClientFactory(Map<String, ClientProperties> clientPropertiesMap) {
      this.clientPropertiesMap = clientPropertiesMap;
    }

    @Override
    public SftpClient create(String key) throws Exception {
      return new SftpClient(clientPropertiesMap.get(key));
    }

    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
      return new DefaultPooledObject<>(sftpClient);
    }

    @Override
    public boolean validateObject(String key, PooledObject<SftpClient> p) {
      return p.getObject().validateConnect();
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
    log.info("HaibaraCP :" + poolProperties);
    return config;
  }

  private GenericKeyedObjectPoolConfig<SftpClient> getKeyedPoolConfig(PoolProperties poolProperties) {
    GenericKeyedObjectPoolConfig<SftpClient> config = commonPoolConfig(new GenericKeyedObjectPoolConfig<>(), poolProperties);
    config.setMinIdlePerKey(poolProperties.getMinIdle());
    config.setMaxIdlePerKey(poolProperties.getMaxIdle());
    config.setMaxTotalPerKey(poolProperties.getMaxActivePerKey());
    config.setMaxTotal(poolProperties.getMaxActive());
    log.info("HaibaraCP :" + poolProperties);
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
