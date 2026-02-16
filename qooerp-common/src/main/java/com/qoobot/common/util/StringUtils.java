package com.qoobot.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return StrUtil.isEmpty(str);
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StrUtil.isNotEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return StrUtil.isNotBlank(str);
    }

    /**
     * 字符串判空，返回默认值
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return StrUtil.emptyIfNull(str).isEmpty() ? defaultValue : str;
    }

    /**
     * 字符串判空，返回默认值
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    /**
     * 字符串截取
     */
    public static String substring(String str, int startIndex) {
        return StrUtil.sub(str, startIndex, str.length());
    }

    /**
     * 字符串截取
     */
    public static String substring(String str, int startIndex, int endIndex) {
        return StrUtil.sub(str, startIndex, endIndex);
    }

    /**
     * 去除字符串两端的空格
     */
    public static String trim(String str) {
        return StrUtil.trim(str);
    }

    /**
     * 字符串驼峰转下划线
     */
    public static String toUnderlineCase(String str) {
        return StrUtil.toUnderlineCase(str);
    }

    /**
     * 字符串下划线转驼峰
     */
    public static String toCamelCase(String str) {
        return StrUtil.toCamelCase(str);
    }

    /**
     * 字符串转蛇形命名（下划线命名）
     */
    public static String toSnakeCase(String str) {
        return StrUtil.toUnderlineCase(str);
    }

    /**
     * 字符串转短横线命名
     */
    public static String toKebabCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    /**
     * 缩略字符串
     */
    public static String abbreviate(String str, int maxWidth) {
        if (isEmpty(str) || str.length() <= maxWidth) {
            return str;
        }
        return str.substring(0, maxWidth - 3) + "...";
    }

    /**
     * 获取分隔符之前的字符串
     */
    public static String substringBefore(String str, String separator) {
        return StrUtil.subBefore(str, separator, false);
    }

    /**
     * 获取分隔符之后的字符串
     */
    public static String substringAfter(String str, String separator) {
        return StrUtil.subAfter(str, separator, false);
    }

    /**
     * 判断字符串是否包含子串
     */
    public static boolean contains(String str, CharSequence searchStr) {
        return StrUtil.contains(str, searchStr);
    }

    /**
     * 字符串连接
     */
    public static String join(Object[] array, String separator) {
        return StrUtil.join(separator, array);
    }

    /**
     * 字符串分割
     */
    public static String[] split(String str, String separator) {
        return StrUtil.splitToArray(str, separator);
    }

    /**
     * 字符串转大写
     */
    public static String upperCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.toUpperCase();
    }

    /**
     * 字符串转小写
     */
    public static String lowerCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.toLowerCase();
    }

    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        return StrUtil.isNumeric(str);
    }
}
