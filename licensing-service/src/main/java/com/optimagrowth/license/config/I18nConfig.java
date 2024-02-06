package com.optimagrowth.license.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.*;

@Configuration
class I18nConfig {
    @Bean
    public LocaleResolver localeResolver()  {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US); // 기본 로케일 설정
        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true); // 메시지가 없으면 코드로 반환
        messageSource.setBasenames("messages"); // 언어 프로퍼터 피알의 기본 이름을 설정(message_언어.properties)
        return messageSource;
    }
}