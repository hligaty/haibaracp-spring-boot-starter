# HaibaraCP

<p align="center">
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/blob/master/LICENSE"><img src="https://img.shields.io/github/license/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://api.github.com/repos/hligaty/haibaracp-spring-boot-starter/releases/latest"><img src="https://img.shields.io/github/v/release/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/stargazers"><img src="https://img.shields.io/github/stars/hligaty/haibaracp-spring-boot-starter"></a>
<a href="https://github.com/hligaty/haibaracp-spring-boot-starter/network/members"><img src="https://img.shields.io/github/forks/hligaty/haibaracp-spring-boot-starter"></a>
</p>

## 介绍
HaibaraCP 是一个 SFTP 连接池，基于 commons-pool2 和 jsch 实现。

## Maven repository

```xml
<dependency>
    <groupId>io.github.hligaty</groupId>
    <artifactId>haibaracp-spring-boot-starter</artifactId>
    <version>1.0.3</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.8.1</version>
</dependency>
```

和 `spring-boot-starter-data-redis` 一样，需要手动引入 `commons-pool2` 依赖。

## 配置

```yml
sftp:
  host: 127.0.0.1
  port: 22
  username: root
  password: '123456'
  session:
    StrictHostKeyChecking: no
    kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
  pool:
    max-idle: 8
    min-idle: 1
    max-active: 16
    max-wait: 150000
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 120000
```

## 使用

HaibaraCP 提供 SftpTemplate 类，它与 `spring-boot-starter-data-redis`  提供的 RedisTemplate 使用方法相同，任意方式注入即可使用：

```java
@Component
public class XXXService {
  @Autowired
  private SftpTemplate sftpTemplate;

  void service() {
    //sftpTemplate........
  }
}
```

## API

### 上传文件

```java
// root 账户 SFTP 登录后目录为 /root
try (InputStream inputStream1 = Files.newInputStream(Paths.get("D:\\1.txt"));
     InputStream inputStream2 = Files.newInputStream(Paths.get("D:\\2.txt"));
     InputStream inputStream3 = Files.newInputStream(Paths.get("D:\\3.txt"))) {
  // 上传 D:\\1.txt 到 /home/haibara/1.txt
  sftpTemplate.upload(inputStream1, "/home/haibara/1.txt");
  
  // 上传 D:\\2.txt 到 /root/haibara/2.txt
  sftpTemplate.upload(inputStream2, "haibara/2.txt");
  
  // 上传 D:\\3.txt 到 /root/3.txt
  sftpTemplate.upload(inputStream3, "3.txt");
}
```

`upload(InputStream from, String to)` 方法首先根据 to 逐级检查目录（如果是目录格式），目录不存在就会创建，直到进入上传的目的地目录，最后上传 from 文件。

方法不会主动关闭流，请手动关闭。

### 下载文件

```java
// root 账户 SFTP 登录后目录为 /root
try (OutputStream outPutStream1 = Files.newOutputStream(Paths.get("D:\\1.txt"));
         OutputStream outPutStream2 = Files.newOutputStream(Paths.get("D:\\2.txt"));
         OutputStream outPutStream3 = Files.newOutputStream(Paths.get("D:\\3.txt"))) {
  // 下载 /home/chongci/1.txt 到 D:\\1.txt
  sftpTemplate.download("/home/chongci/1.txt", outPutStream1);
  
  // 下载 /root/haibara/2.txt 到 D:\\1.txt
  sftpTemplate.download("haibara/2.txt", outPutStream2);
  
  // 下载 /root/3.txt 到 D:\\1.txt
  sftpTemplate.download("3.txt", outPutStream3);
}
```

`download(String from, OutputStream to)` 方法一样会根据 to 逐级检查并进入目录（如果是目录格式），但当某级目录不存在或进入目录后发现文件不存在就会抛出 to 文件 `FileNotFoundException`。

方法不会主动关闭流，请手动关闭。

### 自定义

`execute(SftpCallback<T> action)` 提供自定义 SFTP 操作，比如查看 SFTP 默认目录（关于 ChannelSftp 的其他用法请参考 jsch 的 API）：

```java
String dir = sftpTemplate.execute(ChannelSftp::pwd);
```

### 注意

SftpTemplate 在执行结束后会执行回滚操作，回滚成功就会还原被使用连接的远端目录（但不会还原本地目录），以保证下次使用该连接时是初始连接时的目录。

## 计划

- 支持多个不同 Host 连接。
- 增加 `SftpTemplate` 功能。
- 完善 `ChannelSftp` 回滚机制。

## 其他

如果有问题或需要 SFTP 连接的其他回滚操作欢迎提 <a href="https://github.com/hligaty/spring-study/issues"><img src="https://img.shields.io/github/issues/hligaty/haibaracp-spring-boot-starter"></a>。
