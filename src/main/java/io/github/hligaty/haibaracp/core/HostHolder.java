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
  private static Set<String> hostKeys;

  public static LinkedHashMap<String, ClientProperties> initHostKeys(LinkedHashMap<String, ClientProperties> clientPropertiesMap) {
    if (hostKeys != null) {
      throw new UnsupportedOperationException("HostHolder hostKeys unsupported modify");
    }
    hostKeys = Collections.unmodifiableSet(clientPropertiesMap.keySet());
    return clientPropertiesMap;
  }

  /**
   * Return all host keys.
   *
   * @return host keys.
   * @see ClientProperties#getHosts()
   */
  public static Set<String> hostKeys() {
    if (hostKeys == null) {
      throw new NullPointerException("Not multiple hosts");
    }
    return hostKeys;
  }

  /**
   * Return the filtered host key.
   *
   * @param predicate filter condition.
   * @return host keys.
   */
  public static Set<String> hostKeys(Predicate<String> predicate) {
    if (hostKeys == null) {
      throw new NullPointerException("Not multiple hosts");
    }
    return hostKeys.stream().filter(predicate).collect(Collectors.toSet());
  }

  /**
   * Switch the host connect currently bound to the thread. Only switch once.
   *
   * @param hostKey host key.
   * @see ClientProperties#getHosts()
   */
  public static void changeHost(String hostKey) {
    THREADLOCAL.set(new Tuple2(hostKey, true));
  }

  /**
   * Switch the host connect currently bound to the thread.
   *
   * @param hostKey host key.
   * @param autoClose If true, the thread bound value is automatically cleared.
   * @see ClientProperties#getHosts()
   */
  public static void changeHost(String hostKey, boolean autoClose) {
    THREADLOCAL.set(new Tuple2(hostKey, autoClose));
  }

  /**
   * Clear the hostkey bound to the thread.
   */
  public static void clearHostKey() {
    THREADLOCAL.remove();
  }

  protected static String getHostKey() {
    Tuple2 tuple2 = THREADLOCAL.get();
    if (tuple2 == null) {
      throw new NullPointerException("Host key not set");
    }
    return tuple2.hostKey;
  }

  protected static void clear() {
    Tuple2 tuple2;
    if ((tuple2 = THREADLOCAL.get()) != null && tuple2.autoClose) {
      THREADLOCAL.remove();
    }
  }

  static class Tuple2 {
    public String hostKey;
    public boolean autoClose;

    public Tuple2(String hostKey, boolean autoClose) {
      this.hostKey = hostKey;
      this.autoClose = autoClose;
    }
  }
}
