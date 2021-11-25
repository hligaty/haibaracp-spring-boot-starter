package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link SftpClient} 功能封装类，包含常用 {@link ChannelSftp} 操作。
 *
 * @author gaodapeng
 */
public class ChannelSftpWrapper {
  private static final String SEPARATOR = "/";
  public static final String FILE_NOT_FOUND = "No such file";
  private final ChannelSftp channelSftp;

  public ChannelSftpWrapper(ChannelSftp channelSftp) {
    this.channelSftp = channelSftp;
  }

  /**
   * 进入指定目录，遇到没创建的目录会自动创建。
   * 如果 dir 是“/”开头，就从根目录开始进入；否则从 pwd 当前目录开始进入。
   *
   * @param dir 目录名
   * @throws SftpException 如果检查、进入或创建过程中发生意外
   */
  public final boolean cd(String dir, boolean mkdir) throws SftpException {
    if (!StringUtils.hasText(dir)) {
      return true;
    }
    try {
      channelSftp.cd(dir);
      return true;
    } catch (SftpException ignored) {
      if (dir.startsWith(SEPARATOR)) {
        channelSftp.cd(SEPARATOR);
      }
      String[] multiDir = dir.split(SEPARATOR);
      for (String currDir : multiDir) {
        if (!StringUtils.hasText(currDir)) {
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
  }

  /**
   * 判断当前目录下是否存在 dir 这一级目录
   *
   * @param dir 目录名
   * @return 是否为目录
   * @throws SftpException 判断时出现意外
   */
  public final boolean isDir(String dir) throws SftpException {
    try {
      return channelSftp.lstat(dir).isDir();
    } catch (SftpException e) {
      if (FILE_NOT_FOUND.equals(e.getMessage())) {
        return false;
      }
      throw e;
    }
  }

  /**
   * @see SftpTemplate#download
   */
  public final Void download(String from, OutputStream to) throws FileNotFoundException, SftpException {
    if (cd(from.substring(0, from.lastIndexOf(SEPARATOR) + 1), false)) {
      try {
        channelSftp.get(from.substring(from.lastIndexOf(SEPARATOR) + 1), to);
      } catch (SftpException e) {
        if (FILE_NOT_FOUND.equals(e.getMessage())) {
          throw new FileNotFoundException(from);
        }
        throw e;
      }
      return null;
    }
    throw new FileNotFoundException(from);
  }

  /**
   * @see SftpTemplate#upload(InputStream, String)
   */
  public final Void upload(InputStream from, String to) throws SftpException, FileNotFoundException {
    if (cd(to.substring(0, to.lastIndexOf(SEPARATOR) + 1), true)) {
      channelSftp.put(from, to.substring(to.lastIndexOf(SEPARATOR) + 1));
      return null;
    }
    throw new FileNotFoundException(to);
  }
}
