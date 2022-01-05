package io.github.hligaty.haibaracp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.StringJoiner;

/**
 * Configuration properties for SFTP.
 *
 * @author hligaty
 */
@ConfigurationProperties("sftp")
public class ClientProperties {
  /**
   * SFTP server host.
   */
  private String host = "localhost";
  /**
   * SFTP server port.
   */
  private int port = 22;
  /**
   * Login username of the sftp server.
   */
  private String username;
  /**
   * Whether to enable host key login.
   */
  private boolean strictHostKeyChecking = false;
  /**
   * host key.
   */
  private String keyPath;
  /**
   * Login password or host key passphrase of the sftp server.
   */
  private String password = "";
  /**
   * SSH kex algorithms.
   */
  private String kex;

  /**
   * Configure multiple hosts.
   */
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

  /**
   * @return map key is used to switch the host connection, value is client properties.
   */
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
