package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author hligaty
 */
public class SftpTemplate {
  private static final Logger log = LoggerFactory.getLogger(SftpTemplate.class);
  private final SftpPool sftpPool;

  public SftpTemplate(SftpPool sftpPool) {
    this.sftpPool = sftpPool;
  }

  /**
   * 执行指定的 SFTP 操作。
   *
   * @param action 指定的 SFTP 操作
   * @param <T> 指定的返回值类型
   * @return 指定的返回值，可以为空
   * @throws Exception 操作 channelsSftp 抛出的 SftpException 或者其他的 Exception
   */
  public <T> T execute(SftpCallback<T> action) throws Exception {
    Assert.notNull(action, "Callback object must not be null");
    SftpClient sftpClient = null;
    try {
      sftpClient = sftpPool.borrowObject();
      if (log.isDebugEnabled()) {
        log.debug("{}: Get client.", sftpClient.getClientInfo());
      }
      return action.doInSftp(sftpClient.getChannelSftp());
    } catch (SftpException e) {
      if (log.isDebugEnabled() && sftpClient != null) {
        log.debug("{}: Invalidate client.", sftpClient.getClientInfo());
      }
      sftpPool.invalidateObject(sftpClient);
      sftpClient = null;
      throw e;
    } finally {
      if (sftpClient != null) {
        boolean rollback = sftpClient.rollback();
        if (rollback) {
          sftpPool.returnObject(sftpClient);
        } else {
          sftpPool.invalidateObject(sftpClient);
        }
        if (log.isDebugEnabled()) {
          log.debug(rollback ? "{}: Return client." : "{}: Invalidate client.", sftpClient.getClientInfo());
        }
      }
    }
  }

  /**
   * 将文件 from 下载到 to
   *
   * @param from 文件全路径
   * @param to   文件输出流
   * @throws Exception 目录、文件不存在或下载时出现意外
   */
  public void download(String from, OutputStream to) throws Exception {
    this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, to));
  }


  /**
   * 将文件 from 下载到 to
   *
   * @param from 文件全路径
   * @param to 下载文件路径
   * @throws Exception 目录不存在或下载时出现意外
   */
  public void download(String from, Path to) throws Exception {
    if (!to.isAbsolute() || Files.notExists(to.getParent())) {
      throw new FileNotFoundException(to.toString());
    }
    try (OutputStream outputStream = Files.newOutputStream(to)) {
      this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, outputStream));
    }
  }

  /**
   * 上传 inputStream 到 dir。目录不存在时自动创建，支持相对路径和绝对路径
   *
   * @param from 输入文件流
   * @param to   文件全路径
   * @throws Exception 上传或切换目录时出现意外
   */
  public void upload(InputStream from, String to) throws Exception {
    this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).upload(from, to));
  }
}
