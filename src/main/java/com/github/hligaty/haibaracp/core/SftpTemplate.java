package com.github.hligaty.haibaracp.core;

import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class SftpTemplate {
  private static final Logger log = LoggerFactory.getLogger(SftpTemplate.class);
  private final SftpPool sftpPool;

  public SftpTemplate(SftpPool sftpPool) {
    this.sftpPool = sftpPool;
  }

  public <T> T execute(SftpCallback<T> action) throws Exception {
    Assert.notNull(action, "Callback object must not be null");
    SftpClient sftpClient = null;
    try {
      sftpClient = sftpPool.borrowObject();
      if (log.isDebugEnabled()) {
        log.debug("{}: Get client.", sftpClient.getClientInfo());
      }
      return action.doInSftp(sftpClient);
    } catch (FileNotFoundException e) {
      throw e;
    } catch (Exception e) {
      if (log.isDebugEnabled() && sftpClient != null) {
        log.debug("{}: Invalidate client.", sftpClient.getClientInfo());
      }
      sftpPool.invalidateObject(sftpClient);
      sftpClient = null;
      throw e;
    } finally {
      if (null != sftpClient) {
        if (log.isDebugEnabled()) {
          log.debug("{}: Return vlient.", sftpClient.getClientInfo());
        }
        sftpPool.returnObject(sftpClient);
      }
    }
  }

  /**
   * 将文件 dir 下载到 outputStream
   *
   * @param from 文件全路径
   * @param to   文件输出流
   * @throws Exception 目录、文件不存在或下载时出现意外
   */
  public void download(String from, OutputStream to) throws Exception {
    this.<Void>execute(sftpClient -> {
      if (sftpClient.cd(from.substring(0, from.lastIndexOf("/") + 1), false)) {
        try {
          sftpClient.getChannelSftp().get(from.substring(from.lastIndexOf("/") + 1), to);
        } catch (SftpException e) {
          if (e.id == 2) {
            throw new FileNotFoundException(from);
          }
          throw e;
        }
        return null;
      }
      throw new FileNotFoundException(from);
    });
  }

  /**
   * 上传 inputStream 到 dir，目录不存在时自动创建
   *
   * @param from 输入文件流
   * @param to   文件全路径
   * @throws SftpException 上传或切换目录时出现意外
   */
  public void upload(InputStream from, String to) throws Exception {
    upload(from, to, 0);
  }

  /**
   * 上传 inputStream 到 dir
   *
   * @param from 输入文件流
   * @param to   文件全路径
   * @param mode 模式
   * @throws SftpException 上传或切换目录时出现意外
   */
  public void upload(InputStream from, String to, int mode) throws Exception {
    this.<Void>execute(sftpClient -> {
      if (sftpClient.cd(to.substring(0, to.lastIndexOf("/") + 1), true)) {
        sftpClient.getChannelSftp().put(from, to.substring(to.lastIndexOf("/") + 1), mode);
        return null;
      }
      throw new FileNotFoundException(to);
    });
  }
}
