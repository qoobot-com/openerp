package com.qoobot.qooerp.auth.service;

/**
 * 登录日志服务接口
 */
public interface LoginLogService {

    /**
     * 记录登录日志
     *
     * @param username    用户名
     * @param userId      用户ID
     * @param ip          登录IP
     * @param location    登录地点
     * @param browser     浏览器类型
     * @param os          操作系统
     * @param device      设备类型
     * @param status      登录状态：0-失败 1-成功
     * @param message     登录消息
     */
    void recordLoginLog(String username, Long userId, String ip, String location,
                       String browser, String os, String device, Integer status, String message);

    /**
     * 记录登录成功日志
     *
     * @param username 用户名
     * @param userId   用户ID
     * @param ip       登录IP
     * @param location 登录地点
     */
    void recordSuccessLogin(String username, Long userId, String ip, String location);

    /**
     * 记录登录失败日志
     *
     * @param username 用户名
     * @param ip       登录IP
     * @param message  失败原因
     */
    void recordFailedLogin(String username, String ip, String message);

    /**
     * 清理过期登录日志
     *
     * @param days 保留天数
     */
    void cleanExpiredLogs(int days);
}
