[English](README.md) | [中文](README_zh_CN.md)

# HaibaraCP

<p align="center">
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/JDK-17+-green?logo=java&amp;logoColor=white"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/blob/master/LICENSE"><img src="https://img.shields.io/github/license/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://api.github.com/repos/hligaty/haibaracp-spring-boot-starter/releases/latest"><img src="https://img.shields.io/github/v/release/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/stargazers"><img src="https://img.shields.io/github/stars/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/network/members"><img src="https://img.shields.io/github/forks/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/issues?q=is%3Aissue+is%3Aclosed"><img src="https://img.shields.io/github/issues-closed-raw/hligaty/haibaracp-spring-boot-starter"></a>
</p>

## Introduce 

HaibaraCP is a SpringBoot Starter for SFTP, which provides an easy-to-use  `SftpTemplate`. SFTP uses SSH to establish connections, but the number of  SSH connections is limited by default. Connections other than 10 will  have a 30% probability of connection failure. When there are more than  100 connections, it will refuse to create new connections. Therefore,  avoid frequent creation of new connections. 

## Maven repository

| spring boot version | haibaracp |
| :-----------------: | :-------: |
|        2.x.x        |   1.3.2   |
|        3.x.x        |   2.1.2   |
|        4.x.x        |   3.0.0   |

Dependence Apache commons-pool2:

```xml
<dependency>
    <groupId>io.github.hligaty</groupId>
    <artifactId>haibaracp-spring-boot-starter</artifactId>
    <version>3.0.0</version>
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
  connect-timeout: 5000ms
  channel-connect-timeout: 5000ms
  server-alive-interval: 30000ms
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

### Connect Pool

```yml
sftp:
  pool:
    enabled: true
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

## Usage

HaibaraCP provides the `SftpTemplate ` class, which is used in the same way as the RedisTemplate provided by spring-boot-starter-data-redis, and it can be used by injecting it in any way:

```java
@Component
public class XXXService {
  private final SftpTemplate<SftpSession> sftpTemplate;

  public XXXService(SftpTemplate<SftpSession> sftpTemplate) {
    this.sftpTemplate = sftpTemplate;
  }

  public void service(String from, OutputStream to) throws Exception {
    sftpTemplate.download(from, to);
  }
}
```

## API

- SFTP operations can change the working directory, so the framework resets the working directory to the original directory before the connection is returned to the pool. Note that this only resets the remote working path, not the local working path (usually you don't care about the local working path).

The following instructions are all explained using the configuration in the `Configuration` section, so the work directory is `/root`.

### upload

Upload a file. **Note: Version 3.0.0 removed the automatic parent directory creation feature. Please ensure the parent directory of the target path exists.**

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

`executeWithoutResult(SftpCallbackWithoutResult action)` is used to perform custom SFTP operations with no return value, such as download file (for other uses of ChannelSftp, please refer to jsch&#39;s API):

```java
try (OutputStream outputStream = Files.newOutputStream(Paths.get("/root/aptx4869.doc"))) {
  sftpTemplate.executeWithoutResult(channelSftp -> channelSftp.get("aptx4869.doc", outputStream));
}
```

## Health Check

HaibaraCP implements the health indicator of Spring Boot Actuator. You can view the connection status of SFTP by exposing the `sftp` indicator.

```yml
management:
  endpoint:
    health:
      show-details: always
  health:
    sftp:
      enabled: true
```

## SftpSessionFactory

A factory used for creating SftpSession, which you will use when you need to customize the creation of a Jsch Session or extend the functionality of SftpSession, such as:

```java
@Configuration(proxyBeanMethods = false)
public class SftpConfiguration {

    @Bean
    public SftpSessionFactory sftpSessionFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        return new SftpSessionFactory(clientProperties, poolProperties) {
            @Override
            public SftpSession getSftpSession(ClientProperties clientProperties) {
                return new XxSftpSession(clientProperties);
            }
        };
    }
    
    public static class XxSftpSession extends SftpSession {
        
        private Channel fooChannel;
        
        public FooSftpSession(ClientProperties clientProperties) {
            super(clientProperties);
        }

        @Override
        protected Session createJschSession(ClientProperties clientProperties) throws Exception {
            Session jschSession = super.createJschSession(clientProperties);
            fooChannel = jschSession.openChannel("foo");
            return jschSession;
        }

        public Channel getFooChannel() {
            return xxChannel;
        }
    }

    @Bean
    public SftpTemplate<FooSftpSession> sftpTemplate(SftpSessionFactory sftpSessionFactory) {
        return new SftpTemplate<>(sftpSessionFactory);
    }
}
```

Then, you can use it in the SftpTemplate like this:

```java
sftpTemplate.executeSessionWithoutResult(sftpSession -> {
    Channel fooChannel = sftpSession.getFooChannel();
});
```

## Key format

The key format generated by openssh 7.8 is changed to a new format.

Haibaracp uses `com.github.mwiede:jsch` by default as the implementation of SFTP, which supports the new OpenSSH key format and more encryption algorithms. You no longer need to manually perform PEM format conversion or look for alternative libraries like when using the old version of JSch (com.jcraft:jsch).

If you still see [JSchException: invalid privatekey](https://github.com/mwiede/jsch/issues/12#issuecomment-662863338), please check if the key path is correct or if the key is corrupted.

## ChangeLog

 [CHANGELOG.md](CHANGELOG.md) 

## Thanks for free JetBrains Open Source license

<a href="https://www.jetbrains.com/?from=Mybatis-PageHelper" target="_blank">
<img src="https://user-images.githubusercontent.com/1787798/69898077-4f4e3d00-138f-11ea-81f9-96fb7c49da89.png" height="200"/></a>

