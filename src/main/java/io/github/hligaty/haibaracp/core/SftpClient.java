package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.*;
import io.github.hligaty.haibaracp.config.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 连接池对象
 *
 * @author hligaty
 */
public class SftpClient {
  private static final Logger log = LoggerFactory.getLogger(SftpClient.class);

  private final ChannelSftp channelSftp;
  private final Session session;
  /**
   * 连接信息
   */
  private String clientInfo = "Sftpclient-";
  private static final AtomicLong CLIENT_NUMBER = new AtomicLong(1L);
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  /**
   * ssh 根目录。
   * 用于判断是否成功返回连接到连接池的条件之一
   */
  private final String originalDir;

  public ChannelSftp getChannelSftp() {
    return channelSftp;
  }

  public String getClientInfo() {
    return clientInfo;
  }

  public SftpClient(ClientProperties clientProperties) throws SftpException, JSchException {
    try {
      JSch jsch = new JSch();
      session = jsch.getSession(clientProperties.getUsername(), clientProperties.getHost(), clientProperties.getPort());
      if (clientProperties.isStrictHostKeyChecking()) {
        session.setConfig("StrictHostKeyChecking", "ask");
        session.setUserInfo(new UserInfoImpl(clientProperties.getPassword()));
        jsch.addIdentity(clientProperties.getKeyPath());
      } else {
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(clientProperties.getPassword());
      }
      if (StringUtils.hasText(clientProperties.getKex())) {
        session.setConfig("kex", clientProperties.getKex());
      }
      session.connect();
      channelSftp = (ChannelSftp) session.openChannel("sftp");
      channelSftp.connect();
      clientInfo += CLIENT_NUMBER.getAndIncrement() + ",createTime:" + DATE_TIME_FORMATTER.format(LocalDateTime.now());
      originalDir = channelSftp.pwd();
      if (log.isDebugEnabled()) {
        log.debug("{}: Opened Client, originalDir={}", clientInfo, originalDir);
      }
    } catch (Exception e) {
      disconnect();
      throw e;
    }
  }

  /**
   * 释放连接
   */
  protected final void disconnect() {
    if (channelSftp != null) {
      try {
        channelSftp.disconnect();
      } catch (Exception t) {
        log.error("{}: Failed to release channelSftp.", clientInfo, t);
      }
    }
    if (session != null) {
      try {
        session.disconnect();
      } catch (Exception e) {
        log.error("{}: Failed to release session.", clientInfo, e);
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("{}: Close client.", clientInfo);
    }
  }

  /**
   * 验证连接是否可用。与 {@link #rollback} 相照应。
   *
   * @return 连接是否可用
   */
  protected boolean validateConnect() {
    try {
      boolean result = session.isConnected() && channelSftp.isConnected() && originalDir.equals(channelSftp.pwd());
      if (log.isDebugEnabled()) {
        log.debug("{}: Status is {}", clientInfo, result);
      }
      return result;
    } catch (Exception e) {
      if (log.isDebugEnabled()) {
        log.debug("{}: Failed to validate client.", clientInfo, e);
      }
      return false;
    }
  }

  /**
   * 回滚 SFTP 连接到初始状态。与 {@link #validateConnect} 相照应。
   *
   * @return 是否成功回滚到初始连接状态
   */
  protected boolean rollback() {
    try {
      channelSftp.cd(originalDir);
      return true;
    } catch (SftpException e) {
      log.debug("{}: Failed to cd during return", clientInfo);
    }
    return false;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SftpClient.class.getSimpleName() + "[", "]")
            .add("clientInfo='" + clientInfo + "'")
            .add("originalDir='" + originalDir + "'")
            .toString();
  }

  private static class UserInfoImpl implements UserInfo {
    private final String passphrase;

    public UserInfoImpl(String passphrase) {
      this.passphrase = passphrase;
    }

    @Override
    public String getPassphrase() {
      return passphrase;
    }

    @Override
    public String getPassword() {
      return null;
    }

    @Override
    public boolean promptPassword(String s) {
      return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
      return true;
    }

    @Override
    public boolean promptYesNo(String s) {
      return true;
    }

    @Override
    public void showMessage(String s) {
    }
  }
}
