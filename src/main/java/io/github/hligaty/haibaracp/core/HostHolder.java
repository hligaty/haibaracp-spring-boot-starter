package io.github.hligaty.haibaracp.core;

import io.github.hligaty.haibaracp.config.ClientProperties;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 切换 host
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
   * 返回配置文件 sftp.hosts 下配置的所有 key。
   * 你可以为 key 自定义命名规则，然后对该方法返回的 key 集合按照自定义规则过滤出想要的 key 分组。
   *
   * @return 多 Host 的 key
   */
  public static Set<String> hostKeys() {
    if (hostKeys == null) {
      throw new NullPointerException("Not multiple hosts");
    }
    return hostKeys;
  }

  /**
   * 过滤出指定的 hostkeys
   *
   * @param predicate hostkey 命名规则
   * @return hostkeys
   */
  public static Set<String> hostKeys(Predicate<String> predicate) {
    if (hostKeys == null) {
      throw new NullPointerException("Not multiple hosts");
    }
    return hostKeys.stream().filter(predicate).collect(Collectors.toSet());
  }

  /**
   * 选择 host
   *
   * @param hostKey HostKey
   */
  public static void changeHost(String hostKey) {
    THREADLOCAL.set(new Tuple2(hostKey, true));
  }

  /**
   * 自定义 hostKey 是否自动清除，连续调用同一个 host 时使用。
   * autoClose 为 false 需要手动调用 {@link HostHolder#clearHostKey()} 清除， 否则之后的 host 都是这次 hostKey 对应的 host
   *
   * @param hostKey HostKey
   * @param autoClose 自动清除 HostKey
   */
  public static void changeHost(String hostKey, boolean autoClose) {
    THREADLOCAL.set(new Tuple2(hostKey, autoClose));
  }

  /**
   * 清除 {@link HostHolder#changeHost(String, boolean)} 设置的 hostkey
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
    if ((tuple2 = THREADLOCAL.get()) == null) {
      return;
    }
    if (tuple2.autoClose) {
      THREADLOCAL.remove();
    }
  }

  static class Tuple2 {
    public String hostKey;
    public boolean autoClose = true;

    public Tuple2(String hostKey, boolean autoClose) {
      this.hostKey = hostKey;
      this.autoClose = autoClose;
    }
  }
}
