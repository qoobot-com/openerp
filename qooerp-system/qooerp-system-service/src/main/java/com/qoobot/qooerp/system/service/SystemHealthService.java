package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.entity.SystemHealthCheck;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 健康检查服务接口
 */
public interface SystemHealthService {

    /**
     * 执行服务健康检查
     *
     * @return 检查结果
     */
    Map<String, Object> performHealthCheck();

    /**
     * 执行数据库健康检查
     *
     * @return 检查结果
     */
    Map<String, Object> checkDatabase();

    /**
     * 执行Redis健康检查
     *
     * @return 检查结果
     */
    Map<String, Object> checkRedis();

    /**
     * 执行消息队列健康检查
     *
     * @return 检查结果
     */
    Map<String, Object> checkMessageQueue();

    /**
     * 检测故障
     *
     * @return 故障列表
     */
    List<Map<String, Object>> detectFailures();

    /**
     * 获取健康状态
     *
     * @return 健康状态
     */
    String getHealthStatus();

    /**
     * 分页查询健康检查记录
     *
     * @param current 当前页
     * @param size 每页大小
     * @param checkType 检查类型
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SystemHealthCheck> pageHealthChecks(Long current, Long size, String checkType,
                                              String status, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取最近健康检查记录
     *
     * @param limit 数量限制
     * @return 健康检查记录列表
     */
    List<SystemHealthCheck> getRecentHealthChecks(Integer limit);

    /**
     * 清理过期健康检查记录
     *
     * @param days 保留天数
     */
    void cleanExpiredHealthChecks(Integer days);
}
