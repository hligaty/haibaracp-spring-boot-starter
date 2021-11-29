package io.github.hligaty.haibaracp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.StringJoiner;

/**
 * @author gaodapeng
 */
@ConfigurationProperties("sftp.pool")
public class PoolProperties {
  /**
   * 池中最小的连接数，只有当 timeBetweenEvictionRuns 为正时才有效
   */
  private int minIdle = 1;

  /**
   * 池中最大的空闲连接数，为负值时表示无限
   */
  private int maxIdle = 8;

  /**
   * 池可以产生的最大连接，为负值时表示无限
   */
  private int maxActive = 8;

  /**
   * 当池耗尽时，阻塞的最长时间，为负值时无限等待
   */
  private long maxWait = -1;

  /**
   * 从池中取出连接是否检测可用
   */
  private boolean testOnBorrow = true;

  /**
   * 将连接返还给池时检测是否可用
   */
  private boolean testOnReturn = false;

  /**
   * 检查连接池中的连接是否可用
   */
  private boolean testWhileIdle = true;

  /**
   * 距离上次空闲线程检测完成多久后再次执行
   */
  private long timeBetweenEvictionRuns = 300000L;

  /**
   * 超过 minIdle 小于 maxTotal 的最大连接数
   */
  private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;

  /**
   * 每个 key 的子池最小空闲连接数
   */
  private int minIdlePerKey = 1;

  /**
   * 每个 key 的子池最大空闲连接数
   */
  private int maxIdlePerKey = 8;

  /**
   * 每个 key 的子池可以产生的最大连接数，为负值时表示无限
   */
  private int maxActivePerKey = 8;

  public int getMinIdle() {
    return minIdle;
  }

  public void setMinIdle(int minIdle) {
    this.minIdle = minIdle;
  }

  public int getMaxIdle() {
    return maxIdle;
  }

  public void setMaxIdle(int maxIdle) {
    this.maxIdle = maxIdle;
  }

  public int getMaxActive() {
    return maxActive;
  }

  public void setMaxActive(int maxActive) {
    this.maxActive = maxActive;
  }

  public long getMaxWait() {
    return maxWait;
  }

  public void setMaxWait(long maxWait) {
    this.maxWait = maxWait;
  }

  public boolean isTestOnBorrow() {
    return testOnBorrow;
  }

  public void setTestOnBorrow(boolean testOnBorrow) {
    this.testOnBorrow = testOnBorrow;
  }

  public boolean isTestOnReturn() {
    return testOnReturn;
  }

  public void setTestOnReturn(boolean testOnReturn) {
    this.testOnReturn = testOnReturn;
  }

  public boolean isTestWhileIdle() {
    return testWhileIdle;
  }

  public void setTestWhileIdle(boolean testWhileIdle) {
    this.testWhileIdle = testWhileIdle;
  }

  public long getTimeBetweenEvictionRuns() {
    return timeBetweenEvictionRuns;
  }

  public void setTimeBetweenEvictionRuns(long timeBetweenEvictionRuns) {
    this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
  }

  public long getMinEvictableIdleTimeMillis() {
    return minEvictableIdleTimeMillis;
  }

  public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
  }

  public int getMinIdlePerKey() {
    return minIdlePerKey;
  }

  public void setMinIdlePerKey(int minIdlePerKey) {
    this.minIdlePerKey = minIdlePerKey;
  }

  public int getMaxIdlePerKey() {
    return maxIdlePerKey;
  }

  public void setMaxIdlePerKey(int maxIdlePerKey) {
    this.maxIdlePerKey = maxIdlePerKey;
  }

  public int getMaxActivePerKey() {
    return maxActivePerKey;
  }

  public void setMaxActivePerKey(int maxActivePerKey) {
    this.maxActivePerKey = maxActivePerKey;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PoolProperties.class.getSimpleName() + "[", "]")
            .add("minIdle=" + minIdle)
            .add("maxIdle=" + maxIdle)
            .add("maxActive=" + maxActive)
            .add("maxWait=" + maxWait)
            .add("testOnBorrow=" + testOnBorrow)
            .add("testOnReturn=" + testOnReturn)
            .add("testWhileIdle=" + testWhileIdle)
            .add("timeBetweenEvictionRuns=" + timeBetweenEvictionRuns)
            .add("minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis)
            .add("minIdlePerKey=" + minIdlePerKey)
            .add("maxIdlePerKey=" + maxIdlePerKey)
            .add("maxActivePerKey=" + maxActivePerKey)
            .toString();
  }
}
