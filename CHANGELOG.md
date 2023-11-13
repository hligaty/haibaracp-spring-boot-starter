# Change Log

## 2.1.0

Please note that 2.1.0 is a partially incompatible update with 2.0.0.

- feature:add `SftpSessionFactory` to create `SftpSession` subclasses.
- fix:the exceptions thrown by all methods of `SftpTemplate ` have been changed from `SftpException `(Checked Exception) to `SessionException `(Runtime Exception).
- fix:Remove multi-host support (`HostHolder`).

## 2.1.1

- feat:add Spring Boot Actuator.
