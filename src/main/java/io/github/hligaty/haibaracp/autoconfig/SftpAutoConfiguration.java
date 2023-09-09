package io.github.hligaty.haibaracp.autoconfig;

import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import io.github.hligaty.haibaracp.core.SftpSession;
import io.github.hligaty.haibaracp.core.SftpSessionFactory;
import io.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Sftp support.
 * 
 * @author hligaty
 */
@AutoConfiguration
@EnableConfigurationProperties({ClientProperties.class, PoolProperties.class})
public class SftpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SftpSessionFactory.class)
    public SftpSessionFactory sftpSessionFactory(ClientProperties clientProperties, PoolProperties poolProperties) {
        return new SftpSessionFactory(clientProperties, poolProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SftpTemplate.class)
    public SftpTemplate<SftpSession> sftpTemplate(SftpSessionFactory sftpSessionFactory) {
        return new SftpTemplate<>(sftpSessionFactory);
    }
}
