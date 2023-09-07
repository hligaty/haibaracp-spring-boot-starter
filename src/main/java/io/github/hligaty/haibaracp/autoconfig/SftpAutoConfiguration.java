package io.github.hligaty.haibaracp.autoconfig;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import io.github.hligaty.haibaracp.core.SftpClientFactory;
import io.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author hligaty
 */
@AutoConfiguration
@EnableConfigurationProperties({ClientProperties.class, PoolProperties.class})
public class SftpAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(SftpClientFactory.class)
    public SftpClientFactory sftpClientFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        return new SftpClientFactory(clientProperties, poolProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SftpTemplate.class)
    public SftpTemplate sftpTemplate(SftpClientFactory sftpClientFactory) {
        return new SftpTemplate(sftpClientFactory);
    }
}
