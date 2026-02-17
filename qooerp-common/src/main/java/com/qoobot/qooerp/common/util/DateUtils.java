package com.qoobot.qooerp.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期时间工具类
 */
public class DateUtils {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化Date类型日期
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return DateUtil.format(date, pattern);
    }

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (StrUtil.isEmpty(dateTimeStr)) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析日期字符串为Date
     */
    public static Date parse(String dateStr, String pattern) {
        if (StrUtil.isEmpty(dateStr)) {
            return null;
        }
        return DateUtil.parse(dateStr, pattern);
    }

    /**
     * 获取当前日期时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期时间字符串
     */
    public static String nowStr() {
        return formatDateTime(now());
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return DateUtil.toLocalDateTime(date);
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return java.sql.Timestamp.valueOf(localDateTime);
    }

    /**
     * 日期加天数
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return DateUtil.offsetDay(date, days);
    }

    /**
     * 日期加月数
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) {
            return null;
        }
        return DateUtil.offsetMonth(date, months);
    }

    /**
     * 日期加年数
     */
    public static Date addYears(Date date, int years) {
        if (date == null) {
            return null;
        }
        return DateUtil.offset(date, DateField.YEAR, years);
    }

    /**
     * 判断两个日期是否为同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return DateUtil.isSameDay(date1, date2);
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long betweenDays(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        return DateUtil.betweenDay(start, end, false);
    }
}
