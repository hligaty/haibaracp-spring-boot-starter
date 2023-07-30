package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import org.springframework.util.Assert;

/**
 * @author hligaty
 */
public class SftpTemplate {
  private final SftpPool sftpPool;

  public SftpTemplate(SftpPool sftpPool) {
    this.sftpPool = sftpPool;
  }

  /**
   * Executes the given action object within a connection, which can be exposed or not.
   *
   * @param action callback object that specifies the Sftp action.
   * @param <T>    return type
   * @return object returned by the action.
   * @throws SftpException a sftp exception during remote interaction.
   */
  public <T> T execute(SftpCallback<T> action) throws SftpException {
    Assert.notNull(action, "Callback object must not be null");
    SftpClient sftpClient = null;
    try {
      sftpClient = sftpPool.borrowObject();
      return action.doInSftp(sftpClient.getChannelSftp());
    } finally {
      if (sftpClient != null) {
        if (sftpClient.reset()) {
          sftpPool.returnObject(sftpClient);
        } else {
          sftpPool.invalidateObject(sftpClient);
        }
      }
    }
  }

  /**
   * Executes the given action object within a connection, which can be exposed or not.
   *
   * @param action callback object that specifies the Sftp action.
   * @throws SftpException an IO exception during remote interaction.
   */
  public void executeWithoutResult(SftpCallbackWithoutResult action) throws SftpException {
    Assert.notNull(action, "Callback object must not be null");
    this.execute(channelSftp -> {
      action.doInSftp(channelSftp);
      return null;
    });
  }

  /**
   * Download file.
   * Support relative path and absolute path: "/home/haibara/aptx4869.docx" or "aptx4869.docx".
   *
   * @param from the path to the remote file.
   * @param to   the path to the local file.
   * @throws SftpException an IO exception during remote interaction or file not found.
   */
  public void download(String from, String to) throws SftpException {
    this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, to));
  }

  /**
   * Upload file. Create recursively when remote directory does not exist.
   *
   * @param from the path to the local file.
   * @param to   the path to the remote file.
   * @throws SftpException an IO exception during remote interaction or file not found.
   */
  public void upload(String from, String to) throws SftpException {
    this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).upload(from, to));
  }

  /**
   * Check if the remote file or directory exists.
   *
   * @param path the remote path.
   * @return true if remote path exists.
   * @throws SftpException an IO exception during remote interaction.
   */
  public boolean exists(String path) throws SftpException {
    return this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).exists(path));
  }

  /**
   * View a list of files or directories. Lists are not recursive.
   *
   * @param path the remote path.
   * @return file list.
   * @throws SftpException an IO exception during remote interaction or path not found.
   */
  public LsEntry[] list(String path) throws SftpException {
    return this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).list(path));
  }
}
