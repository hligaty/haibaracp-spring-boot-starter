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

> **Welcome to use and Star support. If you encounter problems during use,  you can raise an Issue and I will try my best to improve it**

##  Introduce 

HaibaraCP is a SpringBoot Starter for SFTP, which supports password and key  login and multiple Host connections, and provides an easy-to-use  `SftpTemplate`. SFTP uses SSH to establish connections, but the number of  SSH connections is limited by default. Connections other than 10 will  have a 30% probability of connection failure. When there are more than  100 connections, it will refuse to create new connections. Therefore,  avoid frequent creation of new connections. 

## Maven repository

Spring boot 2 and Commons-Pool 2.6.0 and above are supported.

```xml
<dependency>
    <groupId>io.github.hligaty</groupId>
    <artifactId>haibaracp-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

## Configuration

See the automatic prompt of the development tool for detailed description of configuration properties.

### Password login

```yml
sftp:
  enabled-log: false
  host: localhost
  port: 22
  username: root
  password: 123456
  kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```
### Key login

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

### Multiple hosts

For example, two hosts, one password login and one key login:

```yml
sftp:
  enabled-log: false
  hosts:
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

### Connect Pool

One host：

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

Multiple hosts：

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

## Use

HaibaraCP provides the `SftpTemplate ` class, which is used in the same way as the RedisTemplate provided by spring-boot-starter-data-redis, and it can be used by injecting it in any way:

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

- All methods may throw `SftpException`, which usually means there is a problem with the connection, or the file you uploaded or downloaded does not exist.
- SFTP operations can change the working directory, so the framework resets the working directory to the original directory before the connection is returned to the pool. Note that this only resets the remote working path, not the local working path (usually you don't care about the local working path).

The following instructions are all explained using the configuration in the `Configuration` section, so the work directory is `/root`.

### upload

Upload a file, the method will recursively create the parent directory where the uploaded remote file is located.

```java
// upload D:\\aptx4869.docx to /home/haibara/aptx4869.docx
sftpTemplate.upload("D:\\aptx4869.docx", "/home/haibara/aptx4869.docx");

// upload D:\\aptx4869.pdf to /root/haibara/aptx4869.pdf
sftpTemplate.upload("D:\\aptx4869.pdf", "haibara/aptx4869.pdf");

// upload D:\\aptx4869.doc to /root/aptx4869.doc
sftpTemplate.upload("D:\\aptx4869.doc", "aptx4869.doc");
```

### download

Download a file, the method will only create the downloaded local file, not the parent directory of the local file.

```java
// download /home/haibara/aptx4869.docx to D:\\aptx4869.docx
sftpTemplate.download("/home/haibara/aptx4869.docx", "D:\\aptx4869.docx");

// download /root/haibara/aptx4869.pdf to D:\\aptx4869.pdf
sftpTemplate.download("haibara/aptx4869.pdf", "D:\\aptx4869.pdf");

// download /root/aptx4869.doc to D:\\aptx4869.doc
sftpTemplate.download("aptx4869.doc", "D:\\aptx4869.doc");
```

### exists

Tests whether a file exists.

```java
// Tests whether /home/haibara/aptx4869.pdf exists
boolean result1 = sftpTemplate.exists("/home/haibara/aptx4869.pdf");
// Tests whether /root/haibara/aptx4869.docx exists
boolean result2 = sftpTemplate.exists("haibara/aptx4869.docx");
// Tests whether /root/aptx4869.doc exists
boolean result3 = sftpTemplate.exists("aptx4869.doc");
```

### list

View a list of files or directories.

```java
// View file /home/haibara/aptx4869.pdf
LsEntry[] list1 = sftpTemplate.list("/home/haibara/aptx4869.pdf");
// View file /root/haibara/aptx4869.docx
LsEntry[] list2 = sftpTemplate.list("haibara/aptx4869.docx");
// View file /root/aptx4869.doc
LsEntry[] list3 = sftpTemplate.list("aptx4869.doc");

// View dir list /home/haibara
LsEntry[] list4 = sftpTemplate.list("/home/haibara");
// View dir list /root/haibara
LsEntry[] list5 = sftpTemplate.list("haibara");
```

### execute

`execute(SftpCallback action)` is used to perform custom SFTP operations,  such as viewing the SFTP default directory (for other uses of  ChannelSftp, please refer to the API of jsch):

```java
String dir = sftpTemplate.execute(ChannelSftp::pwd);
```

Jsch's channelsftp provides many basic methods, which are a little inconvenient for execute. You can use channelsftpwrapper class to use channelsftp more conveniently. All methods of sftptemplate are also implemented through it.

### executeWithoutResult

`executeWithoutResult(SftpCallbackWithoutResult action)` is used to perform custom SFTP operations with no return value, such as viewing the default SFTP directory (for other uses of ChannelSftp, please refer to jsch&#39;s API):

```java
sftpTemplate.executeWithoutResult(channelSftp -> System.out.println(channelSftp.getHome()));
```

###  Multiple hosts

To use SftpTemplate in the connection pool of multiple connections from different hosts, you need to specify the connection to be used for HaibaraCP, otherwise a `NullPointerException` will be thrown. The following describes how to specify the connection:

- `HostHolder.changeHost(string)`: Specify the connection to be used next  time through hostName (that is, the key in the map under the specified  configuration file sftp.hosts. The following hostName will not be  explained repeatedly). Note that it can only specify the next  connection! ! !

```java
HostHolder.changeHost("remote-1");
// success
sftpTemplate.execute(ChannelSftp::pwd);
// NullPointerException
sftpTemplate.execute(ChannelSftp::pwd);
```

- `HostHolder.changeHost(string, boolean)`: It is used when calling the  same host connection continuously to avoid setting the hostName once when executing SftpTemplate once. Pay attention to use with  `HostHolder.clearHost()`! ! !

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

-  `HostHolder.hostNames() ` and ` HostHolder.hostNames(predict < string >) `: get the Names of all or filtered host connections. The two connection switching methods described above need to display the specified hostName, but sometimes the configured n host connections need to be executed in batch. At this time, all or filtered hostName sets can be obtained through this method

```java
// Get all host Name starting with "remote-"
for (String hostName : HostHolder.hostNames(s -> s.startsWith("remote-"))) {
  HostHolder.changeHost(hostName);
  sftpTemplate.upload("D:\\aptx4869.docx", "/home/haibara/aptx4869.docx");
}
```

## Key format

 The key format generated by openssh 7.8 is changed from

```
-----BEGIN RSA PRIVATE KEY-----
xxx
-----END RSA PRIVATE KEY-----
```

to

```
-----BEGIN OPENSSH PRIVATE KEY-----
xxx
-----END OPENSSH PRIVATE KEY-----
```

Haibaracp uses jsch as the implementation of SFTP, and jsch does not support the new format, so you need some small changes:

1. If the key is generated by you, just add ` - M PEM` after the `ssh-keygen` command to generate the old key and continue to use it.
2. If you can't get the old version key by yourself, you must change the POM and change the jsch of jcraft to the jsch Library of others' fork (there is no message after the 0.1.55 version pushed by jcraft in 2018), for example:

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

Otherwise you will see [JSchException: invalid privatekey](https://github.com/mwiede/jsch/issues/12#issuecomment-662863338).

## Future

- A future version may separate the implementation of HaibaraCP and SFTP completely (like Spring Data Redis for Jedis and Lettuce, maybe more like Spring integration file), The real implementation is provided only if you need to manipulate the underlying connection. If implemented, you might create ShortHairCP as an abstraction, SherryCP as an FTP implementation, HaibaraCP as an SFTP implementation, At this point, the version may be 2.x.x.
- Support transactions.

## Thanks for free JetBrains Open Source license

<a href="https://www.jetbrains.com/?from=Mybatis-PageHelper" target="_blank">
<img src="https://user-images.githubusercontent.com/1787798/69898077-4f4e3d00-138f-11ea-81f9-96fb7c49da89.png" height="200"/></a>

