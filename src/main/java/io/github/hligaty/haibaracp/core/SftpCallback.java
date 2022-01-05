package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.lang.Nullable;

/**
 * Callback interface for Sftp code. To be used with {@link SftpTemplate}'s
 * execution methods, often as anonymous classes within a method implementation.
 * Usually, used for chaining several operations together ChannelSftp.cd/put/get etc....
 *
 * @author hligaty
 * @param <T> the result type
 * @see SftpTemplate
 */
@FunctionalInterface
public interface SftpCallback<T> {
  /**
   * Gets called by {@link SftpTemplate} with an active Sftp connection. Does not need to care about activating or
   * closing the connection or handling exceptions.
   *
   * @param channelSftp active Sftp channel
   * @return a result object or null if none
   * @throws Exception if thrown by the youself code
   */
  @Nullable
  T doInSftp(ChannelSftp channelSftp) throws Exception;
}
