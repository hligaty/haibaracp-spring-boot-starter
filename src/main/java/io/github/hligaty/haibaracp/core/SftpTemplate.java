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
   * Executes the given action object within a connection, which can be exposed or not.
   *
   * @param action callback object that specifies the Sftp action
   * @param <T> return type
   * @return object returned by the action
   * @throws Exception in case of Sftp or youself code errors
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
   * Download file.
   * Support relative path and absolute path: "/home/haibara/aptx4869.docx" or "aptx4869.docx"
   *
   * @param from the path to the remote file
   * @param to   output stream of local file
   * @throws Exception if a file not exist or Sftp error occurs
   */
  public void download(String from, OutputStream to) throws Exception {
    this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, to));
  }


  /**
   * Download file.
   *
   * @param from the path to the remote file
   * @param to   the path to the local file
   * @throws Exception if a file not exist or Sftp error occurs
   * @see #download(String, OutputStream)
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
   * Upload file.
   *
   * @param from input stream of local file
   * @param to   the path to the remote file
   * @throws Exception if an Sftp error occurs
   */
  public void upload(InputStream from, String to) throws Exception {
    this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).upload(from, to));
  }
}
