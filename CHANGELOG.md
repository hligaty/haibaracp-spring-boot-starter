# Change Log

## 1.3.0

Please note that 1.3.0 is a partially incompatible update with 1.2.3.

- feature:add `SftpSessionFactory` to create `SftpSession` subclasses.
- fix:the exceptions thrown by all methods of `SftpTemplate ` have been changed from `SftpException `(Checked Exception) to `SessionException `(Runtime Exception).
- fix:Remove multi-host support (`HostHolder`).

## 1.3.1

- feat:add Spring Boot Actuator.

## 1.3.2

Mainly including: Modified the default SSH connection timeout and other configurations.

- fix:channelSftp connect timeout.
- refactor:Removed unused pool properties.
- feat:Added non pooling usage.
- feat:Added socket timeout.
