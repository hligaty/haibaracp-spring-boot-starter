# Change Log

## 3.0.0

Please note that 3.0.0 is a major version update with breaking changes.

- chore: Upgrade to Spring Boot 4.0.1
- chore: Replace jsch with mwiede/jsch fork and upgrade to 2.27.7
- refactor: Update timeout properties in ClientProperties and PoolProperties to use Duration type
- refactor: Remove automatic directory creation in upload method
- refactor: Update annotations to use jspecify for nullability
- refactor: Replace deprecated setMinEvictableIdleTime with setMinEvictableIdleDuration
- refactor: Refactor health indicator imports for Spring Boot 4.x

## 2.1.2

Please note that 2.1.0 is a partially incompatible update with 2.0.0.

- feature:add `SftpSessionFactory` to create `SftpSession` subclasses.
- fix:the exceptions thrown by all methods of `SftpTemplate ` have been changed from `SftpException `(Checked Exception) to `SessionException `(Runtime Exception).
- fix:Remove multi-host support (`HostHolder`).

## 2.1.1

- feat:add Spring Boot Actuator.

## 2.1.2

Mainly including: Modified the default SSH connection timeout and other configurations.

- fix:channelSftp connect timeout.
- refactor:Removed unused pool properties.
- feat:Added non pooling usage.
- feat:Added socket timeout.
