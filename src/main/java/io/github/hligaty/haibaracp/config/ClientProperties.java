package io.github.hligaty.haibaracp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.StringJoiner;

/**
 * @author hligaty
 */
@ConfigurationProperties("sftp")
public class ClientProperties {
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
   * 验证私钥
   */
  private boolean strictHostKeyChecking = false;
  /**
   * 私钥路径
   */
  private String keyPath;
  /**
   * 密码或私钥密码
   */
  private String password = "";
  /**
   * 算法
   */
  private String kex;

  private LinkedHashMap<String, ClientProperties> hosts;

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

  public boolean isStrictHostKeyChecking() {
    return strictHostKeyChecking;
  }

  public void setStrictHostKeyChecking(boolean strictHostKeyChecking) {
    this.strictHostKeyChecking = strictHostKeyChecking;
  }

  public String getKeyPath() {
    return keyPath;
  }

  public void setKeyPath(String keyPath) {
    this.keyPath = keyPath;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getKex() {
    return kex;
  }

  public void setKex(String kex) {
    this.kex = kex;
  }

  public LinkedHashMap<String, ClientProperties> getHosts() {
    return hosts;
  }

  public void setHosts(LinkedHashMap<String, ClientProperties> hosts) {
    this.hosts = hosts;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ClientProperties.class.getSimpleName() + "[", "]")
            .add("host='" + host + "'")
            .add("port=" + port)
            .add("username='" + username + "'")
            .add("strictHostKeyChecking=" + strictHostKeyChecking)
            .add("keyPath='" + keyPath + "'")
            .add("password='" + password + "'")
            .add("kex='" + kex + "'")
            .add("hosts=" + hosts)
            .toString();
  }
}
