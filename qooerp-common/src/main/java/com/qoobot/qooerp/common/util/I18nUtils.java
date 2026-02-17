package com.qoobot.qooerp.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class I18nUtils {

    private final MessageSource messageSource;

    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @return 消息内容
     */
    public String getMessage(String code) {
        return getMessage(code, null, null);
    }

    /**
     * 获取国际化消息（带参数）
     *
     * @param code 消息代码
     * @param args 参数
     * @return 消息内容
     */
    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, null);
    }

    /**
     * 获取国际化消息（带默认值）
     *
     * @param code         消息代码
     * @param defaultMessage 默认消息
     * @return 消息内容
     */
    public String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    /**
     * 获取国际化消息（完整版）
     *
     * @param code         消息代码
     * @param args         参数
     * @param defaultMessage 默认消息
     * @return 消息内容
     */
    public String getMessage(String code, Object[] args, String defaultMessage) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(code, args, defaultMessage, locale);
        } catch (Exception e) {
            log.error("获取国际化消息失败: code={}", code, e);
            return defaultMessage != null ? defaultMessage : code;
        }
    }

    /**
     * 获取国际化消息（指定语言）
     *
     * @param code   消息代码
     * @param locale 语言
     * @return 消息内容
     */
    public String getMessage(String code, Locale locale) {
        return getMessage(code, null, null, locale);
    }

    /**
     * 获取国际化消息（指定语言，带参数）
     *
     * @param code   消息代码
     * @param args   参数
     * @param locale 语言
     * @return 消息内容
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        return getMessage(code, args, null, locale);
    }

    /**
     * 获取国际化消息（完整版，指定语言）
     *
     * @param code         消息代码
     * @param args         参数
     * @param defaultMessage 默认消息
     * @param locale       语言
     * @return 消息内容
     */
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        try {
            return messageSource.getMessage(code, args, defaultMessage, locale);
        } catch (Exception e) {
            log.error("获取国际化消息失败: code={}, locale={}", code, locale, e);
            return defaultMessage != null ? defaultMessage : code;
        }
    }

    /**
     * 设置当前语言
     *
     * @param locale 语言
     */
    public void setLocale(Locale locale) {
        LocaleContextHolder.setLocale(locale);
    }

    /**
     * 获取当前语言
     *
     * @return 当前语言
     */
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * 重置语言
     */
    public void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }
}
