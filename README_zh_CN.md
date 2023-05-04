[English](README.md) | [中文](README_zh_CN.md)

# HaibaraCP

<p align="center">
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/JDK-8+-green?logo=java&amp;logoColor=white"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/blob/master/LICENSE"><img src="https://img.shields.io/github/license/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://api.github.com/repos/hligaty/haibaracp-spring-boot-starter/releases/latest"><img src="https://img.shields.io/github/v/release/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/stargazers"><img src="https://img.shields.io/github/stars/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/network/members"><img src="https://img.shields.io/github/forks/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/issues?q=is%3Aissue+is%3Aclosed"><img src="https://img.shields.io/github/issues-closed-raw/hligaty/haibaracp-spring-boot-starter"></a>
</p>

> **Github：[hligaty/haibaracp-spring-boot-starter: SFTP Connect Pool (github.com)](https://github.com/hligaty/haibaracp-spring-boot-starter)**

> **Gitee：[haibaracp-spring-boot-starter: SFTP Connect Pool (gitee.com)](https://gitee.com/hligy/haibaracp-spring-boot-starter)**

> **欢迎使用和Star支持，如使用过程中碰到问题，可以提出Issue，我会尽力完善**

## 介绍

HaibaraCP 是一个 SFTP 的 SpringBoot Starter，支持密码和密钥登录以及多个 Host 连接，并提供和 RedisTemplate 一样优雅的 SftpTemplate。SFTP 通过 SSH 建立连接，而 SSH 连接数默认是有限的，10 个以外的连接将有 30 % 的概率连接失败，当超过 100 个连接时将拒绝创建新连接，因此要避免频繁创建新连接。

## Maven 依赖

| spring boot version | haibaracp |
| :-----------------: | :-------: |
|        2.x.x        |   1.2.3   |
|        3.x.x        |   2.0.0   |

依赖 Apache commons-pool2：

```xml
<dependency>
    <groupId>io.github.hligaty</groupId>
    <artifactId>haibaracp-spring-boot-starter</artifactId>
    <version>x.x.x</version>
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
  enabled-log: false
  host: localhost
  port: 22
  username: root
  password: 123456
  kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```
### 密钥登录

```yml
sftp:
  enabled-log: false
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

```yml
sftp:
  enabled-log: false
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

## 用法

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

- 所有方法都可能抛出 `SftpException`，这通常代表连接出问题了，也可能是你上传或下载的文件不存在。
- sftp 操作可能会改变工作目录，因此在连接返回给池前，框架会重置工作目录为原始目录。注意这只会重置远端工作路径，不会重置本地工作路径（通常你并不关心本地工作路径）。

下面的介绍全部使用 `配置` 章节中的配置进行说明，因此初始工作目录是 `/root`。

### upload

上传文件，该方法会递归创建上传的远程文件所在的父目录。

```java
// 上传 D:\\aptx4869.docx 到 /home/haibara/aptx4869.docx
sftpTemplate.upload("D:\\aptx4869.docx", "/home/haibara/aptx4869.docx");

// 上传 D:\\aptx4869.pdf 到 /root/haibara/aptx4869.pdf
sftpTemplate.upload("D:\\aptx4869.pdf", "haibara/aptx4869.pdf");

// 上传 D:\\aptx4869.doc 到 /root/aptx4869.doc
sftpTemplate.upload("D:\\aptx4869.doc", "aptx4869.doc");
```

### download

下载文件，该方法只会创建下载的本地文件，不会创建本地文件的父目录。

```java
// 下载 /home/haibara/aptx4869.docx 到 D:\\aptx4869.docx
sftpTemplate.download("/home/haibara/aptx4869.docx", "D:\\aptx4869.docx");

// 下载 /root/haibara/aptx4869.pdf 到 D:\\aptx4869.pdf
sftpTemplate.download("haibara/aptx4869.pdf", "D:\\aptx4869.pdf");

// 下载 /root/aptx4869.doc 到 D:\\aptx4869.doc
sftpTemplate.download("aptx4869.doc", "D:\\aptx4869.doc");
```

### exists

```java
// 测试 /home/haibara/aptx4869.docx 是否存在
boolean result1 = sftpTemplate.exists("/home/haibara/aptx4869.pdf");
// 测试 /root/haibara/aptx4869.docx 是否存在
boolean result2 = sftpTemplate.exists("haibara/aptx4869.docx");
// 测试 /root/aptx4869.docx 是否存在
boolean result3 = sftpTemplate.exists("aptx4869.doc");
```

### list

```java
// 查看文件 /home/haibara/aptx4869.pdf
LsEntry[] list1 = sftpTemplate.list("/home/haibara/aptx4869.pdf");
// 查看文件 /root/haibara/aptx4869.docx
LsEntry[] list2 = sftpTemplate.list("haibara/aptx4869.docx");
// 查看文件 /root/aptx4869.doc
LsEntry[] list3 = sftpTemplate.list("aptx4869.doc");

// 查看目录 /home/haibara
LsEntry[] list4 = sftpTemplate.list("/home/haibara");
// 查看目录 /root/haibara
LsEntry[] list5 = sftpTemplate.list("haibara");
```

### execute

`execute(SftpCallback<T> action)` 用于执行自定义 SFTP 操作，比如查看 SFTP 默认目录（关于 ChannelSftp 的其他用法请参考 jsch 的 API）：

```java
String dir = sftpTemplate.execute(ChannelSftp::pwd);
```

Jsch 的 ChannelSftp 提供了很多基础的方法，对于 execute 来说有点不太便捷，你可以使用 ChannelSftpWrapper 类来更便捷的使用 ChannelSftp，SftpTemplate 所有的方法也都是通过它来实现的。

### executeWithoutResult

`executeWithoutResult(SftpCallbackWithoutResult action)`用于执行自定义没有返回值的SFTP操作，比如下载文件（ChannelSftp的其他用途，请参考 jsch 的 API）：

```java
try (OutputStream outputStream = Files.newOutputStream(Paths.get("/root/aptx4869.doc"))) {
  sftpTemplate.executeWithoutResult(channelSftp -> channelSftp.get("aptx4869.doc", outputStream));
}
```

###  多 Host

在多 Host 使用  SftpTemplate 需要为 HaibaraCP 指定将要使用的连接，否则将抛出 `NullPointerException`，下面介绍了如何指定连接：

- `HostHolder.changeHost(string)` ：通过 hostName （即指定配置文件 sftp.hosts 下 map 中的 key。后面的 hostName 不再重复说明） 指定下次使用的连接。注意它只能指定下一次的连接！！！

```java
HostHolder.changeHost("remote-1");
// 成功打印 remote-1 对应连接的原始目录
sftpTemplate.execute(ChannelSftp::pwd);
// 第二次执行失败，抛出空指针
sftpTemplate.execute(ChannelSftp::pwd);
```

- `HostHolder.changeHost(string, boolean)`：连续调用相同 host 连接时使用，避免执行一次 SftpTemplate 就要设置一次 hostName。注意要配合 `HostHolder.clearHost()` 使用！！！

```java
HostHolder.changeHost("remote-1", false);
try {
  sftpTemplate.upload("D:\\aptx4869.docx", "/home/haibara/aptx4869.docx");
  sftpTemplate.upload("D:\\aptx4869.pdf", "haibara/aptx4869.pdf");
  sftpTemplate.upload("D:\\aptx4869.doc", "aptx4869.doc");
} finally {
  HostHolder.clearHost();
}
```

-  `HostHolder.hostNames()` 与 `HostHolder.hostNames(Predicate<String>)`：获取所有或过滤后的 host 连接的 name。前面介绍的两种切换连接的方式都要显示指定 hostName，但有时需要批量执行配置的 n 个 host 连接，此时可以通过该方法获取所有或过滤后的 hostName 集合。

```java
// 获取所有以“remote-”开头的 hostName
for (String hostName : HostHolder.hostNames(s -> s.startsWith("remote-"))) {
  HostHolder.changeHost(hostName);
  sftpTemplate.upload("D:\\aptx4869.docx", "/home/haibara/aptx4869.docx");
}
```

## 密钥格式

OpenSSH 自 7.8 起，默认的密钥格式由

```
-----BEGIN RSA PRIVATE KEY-----
xxx
-----END RSA PRIVATE KEY-----
```

变更为：

```
-----BEGIN OPENSSH PRIVATE KEY-----
xxx
-----END OPENSSH PRIVATE KEY-----
```

Haibaracp 使用 Jsch 作为 SFTP 的实现，而 Jsch 不支持新的格式，因此你需要一些小改动：

1. 如果密钥由你生成，仅需在 `ssh-keygen` 命令后加上 `-m PEM` 以生成旧版的密钥继续使用。
2. 如果你无法自己获取旧版密钥，此时必须更改 POM，将 Jcraft 的 Jsch 更改为其他人 fork 的 Jsch 库（Jcraft 自 2018 年推送的 0.1.55 版本后没有任何的消息），比如：

```xml
<dependency>
    <groupId>io.github.hligaty</groupId>
    <artifactId>haibaracp-spring-boot-starter</artifactId>
    <version>1.2.3</version>
    <exclusions>
        <exclusion>
            <groupId>com.jcraft</groupId>
            <artifactId>jsch</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>com.github.mwiede</groupId>
    <artifactId>jsch</artifactId>
    <version>0.1.72</version>
</dependency>
```

否则你将看到 [JSchException: invalid privatekey](https://github.com/mwiede/jsch/issues/12#issuecomment-662863338)。

## 路线图

- [ ] 提供一个 SessionFactory Bean 用于自定义创建连接

## Thanks for free JetBrains Open Source license

<a href="https://www.jetbrains.com/?from=Mybatis-PageHelper" target="_blank">
<img src="https://user-images.githubusercontent.com/1787798/69898077-4f4e3d00-138f-11ea-81f9-96fb7c49da89.png" height="200"/></a>

