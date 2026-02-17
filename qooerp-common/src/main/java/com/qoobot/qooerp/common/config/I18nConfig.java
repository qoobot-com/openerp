package com.qoobot.qooerp.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 国际化配置类
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    /**
     * 默认语言
     */
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    /**
     * Session语言解析器
     */
    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(DEFAULT_LOCALE);
        return resolver;
    }

    /**
     * 语言切换拦截器
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * 消息源
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        // 设置国际化资源文件的基础名称
        source.setBasenames("i18n/messages");
        // 设置编码
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 设置是否缓存
        source.setCacheSeconds(-1);
        // 设置默认语言
        source.setDefaultLocale(DEFAULT_LOCALE);
        return source;
    }
}
