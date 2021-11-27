package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.*;
import io.github.hligaty.haibaracp.config.SftpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  public SftpClient(SftpProperties sftpProperties) throws SftpException, JSchException {
    try {
      JSch jsch = new JSch();
      session = jsch.getSession(sftpProperties.getUsername(), sftpProperties.getHost(), sftpProperties.getPort());
      if (sftpProperties.isStrictHostKeyChecking()) {
        session.setConfig("StrictHostKeyChecking", "ask");
        session.setUserInfo(new UserInfoImpl(sftpProperties.getPassword()));
        jsch.addIdentity(sftpProperties.getKeyPath());
      } else {
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(sftpProperties.getPassword());
      }
      if (StringUtils.hasText(sftpProperties.getKex())) {
        session.setConfig("kex", sftpProperties.getKex());
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
}
