package com.qoobot.qooerp.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 登录上下文工具类
 */
@Slf4j
public class LoginContextUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String LOCALHOST_IP_V6 = "0:0:0:0:0:0:0:1";
    private static final int IP_LENGTH = 15;

    /**
     * 获取当前请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取客户端IP地址
     *
     * @return IP地址
     */
    public static String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return LOCALHOST;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOCALHOST_IP_V6.equals(ip)) {
                ip = LOCALHOST;
            }
        }

        // 对于通过多个代理的情况，第一个IP才是客户端真实IP
        if (ip != null && ip.length() > IP_LENGTH) {
            int index = ip.indexOf(",");
            if (index != -1) {
                ip = ip.substring(0, index);
            }
        }

        return ip;
    }

    /**
     * 获取User-Agent
     *
     * @return User-Agent
     */
    public static String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return "Unknown";
        }
        return request.getHeader("User-Agent");
    }

    /**
     * 解析浏览器类型
     *
     * @param userAgent User-Agent
     * @return 浏览器类型
     */
    public static String parseBrowser(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        } else {
            return "Unknown";
        }
    }

    /**
     * 解析操作系统
     *
     * @param userAgent User-Agent
     * @return 操作系统
     */
    public static String parseOs(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "Mac";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }

    /**
     * 解析设备类型
     *
     * @param userAgent User-Agent
     * @return 设备类型
     */
    public static String parseDevice(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Mobile") || userAgent.contains("Android") ||
                userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "Mobile";
        } else if (userAgent.contains("Tablet")) {
            return "Tablet";
        } else {
            return "PC";
        }
    }

    /**
     * 获取本机主机名
     *
     * @return 主机名
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("获取主机名失败", e);
            return "Unknown";
        }
    }
}
