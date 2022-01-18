package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;

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
  private static final ThreadLocal<Tuple2> THREADLOCAL = new ThreadLocal<>();
  private static Set<String> hostNames;

  public static LinkedHashMap<String, ClientProperties> initHostKeys(LinkedHashMap<String, ClientProperties> clientPropertiesMap) {
    if (hostNames != null) {
      throw new UnsupportedOperationException("HostHolder hostNames unsupported modify");
    }
    hostNames = Collections.unmodifiableSet(clientPropertiesMap.keySet());
    return clientPropertiesMap;
  }

  /**
   * Return all host keys.
   *
   * @return host keys.
   * @see ClientProperties#getHosts()
   * @deprecated Use {@link #hostNames()}.
   */
  @Deprecated
  public static Set<String> hostKeys() {
    return hostNames();
  }

  /**
   * Return all host keys.
   *
   * @return host keys.
   * @see ClientProperties#getHosts()
   */
  public static Set<String> hostNames() {
    if (hostNames == null) {
      throw new NullPointerException("Not multiple hosts");
    }
    return hostNames;
  }

  /**
   * Return the filtered host key.
   *
   * @param predicate filter condition.
   * @return host keys.
   * @deprecated Use {@link #hostNames(Predicate)}.
   */
  @Deprecated
  public static Set<String> hostKeys(Predicate<String> predicate) {
    return hostNames(predicate);
  }

  /**
   * Return the filtered host key.
   *
   * @param predicate filter condition.
   * @return host keys.
   */
  public static Set<String> hostNames(Predicate<String> predicate) {
    if (hostNames == null) {
      throw new NullPointerException("Not multiple hosts");
    }
    return hostNames.stream().filter(predicate).collect(Collectors.toSet());
  }

  /**
   * Switch the host connect currently bound to the thread. Only switch once.
   *
   * @param hostName host key.
   * @see ClientProperties#getHosts()
   */
  public static void changeHost(String hostName) {
    THREADLOCAL.set(new Tuple2(hostName, true));
  }

  /**
   * Switch the host connect currently bound to the thread.
   *
   * @param hostName host key.
   * @param autoClose If true, the thread bound value is automatically cleared.
   * @see ClientProperties#getHosts()
   */
  public static void changeHost(String hostName, boolean autoClose) {
    THREADLOCAL.set(new Tuple2(hostName, autoClose));
  }

  /**
   * Clear the hostkey bound to the thread.
   * @deprecated Use {@link #clearHost()}.
   */
  @Deprecated
  public static void clearHostKey() {
    clearHost();
  }

  /**
   * Clear the hostkey bound to the thread.
   */
  public static void clearHost() {
    THREADLOCAL.remove();
  }

  protected static String getHostKey() {
    Tuple2 tuple2 = THREADLOCAL.get();
    if (tuple2 == null) {
      throw new NullPointerException("Host key not set");
    }
    return tuple2.hostName;
  }

  protected static void clear() {
    Tuple2 tuple2;
    if ((tuple2 = THREADLOCAL.get()) != null && tuple2.autoClose) {
      THREADLOCAL.remove();
    }
  }

  static class Tuple2 {
    public String hostName;
    public boolean autoClose;

    public Tuple2(String hostName, boolean autoClose) {
      this.hostName = hostName;
      this.autoClose = autoClose;
    }
  }
}
