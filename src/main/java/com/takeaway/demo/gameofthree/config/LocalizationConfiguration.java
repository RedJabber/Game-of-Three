package com.takeaway.demo.gameofthree.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * LocalizationConfiguration.
 *
 * @author Andrey Arefyev
 */
@Configuration
public class LocalizationConfiguration {
    @Bean
    public MessageSource messageSource() {
        var messageBundle = new ReloadableResourceBundleMessageSource();
        messageBundle.setDefaultEncoding("UTF-8");
        messageBundle.setBasenames("classpath:/messages");
        messageBundle.setFallbackToSystemLocale(false);
        return messageBundle;
    }
}