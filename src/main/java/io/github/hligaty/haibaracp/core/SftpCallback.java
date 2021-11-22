package io.github.hligaty.haibaracp.core;

import org.springframework.lang.Nullable;

/**
 * @author hligaty
 */
@FunctionalInterface
public interface SftpCallback<T> {
  @Nullable
  T doInSftp(SftpClient client) throws Exception;
}
