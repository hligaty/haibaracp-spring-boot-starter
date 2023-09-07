package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import io.github.hligaty.haibaracp.autoconfig.SftpAutoConfiguration;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import org.springframework.util.StringUtils;

public class JschSessionFactory {
    
    /**
     * Create a Jsch Session.
     *
     * @param clientProperties properties for sftp.
     * @return {@link com.jcraft.jsch.Session}
     * @throws Exception an exception during create session.
     * @see SftpClientFactory
     * @see SftpAutoConfiguration#sftpClientFactory(ClientProperties, PoolProperties) 
     */
    public Session get(ClientProperties clientProperties) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(clientProperties.getUsername(), clientProperties.getHost(), clientProperties.getPort());
        if (clientProperties.isStrictHostKeyChecking()) {
            session.setConfig("StrictHostKeyChecking", "ask");
            session.setUserInfo(new UserInfoImpl(clientProperties.getPassword()));
            jsch.addIdentity(clientProperties.getKeyPath());
        } else {
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(clientProperties.getPassword());
        }
        if (StringUtils.hasText(clientProperties.getKex())) {
            session.setConfig("kex", clientProperties.getKex());
        }
        session.connect(clientProperties.getConnectTimeout());
        return session;
    }

    private record UserInfoImpl(String passphrase) implements UserInfo {

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
}
