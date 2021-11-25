package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.lang.Nullable;

/**
 * @author hligaty
 */
@FunctionalInterface
public interface SftpCallback<T> {
  /**
   * 执行自定义的 channelSftp 操作，由 {@link SftpTemplate#execute} 执行。
   * 不必担心连接池中的 channelSftp 与新建立连接的 channelSftp 状态不同，可以认为它是一个崭新的连接。
   *
   * @param channelSftp jsch channel
   * @return 返回值，可以为空
   * @throws Exception 调用的 channelSftp 出现 SftpException 或其他 Exception
   */
  @Nullable
  T doInSftp(ChannelSftp channelSftp) throws Exception;
}
