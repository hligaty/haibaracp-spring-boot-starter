package io.github.hligaty.haibaracp.autoconfig;

import com.jcraft.jsch.JSch;
import io.github.hligaty.haibaracp.config.ClientProperties;
import io.github.hligaty.haibaracp.config.PoolProperties;
import io.github.hligaty.haibaracp.core.JschLogger;
import io.github.hligaty.haibaracp.core.SftpPool;
import io.github.hligaty.haibaracp.core.SftpTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author hligaty
 */
@AutoConfiguration
@EnableConfigurationProperties({ClientProperties.class, PoolProperties.class})
public class SftpAutoConfiguration {
    
    @Bean
    public SftpPool sftpPool(ClientProperties clientProperties, PoolProperties poolProperties) {
        JSch.setLogger(new JschLogger(clientProperties.isEnabledLog()));
        return new SftpPool(clientProperties, poolProperties);
    }
    
    @Bean
    public SftpTemplate sftpTemplate(SftpPool sftpPool) {
        return new SftpTemplate(sftpPool);
    }
}
