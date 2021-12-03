package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.UserInfo;

/**
 * SFTP 秘钥登录
 *
 * @author hligaty
 */
public class UserInfoImpl implements UserInfo {
  private final String passphrase;

  public UserInfoImpl(String passphrase) {
    this.passphrase = passphrase;
  }

  @Override
  public String getPassphrase() {
    return passphrase;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public boolean promptPassword(String s) {
    return false;
  }

  @Override
  public boolean promptPassphrase(String s) {
    return true;
  }

  @Override
  public boolean promptYesNo(String s) {
    return true;
  }

  @Override
  public void showMessage(String s) {
  }
}
