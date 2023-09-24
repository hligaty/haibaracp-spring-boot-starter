# Change Log

## 1.3.0

Please note that 1.3.0 is a partially incompatible update with 1.2.3.

- feature:add `SftpSessionFactory` to create `SftpSession` subclasses.
- fix:the exceptions thrown by all methods of `SftpTemplate ` have been changed from `SftpException `(Checked Exception) to `SessionException `(Runtime Exception).
- fix:Remove multi-host support (`HostHolder`).
