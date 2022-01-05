[English](readme/README.md) | [中文](readme/README_zh_CN.md)

# HaibaraCP

<p align="center">
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/JDK-8+-green?logo=java&amp;logoColor=white"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/blob/master/LICENSE"><img src="https://img.shields.io/github/license/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://api.github.com/repos/hligaty/haibaracp-spring-boot-starter/releases/latest"><img src="https://img.shields.io/github/v/release/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/stargazers"><img src="https://img.shields.io/github/stars/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/network/members"><img src="https://img.shields.io/github/forks/hligaty/haibaracp-spring-boot-starter"></a>
</p>

> **Github：[hligaty/haibaracp-spring-boot-starter: SFTP Connect Pool (github.com)](https://github.com/hligaty/haibaracp-spring-boot-starter)**

> **Gitee：[haibaracp-spring-boot-starter: SFTP Connect Pool (gitee.com)](https://gitee.com/hligy/haibaracp-spring-boot-starter)**

> **欢迎使用和Star支持，如使用过程中碰到问题，可以提出Issue，我会尽力完善**

## 介绍

HaibaraCP 是一个 SFTP 连接池，支持密码和密钥登录以及多个 Host 连接，并提供和 RedisTemplate 一样优雅的 SftpTemplate。SFTP 通过 SSH 建立连接，而 SSH 连接数默认是有限的，10 个以外的连接将有 30 % 的概率连接失败，当超过 100 个连接时将拒绝创建新连接，因此要避免频繁创建新连接。

## Maven 依赖

支持 springboot2 和 commons-pool2-2.6.0 及以上版本。

```xml
<dependency>
    <groupId>io.github.hligaty</groupId>
    <artifactId>haibaracp-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

## 配置

详细的配置属性说明见开发工具的自动提示。

### 密码登录

```yml
sftp:
  host: localhost
  port: 22
  username: root
  password: 123456
  kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```
### 密钥登录

```yml
sftp:
  host: localhost
  port: 22
  username: root
  strict-host-key-checking: true
  key-path: C:\\Users\\user\\.ssh\\id_rsa
  password: Jui8cv@kK9!0
  kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```

### 多 Host

比如两个 Host，一个密码登录，一个密钥登录：

```
sftp:
  hosts:
    # 地址的名字，你可以通过它来切换连接
    remote-1:
      host: 127.0.0.1
      port: 22
      username: root
      password: 123456
      kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
    local-1:
      host: 127.0.0.1
      port: 22
      username: root
      strict-host-key-checking: true
      key-path: C:\\Users\\user\\.ssh\\id_rsa
      password: Jui8cv@kK9!0
      kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```

### 连接池（可以不配置）

单 Host 连接池配置：

```yml
sftp:
  pool:
    min-idle: 1
    max-idle: 8
    max-active: 8
    max-wait: -1
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 600000
    min-evictable-idle-time-millis: 1800000
```

多 Host 连接池配置：

```yml
sftp:
  pool:
    min-idle-per-key: 1
    max-idle-per-key: 8
    max-active-per-key: 8
    max-active: 8
    max-wait: -1
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 600000
    min-evictable-idle-time-millis: 1800000
```

## 使用

HaibaraCP 提供 SftpTemplate 类，它与 `spring-boot-starter-data-redis`  提供的 RedisTemplate 使用方法相同，任意方式注入即可使用：

```java
@Component
public class XXXService {
  private final SftpTemplate sftpTemplate;

  public XXXService(SftpTemplate sftpTemplate) {
    this.sftpTemplate = sftpTemplate;
  }

  public void service(String from, OutputStream to) throws Exception {
    sftpTemplate.download(from, to);
  }
}
```

## API

### 上传文件

```java
// root 账户 SFTP 登录后目录为 /root
try (InputStream inputStream1 = Files.newInputStream(Paths.get("D:\\aptx4869.docx"));
     InputStream inputStream2 = Files.newInputStream(Paths.get("D:\\aptx4869.pdf"));
     InputStream inputStream3 = Files.newInputStream(Paths.get("D:\\aptx4869.doc"))) {
  // 上传 D:\\aptx4869.docx 到 /home/haibara/aptx4869.docx
  sftpTemplate.upload(inputStream1, "/home/haibara/aptx4869.docx");

  // 上传 D:\\aptx4869.pdf 到 /root/haibara/aptx4869.pdf
  sftpTemplate.upload(inputStream2, "haibara/aptx4869.pdf");

  // 上传 D:\\aptx4869.doc 到 /root/aptx4869.doc
  sftpTemplate.upload(inputStream3, "aptx4869.doc");
}
```

`upload(InputStream from, String to)` 方法会逐级检查上传目录（如果是目录格式），目录不存在就会创建，直到进入上传的目录后上传文件。

方法不会主动关闭流，请手动关闭。

### 下载文件

```java
// root 账户 SFTP 登录后目录为 /root
// 下载 /home/haibara/aptx4869.docx 到 D:\\aptx4869.docx
sftpTemplate.download("/home/haibara/aptx4869.docx", Paths.get("D:\\aptx4869.docx"));
try (OutputStream outPutStream2 = Files.newOutputStream(Paths.get("D:\\aptx4869.pdf"));
         OutputStream outPutStream3 = Files.newOutputStream(Paths.get("D:\\aptx4869.doc"))) {
  // 下载 /root/haibara/aptx4869.pdf 到 D:\\aptx4869.pdf
  sftpTemplate.download("haibara/aptx4869.pdf", outPutStream2);

  // 下载 /root/aptx4869.doc 到 D:\\aptx4869.doc
  sftpTemplate.download("aptx4869.doc", outPutStream3);
}
```

下载文件一样会逐级检查下载目录（如果是目录格式），并在进入目录后下载文件，但如果某级目录不存在或进入目录后发现文件不存在会立即抛出 to 文件 `FileNotFoundException`，如果传入的 `Path` 类型不是绝对路径也会立即抛出 `FileNotFoundException` 。

方法不会主动关闭下载（输出）文件的 `outPutStream` 流，请手动关闭。

### 自定义

`execute(SftpCallback<T> action)` 用于自定义 SFTP 操作，比如查看 SFTP 默认目录（关于 ChannelSftp 的其他用法请参考 jsch 的 API）：

```java
String dir = sftpTemplate.execute(ChannelSftp::pwd);
```

###  多 Host

在多 Host 使用  SftpTemplate 需要为 HaibaraCP 指定将要使用的连接，否则将抛出 `NullPointerException`，下面介绍了如何指定连接（例子使用 `配置-多Host` 章节中的配置进行说明）：

- `HostHolder.changeHost(string)` ：通过 hostkey （即指定配置文件 sftp.hosts 下 map 中的 key。后面的 hostkey 不再重复说明） 指定下次使用的连接。注意它只能指定下一次的连接！！！

```
HostHolder.changeHost("remote-1");
// 成功打印 remote-1 对应连接的原始目录
sftpTemplate.execute(ChannelSftp::pwd);
// 第二次执行失败，抛出空指针
sftpTemplate.execute(ChannelSftp::pwd);
```

- `HostHolder.changeHost(string, boolean)`：连续调用相同 host 连接时使用，避免执行一次 SftpTemplate 就要设置一次 hostkey。注意要配合 `HostHolder.clearHostKey()` 使用！！！

```java
// 手动选择 hostkey
HostHolder.changeHost("remote-1", false);
try (InputStream inputStream1 = Files.newInputStream(Paths.get("D:\\aptx4869.docx"));
     InputStream inputStream2 = Files.newInputStream(Paths.get("D:\\aptx4869.pdf"));
     InputStream inputStream3 = Files.newInputStream(Paths.get("D:\\aptx4869.doc"))) {
  sftpTemplate.upload(inputStream1, "/home/haibara/aptx4869.docx");
  sftpTemplate.upload(inputStream2, "haibara/aptx4869.pdf");
  sftpTemplate.upload(inputStream3, "aptx4869.doc");
} finally {
  HostHolder.clearHostKey();
}
```

-  `HostHolder.hostKeys()` 与 `HostHolder.hostKeys(Predicate<String>)`：获取所有或过滤后的 host 连接的 key。前面介绍的两种切换连接的方式都要显示指定 hostkey，但有时需要批量执行配置的 n 个 host 连接，此时可以通过该方法获取所有或过滤后的 hostkey 集合。

```java
// 获取所有以“remote-”开头的 hostkey
for (String hostKey : HostHolder.hostKeys(s -> s.startsWith("remote-"))) {
  HostHolder.changeHost(hostKey);
  try (InputStream inputStream1 = Files.newInputStream(Paths.get("D:\\aptx4869.docx"))) {
    sftpTemplate.upload(inputStream1, "/home/haibara/aptx4869.docx");
  }
}
```

## 常见问题

- JSchException: invalid privatekey：https://github.com/mwiede/jsch/issues/12#issuecomment-662863338

## Thanks for free JetBrains Open Source license

<a href="https://www.jetbrains.com/?from=Mybatis-PageHelper" target="_blank">
<img src="https://user-images.githubusercontent.com/1787798/69898077-4f4e3d00-138f-11ea-81f9-96fb7c49da89.png" height="200"/></a>
