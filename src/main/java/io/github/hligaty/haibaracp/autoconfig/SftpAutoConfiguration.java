package io.github.hligaty.haibaracp.autoconfig;

import com.jcraft.jsch.JSch;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import io.github.hligaty.haibaracp.core.JschLogger;
import io.github.hligaty.haibaracp.core.SftpClient;
import io.github.hligaty.haibaracp.core.SftpClientFactory;
import io.github.hligaty.haibaracp.core.SftpClientPool;
import io.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.beans.factory.ObjectProvider;
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
    @ConditionalOnMissingBean(SftpClientPool.class)
    public SftpClientPool sftpPool(ClientProperties clientProperties,
                                   PoolProperties poolProperties,
                                   ObjectProvider<SftpClientFactory> sftpClientFactoryObjectProvider) {
        JSch.setLogger(new JschLogger(clientProperties.isEnabledLog()));
        return new SftpClientPool(sftpClientFactoryObjectProvider.getIfAvailable(() -> () -> new SftpClient(clientProperties)),
                poolProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SftpTemplate.class)
    public SftpTemplate sftpTemplate(SftpClientPool sftpClientPool) {
        return new SftpTemplate(sftpClientPool);
    }
}
