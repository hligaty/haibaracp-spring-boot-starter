[English](README.md) | [中文](README_zh_CN.md)

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

> **Welcome to use and Star support. If you encounter problems during use,  you can raise an Issue and I will try my best to improve it**

##  Introduce 

HaibaraCP is an SFTP connection pool, which supports password and key  login and multiple Host connections, and provides an easy-to-use  `SftpTemplate`. SFTP uses SSH to establish connections, but the number of  SSH connections is limited by default. Connections other than 10 will  have a 30% probability of connection failure. When there are more than  100 connections, it will refuse to create new connections. Therefore,  avoid frequent creation of new connections. 

## Maven repository

Spring boot 2 and Commons-Pool 2.6.0 and above are supported.

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

## Configuration

See the automatic prompt of the development tool for detailed description of configuration properties.

### Password login

```yml
sftp:
  host: localhost
  port: 22
  username: root
  password: 123456
  kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```
### Key login

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

### Multiple hosts

For example, two hosts, one password login and one key login:

```
sftp:
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

### Upload

```java
try (InputStream inputStream1 = Files.newInputStream(Paths.get("D:\\aptx4869.docx"));
     InputStream inputStream2 = Files.newInputStream(Paths.get("D:\\aptx4869.pdf"));
     InputStream inputStream3 = Files.newInputStream(Paths.get("D:\\aptx4869.doc"))) {
  // upload D:\\aptx4869.docx to /home/haibara/aptx4869.docx
  sftpTemplate.upload(inputStream1, "/home/haibara/aptx4869.docx");
  
  // upload D:\\aptx4869.pdf to /root/haibara/aptx4869.pdf
  sftpTemplate.upload(inputStream2, "haibara/aptx4869.pdf");
  
  // upload D:\\aptx4869.doc to /root/aptx4869.doc
  sftpTemplate.upload(inputStream3, "aptx4869.doc");
}
```

The `upload(InputStream from, String to)` method will check the upload  directory level by level (if it is a directory format), the directory  will be created if it does not exist, and the file will be uploaded  after entering the uploaded directory.

The method does not actively  close the stream, please close it manually.

### Download

```java
// download /home/haibara/aptx4869.docx to D:\\aptx4869.docx
sftpTemplate.download("/home/haibara/aptx4869.docx", Paths.get("D:\\aptx4869.docx"));
try (OutputStream outPutStream2 = Files.newOutputStream(Paths.get("D:\\aptx4869.pdf"));
         OutputStream outPutStream3 = Files.newOutputStream(Paths.get("D:\\aptx4869.doc"))) {
  // download /root/haibara/aptx4869.pdf to D:\\aptx4869.pdf
  sftpTemplate.download("haibara/aptx4869.pdf", outPutStream2);
  
  // download /root/aptx4869.doc to D:\\aptx4869.doc
  sftpTemplate.download("aptx4869.doc", outPutStream3);
}
```

When downloading a file, it will check the download directory level by  level (if it is in a directory format), and download the file after  entering the directory, but if a certain level of directory does not  exist or the file does not exist after entering the directory, the to  file `FileNotFoundException` will be thrown immediately, if If the type  of `Path` is not an absolute path, a `FileNotFoundException` will be  thrown immediately. The method will not actively close the  `outPutStream` stream of the downloaded (output) file, please close it  manually.

### Execute

`execute(SftpCallback action)` is used to customize SFTP operations,  such as viewing the SFTP default directory (for other uses of  ChannelSftp, please refer to the API of jsch):

```java
String dir = sftpTemplate.execute(ChannelSftp::pwd);
```

###  Multiple hosts

To use SftpTemplate in the connection pool of multiple connections from different hosts, you need to specify the connection to be used for HaibaraCP, otherwise a `NullPointerException` will be thrown. The following describes how to specify the connection (examples use the configuration in the `Configuration-Multiple Host` chapter to explain ):

- `HostHolder.changeHost(string)`: Specify the connection to be used next  time through hostkey (that is, the key in the map under the specified  configuration file sftp.hosts. The following hostkey will not be  explained repeatedly). Note that it can only specify the next  connection! ! !

```
HostHolder.changeHost("remote-1");
// success
sftpTemplate.execute(ChannelSftp::pwd);
// NullPointerException
sftpTemplate.execute(ChannelSftp::pwd);
```

- `HostHolder.changeHost(string, boolean)`: It is used when calling the  same host connection continuously to avoid setting the hostkey once when executing SftpTemplate once. Pay attention to use with  `HostHolder.clearHostKey()`! ! !

```java
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

-  `HostHolder. Hostkeys() ` and ` hostholder Hostkeys (predict < string >) `: get the keys of all or filtered host connections. The two connection switching methods described above need to display the specified hostkey, but sometimes the configured n host connections need to be executed in batch. At this time, all or filtered hostkey sets can be obtained through this method

```java
// Get all hostkeys starting with "remote-"
for (String hostKey : HostHolder.hostKeys(s -> s.startsWith("remote-"))) {
  HostHolder.changeHost(hostKey);
  try (InputStream inputStream1 = Files.newInputStream(Paths.get("D:\\aptx4869.docx"))) {
    sftpTemplate.upload(inputStream1, "/home/haibara/aptx4869.docx");
  }
}
```

## Problem

- JSchException: invalid privatekey：https://github.com/mwiede/jsch/issues/12#issuecomment-662863338

## Thanks for free JetBrains Open Source license

<a href="https://www.jetbrains.com/?from=Mybatis-PageHelper" target="_blank">
<img src="https://user-images.githubusercontent.com/1787798/69898077-4f4e3d00-138f-11ea-81f9-96fb7c49da89.png" height="200"/></a>

