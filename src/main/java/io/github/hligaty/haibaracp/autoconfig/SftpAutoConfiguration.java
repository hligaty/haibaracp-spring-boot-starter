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
