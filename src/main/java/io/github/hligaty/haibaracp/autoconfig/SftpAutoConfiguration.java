package io.github.hligaty.haibaracp.autoconfig;

import com.jcraft.jsch.JSch;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import io.github.hligaty.haibaracp.core.HostHolder;
import io.github.hligaty.haibaracp.core.JschLogger;
import io.github.hligaty.haibaracp.core.SftpPool;
import io.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hligaty
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ClientProperties.class, PoolProperties.class})
public class SftpAutoConfiguration {

  @Bean
  public SftpPool sftpPool(ClientProperties clientProperties, PoolProperties poolProperties) {
    JSch.setLogger(new JschLogger(clientProperties.isEnabledLog()));
    return clientProperties.getHosts() == null ?
            new SftpPool(clientProperties, poolProperties) :
            new SftpPool(HostHolder.initHostKeys(clientProperties.getHosts()), poolProperties);
  }

  @Bean
  public SftpTemplate sftpTemplate(SftpPool sftpPool) {
    return new SftpTemplate(sftpPool);
  }
}
