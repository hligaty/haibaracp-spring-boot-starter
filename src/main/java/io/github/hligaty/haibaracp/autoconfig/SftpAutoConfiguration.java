package io.github.hligaty.haibaracp.autoconfig;

import io.github.hligaty.haibaracp.config.SftpProperties;
import io.github.hligaty.haibaracp.core.SftpPool;
import io.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hligaty
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SftpProperties.class)
public class SftpAutoConfiguration {

  @Bean
  public SftpPool sftpPool(SftpProperties sftpProperties) {
    return new SftpPool(sftpProperties);
  }

  @Bean
  public SftpTemplate sftpTemplate(SftpPool sftpPool) {
    return new SftpTemplate(sftpPool);
  }
}
