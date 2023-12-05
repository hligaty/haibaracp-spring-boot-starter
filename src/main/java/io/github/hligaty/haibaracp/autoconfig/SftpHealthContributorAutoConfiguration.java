/*
 * Copyright 2021-2023 hligaty
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hligaty.haibaracp.autoconfig;

import io.github.hligaty.haibaracp.actuator.SftpHealthIndicator;
import io.github.hligaty.haibaracp.core.SftpSessionFactory;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SftpHealthIndicator}.
 *
 * @author hligaty
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HealthContributor.class)
@ConditionalOnEnabledHealthIndicator("sftp")
@AutoConfigureAfter(SftpAutoConfiguration.class)
public class SftpHealthContributorAutoConfiguration extends CompositeHealthContributorConfiguration<SftpHealthIndicator, SftpSessionFactory> {

    @Bean
    public HealthContributor sftpHealthContributor(Map<String, SftpSessionFactory> sftpSessionFactories) {
        return createContributor(sftpSessionFactories);
    }

}
