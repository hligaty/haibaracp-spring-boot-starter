package com.github.hligaty.haibaracp.autoconfig;

import com.github.hligaty.haibaracp.config.SftpProperties;
import com.github.hligaty.haibaracp.core.SftpPool;
import com.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hligaty & haibara
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
