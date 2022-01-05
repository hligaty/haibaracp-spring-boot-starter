package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class that simplifies ChannelSftp use code.
 *
 * @author hligaty
 */
public class ChannelSftpWrapper {
  private static final String SEPARATOR = "/";
  private final ChannelSftp channelSftp;

  public ChannelSftpWrapper(ChannelSftp channelSftp) {
    this.channelSftp = channelSftp;
  }

  /**
   * Switch the directory, if the directory does not exist, mkdir decides whether to create it.
   *
   * @param dir the directory to switch
   * @throws SftpException if an sftp error occurs
   */
  public final boolean cd(String dir, boolean mkdir) throws SftpException {
    if (!StringUtils.hasText(dir)) {
      return true;
    }
    try {
      channelSftp.cd(dir);
      return true;
    } catch (SftpException e) {
      if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
        throw e;
      }
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
   * Tests whether a dir exists.
   *
   * @param dir the dir to test
   * @return true if the dir is exist
   * @throws SftpException if an sftp error occurs
   */
  public final boolean isDir(String dir) throws SftpException {
    try {
      return channelSftp.lstat(dir).isDir();
    } catch (SftpException e) {
      if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
        return false;
      }
      throw e;
    }
  }

  /**
   * @see SftpTemplate#download(String, OutputStream)
   */
  public final Void download(String from, OutputStream to) throws FileNotFoundException, SftpException {
    if (cd(from.substring(0, from.lastIndexOf(SEPARATOR) + 1), false)) {
      try {
        String fileName = from.substring(from.lastIndexOf(SEPARATOR) + 1);
        AtomicBoolean exist = new AtomicBoolean();
        channelSftp.ls(fileName, entry -> {
          if (fileName.equals(entry.getFilename())) {
            exist.set(true);
          }
          return ChannelSftp.LsEntrySelector.BREAK;
        });
        if (!exist.get()) {
          throw new FileNotFoundException(from);
        }
        channelSftp.get(fileName, to);
      } catch (SftpException e) {
        if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
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
