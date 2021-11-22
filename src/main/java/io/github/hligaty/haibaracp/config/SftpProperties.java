package io.github.hligaty.haibaracp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author hligaty
 */
@ConfigurationProperties("sftp")
public class SftpProperties {
  /**
   * 地址
   */
  private String host = "localhost";
  /**
   * 端口号
   */
  private int port = 22;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * Session 参数配置
   */
  private Map<String, String> session;
  /**
   * 连接池配置
   */
  private Pool pool;

  /**
   * 连接池配置类
   */
  public static class Pool {
    /**
     * 池中最小的连接数，只有当 timeBetweenEvictionRuns 为正时才有效
     */
    private int minIdle = 1;

    /**
     * 池中最大的空闲连接数，为负值时表示无限
     */
    private int maxIdle = 8;

    /**
     * 池可以产生的最大对象数，为负值时表示无限
     */
    private int maxActive = 16;

    /**
     * 当池耗尽时，阻塞的最长时间，为负值时无限等待
     */
    private long maxWait = -1;

    /**
     * 从池中取出对象是是否检测可用
     */
    private boolean testOnBorrow = true;

    /**
     * 将对象返还给池时检测是否可用
     */
    private boolean testOnReturn = false;

    /**
     * 检查连接池对象是否可用
     */
    private boolean testWhileIdle = true;

    /**
     * 距离上次空闲线程检测完成多久后再次执行
     */
    private long timeBetweenEvictionRuns = 300000L;

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

    @Override
    public String toString() {
      return "Pool{" +
        "minIdle=" + minIdle +
        ", maxIdle=" + maxIdle +
        ", maxActive=" + maxActive +
        ", maxWait=" + maxWait +
        ", testOnBorrow=" + testOnBorrow +
        ", testOnReturn=" + testOnReturn +
        ", testWhileIdle=" + testWhileIdle +
        ", timeBetweenEvictionRuns=" + timeBetweenEvictionRuns +
        '}';
    }
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Map<String, String> getSession() {
    return session;
  }

  public void setSession(Map<String, String> session) {
    this.session = session;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  @Override
  public String toString() {
    return "SftpProperties{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", username='" + username + '\'' +
      ", password='" + password + '\'' +
      ", session=" + session +
      ", pool=" + pool +
      '}';
  }
}
