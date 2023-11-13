package io.github.hligaty.haibaracp.autoconfig;

import io.github.hligaty.haibaracp.actuator.SftpHealthIndicator;
import io.github.hligaty.haibaracp.core.SftpSessionFactory;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SftpHealthIndicator}.
 *
 * @author hligaty
 */
@AutoConfiguration
@ConditionalOnClass(HealthContributor.class)
@ConditionalOnEnabledHealthIndicator("sftp")
@AutoConfigureAfter(SftpAutoConfiguration.class)
public class SftpHealthContributorAutoConfiguration extends CompositeHealthContributorConfiguration<SftpHealthIndicator, SftpSessionFactory> {

    SftpHealthContributorAutoConfiguration() {
        super(SftpHealthIndicator::new);
    }

    @Bean
    public HealthContributor sftpHealthContributor(Map<String, SftpSessionFactory> sftpSessionFactories) {
        return createContributor(sftpSessionFactories);
    }

}
