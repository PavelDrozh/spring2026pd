package org.example.config;


import org.example.util.IOService;
import org.example.util.LocalizationService;
import org.example.util.LocalizationServiceImpl;
import org.example.util.StreamsIOService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(MessageSource.class)
@Configuration
public class IOAutoconfiguration {

    @ConditionalOnMissingBean(IOService.class)
    @Bean
    public IOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    @ConditionalOnMissingBean(LocaleProvider.class)
    @Bean
    public LocaleProvider localeProvider(@Value("${spring.application.locale:en-US}") String localeTag) {
        return new DefaultLocaleProvider(localeTag);
    }

    @ConditionalOnMissingBean(LocalizationService.class)
    @Bean
    public LocalizationService localizationService(LocaleProvider localeProvider,
                                                   MessageSource messageSource) {
        return new LocalizationServiceImpl(localeProvider, messageSource);
    }

}
