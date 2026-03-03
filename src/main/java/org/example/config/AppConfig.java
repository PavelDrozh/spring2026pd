package org.example.config;

import org.example.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PropertyConfig.class)
public class AppConfig {

    @ConditionalOnMissingBean(LocalizedIOservice.class)
    @Bean
    public LocalizedIOservice localizedIOservice(LocalizationService ls, IOService io) {
        return new LocalizedIOserviceImpl(ls, io);
    }

    @ConditionalOnMissingBean(UsersCommandGetter.class)
    @Bean
    public UsersCommandGetter usersCommandGetter(LocalizedIOservice io) {
        return new UsersCommandGetter(io);
    }

}

