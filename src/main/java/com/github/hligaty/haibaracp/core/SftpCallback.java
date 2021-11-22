package com.github.hligaty.haibaracp.core;

import org.springframework.lang.Nullable;

/**
 * @author hligaty & haibara
 */
@FunctionalInterface
public interface SftpCallback<T> {
  @Nullable
  T doInSftp(SftpClient client) throws Exception;
}
