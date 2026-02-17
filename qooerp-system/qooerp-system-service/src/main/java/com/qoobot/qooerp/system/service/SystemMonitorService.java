package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.entity.SystemMonitor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统监控服务接口
 */
public interface SystemMonitorService {

    /**
     * 记录性能监控数据
     *
     * @param monitorType 监控类型
     * @param metricName 监控指标名称
     * @param metricValue 监控指标值
     */
    void recordMonitorData(String monitorType, String metricName, Double metricValue);

    /**
     * 获取CPU监控数据
     *
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    List<SystemMonitor> getCpuMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取内存监控数据
     *
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    List<SystemMonitor> getMemoryMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取磁盘监控数据
     *
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    List<SystemMonitor> getDiskMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取网络监控数据
     *
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    List<SystemMonitor> getNetworkMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取JVM监控数据
     *
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    List<SystemMonitor> getJvmMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取业务监控数据
     *
     * @param serviceName 服务名称
     * @param metricName 监控指标名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    List<SystemMonitor> getBusinessMonitorData(String serviceName, String metricName,
                                               LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询监控数据
     *
     * @param current 当前页
     * @param size 每页大小
     * @param monitorType 监控类型
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SystemMonitor> pageMonitorData(Long current, Long size, String monitorType,
                                        String instanceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取当前系统性能概览
     *
     * @return 性能概览数据
     */
    Map<String, Object> getCurrentPerformanceOverview();

    /**
     * 清理过期监控数据
     *
     * @param days 保留天数
     */
    void cleanExpiredMonitorData(Integer days);
}
