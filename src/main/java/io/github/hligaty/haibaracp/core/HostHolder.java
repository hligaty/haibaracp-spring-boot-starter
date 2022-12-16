package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Multi-host connection control.
 *
 * @author hligaty
 */
public class HostHolder {
  private static final ThreadLocal<Record> THREADLOCAL = new ThreadLocal<>();
  private static Set<String> hostNames;

  public static LinkedHashMap<String, ClientProperties> initHostNames(LinkedHashMap<String, ClientProperties> clientPropertiesMap) {
    if (hostNames != null) {
      throw new UnsupportedOperationException("HostHolder hostNames unsupported modify");
    }
    hostNames = Collections.unmodifiableSet(clientPropertiesMap.keySet());
    return clientPropertiesMap;
  }

  /**
   * Return all host names.
   *
   * @return host names.
   * @see ClientProperties#getHosts()
   */
  public static Set<String> hostNames() {
    Assert.notNull(hostNames, "Not multiple hosts");
    return hostNames;
  }

  /**
   * Return the filtered host name.
   *
   * @param predicate filter condition.
   * @return host names.
   */
  public static Set<String> hostNames(Predicate<String> predicate) {
    Assert.notNull(hostNames, "Not multiple hosts");
    return hostNames.stream().filter(predicate).collect(Collectors.toSet());
  }

  /**
   * Switch the host connect currently bound to the thread. Only switch once.
   *
   * @param hostName host name.
   * @see ClientProperties#getHosts()
   */
  public static void changeHost(String hostName) {
    Assert.notNull(hostName, "hostName must not be null");
    THREADLOCAL.set(new Record(hostName, true));
  }

  /**
   * Switch the host connect currently bound to the thread.
   *
   * @param hostName host name.
   * @param autoClose If true, the thread bound value is automatically cleared.
   * @see ClientProperties#getHosts()
   */
  public static void changeHost(String hostName, boolean autoClose) {
    Assert.notNull(hostName, "hostName must not be null");
    THREADLOCAL.set(new Record(hostName, autoClose));
  }

  /**
   * Clear the host bound to the thread.
   */
  public static void clearHost() {
    THREADLOCAL.remove();
  }

  protected static String getHostName() {
    Record record;
    Assert.notNull(record = THREADLOCAL.get() , "Host name not set");
    return record.hostName;
  }

  protected static void clear() {
    Record record;
    if ((record = THREADLOCAL.get()) != null && record.autoClose) {
      THREADLOCAL.remove();
    }
  }

  static class Record {
    String hostName;
    boolean autoClose;

    Record(String hostName, boolean autoClose) {
      this.hostName = hostName;
      this.autoClose = autoClose;
    }
  }
}
