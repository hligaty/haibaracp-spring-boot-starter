package com.github.hligaty.haibaracp.core;

import com.github.hligaty.haibaracp.config.SftpProperties;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hligaty & haibara
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
      session.setPassword(sftpProperties.getPassword());
      Properties config = new Properties();
      if (sftpProperties.getSession() != null) {
        sftpProperties.getSession().forEach(config::put);
      }
      session.setConfig(config);
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
   * 进入指定目录，遇到没创建的目录会自动创建。
   * 如果 dir 是“/”开头，就从根目录开始进入；否则从 pwd 当前目录开始进入。
   *
   * @param dir 目录名
   * @throws SftpException 如果检查、进入或创建过程中发生意外
   */
  public final boolean cd(String dir, boolean mkdir) throws SftpException {
    if (dir.startsWith("/")) {
      try {
        channelSftp.cd(dir);
        return true;
      } catch (SftpException ignored) {
      }
    }
    channelSftp.cd("/");
    String[] multiDir = dir.split("/");
    for (String currDir : multiDir) {
      if (StringUtils.isEmpty(currDir)) {
        continue;
      }
      if (!isDir(currDir)) {
        if (!mkdir) {
          return false;
        }
        channelSftp.mkdir(currDir);
      }
      channelSftp.cd(currDir);
    }
    return true;
  }

  protected final boolean isDir(String dir) throws SftpException {
    try {
      return channelSftp.lstat(dir).isDir();
    } catch (SftpException e) {
      if ("No such file".equals(e.getMessage())) {
        return false;
      }
      throw e;
    }
  }

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
      log.debug("{}: Is Closed.", clientInfo);
    }
  }

  protected boolean validateConnect() {
    try {
      boolean result = session.isConnected() && channelSftp.isConnected() && originalDir.equals(channelSftp.pwd());
      if (log.isDebugEnabled()) {
        log.debug("{}: Status is {}", clientInfo, result);
      }
      return result;
    } catch (Exception e) {
      if (log.isDebugEnabled()) {
        log.debug("{}: Failed to validate sftpClient.", clientInfo, e);
      }
      return false;
    }
  }

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
