package com.example.amsys.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

/**
 * 国際化（i18n）設定クラス
 * すべてのメッセージを日本語で返すための設定
 */
@Configuration
public class LocaleConfig {

    /**
     * ロケールリゾルバーの設定
     * 日本語ロケール（ja_JP）を固定で使用
     */
    @Bean
    LocaleResolver localeResolver() {
        FixedLocaleResolver resolver = new FixedLocaleResolver();
        resolver.setDefaultLocale(Locale.JAPAN);
        return resolver;
    }

    /**
     * メッセージソースの設定
     * messages.propertiesからメッセージを読み込み
     */
    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.JAPAN);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
