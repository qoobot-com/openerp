package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.entity.SystemMonitor;
import com.qoobot.qooerp.system.mapper.SystemMonitorMapper;
import com.qoobot.qooerp.system.service.SystemMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统监控服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemMonitorServiceImpl extends ServiceImpl<SystemMonitorMapper, SystemMonitor>
        implements SystemMonitorService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
    private final MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();

    private static final String INSTANCE_ID = "qooerp-system-" + System.currentTimeMillis();

    @Override
    public void recordMonitorData(String monitorType, String metricName, Double metricValue) {
        try {
            SystemMonitor monitor = new SystemMonitor();
            monitor.setMonitorType(monitorType);
            monitor.setMetricName(metricName);
            monitor.setMetricValue(metricValue);
            monitor.setInstanceId(INSTANCE_ID);
            monitor.setMonitorTime(LocalDateTime.now());
            monitor.setStatus("NORMAL");

            save(monitor);
        } catch (Exception e) {
            log.error("记录监控数据失败: monitorType={}, metricName={}", monitorType, metricName, e);
        }
    }

    @Override
    public List<SystemMonitor> getCpuMonitorData(String instanceId, LocalDateTime startTime,
                                                   LocalDateTime endTime) {
        return list(new LambdaQueryWrapper<SystemMonitor>()
                .eq(SystemMonitor::getMonitorType, "CPU")
                .eq(instanceId != null, SystemMonitor::getInstanceId, instanceId)
                .ge(startTime != null, SystemMonitor::getMonitorTime, startTime)
                .le(endTime != null, SystemMonitor::getMonitorTime, endTime)
                .orderByAsc(SystemMonitor::getMonitorTime)
                .last("LIMIT 1000"));
    }

    @Override
    public List<SystemMonitor> getMemoryMonitorData(String instanceId, LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        return list(new LambdaQueryWrapper<SystemMonitor>()
                .eq(SystemMonitor::getMonitorType, "MEMORY")
                .eq(instanceId != null, SystemMonitor::getInstanceId, instanceId)
                .ge(startTime != null, SystemMonitor::getMonitorTime, startTime)
                .le(endTime != null, SystemMonitor::getMonitorTime, endTime)
                .orderByAsc(SystemMonitor::getMonitorTime)
                .last("LIMIT 1000"));
    }

    @Override
    public List<SystemMonitor> getDiskMonitorData(String instanceId, LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        return list(new LambdaQueryWrapper<SystemMonitor>()
                .eq(SystemMonitor::getMonitorType, "DISK")
                .eq(instanceId != null, SystemMonitor::getInstanceId, instanceId)
                .ge(startTime != null, SystemMonitor::getMonitorTime, startTime)
                .le(endTime != null, SystemMonitor::getMonitorTime, endTime)
                .orderByAsc(SystemMonitor::getMonitorTime)
                .last("LIMIT 1000"));
    }

    @Override
    public List<SystemMonitor> getNetworkMonitorData(String instanceId, LocalDateTime startTime,
                                                        LocalDateTime endTime) {
        return list(new LambdaQueryWrapper<SystemMonitor>()
                .eq(SystemMonitor::getMonitorType, "NETWORK")
                .eq(instanceId != null, SystemMonitor::getInstanceId, instanceId)
                .ge(startTime != null, SystemMonitor::getMonitorTime, startTime)
                .le(endTime != null, SystemMonitor::getMonitorTime, endTime)
                .orderByAsc(SystemMonitor::getMonitorTime)
                .last("LIMIT 1000"));
    }

    @Override
    public List<SystemMonitor> getJvmMonitorData(String instanceId, LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        return list(new LambdaQueryWrapper<SystemMonitor>()
                .eq(SystemMonitor::getMonitorType, "JVM")
                .eq(instanceId != null, SystemMonitor::getInstanceId, instanceId)
                .ge(startTime != null, SystemMonitor::getMonitorTime, startTime)
                .le(endTime != null, SystemMonitor::getMonitorTime, endTime)
                .orderByAsc(SystemMonitor::getMonitorTime)
                .last("LIMIT 1000"));
    }

    @Override
    public List<SystemMonitor> getBusinessMonitorData(String serviceName, String metricName,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        return list(new LambdaQueryWrapper<SystemMonitor>()
                .eq(SystemMonitor::getMonitorType, "BUSINESS")
                .eq(SystemMonitor::getServiceName, serviceName)
                .eq(SystemMonitor::getMetricName, metricName)
                .ge(startTime != null, SystemMonitor::getMonitorTime, startTime)
                .le(endTime != null, SystemMonitor::getMonitorTime, endTime)
                .orderByAsc(SystemMonitor::getMonitorTime)
                .last("LIMIT 1000"));
    }

    @Override
    public IPage<SystemMonitor> pageMonitorData(Long current, Long size, String monitorType,
                                                    String instanceId, LocalDateTime startTime, LocalDateTime endTime) {
        Page<SystemMonitor> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemMonitor> wrapper = new LambdaQueryWrapper<>();

        if (monitorType != null) {
            wrapper.eq(SystemMonitor::getMonitorType, monitorType);
        }
        if (instanceId != null) {
            wrapper.eq(SystemMonitor::getInstanceId, instanceId);
        }
        if (startTime != null) {
            wrapper.ge(SystemMonitor::getMonitorTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SystemMonitor::getMonitorTime, endTime);
        }

        wrapper.orderByDesc(SystemMonitor::getMonitorTime);

        return page(page, wrapper);
    }

    @Override
    public Map<String, Object> getCurrentPerformanceOverview() {
        Map<String, Object> overview = new HashMap<>();

        // CPU使用率
        double cpuUsage = osMxBean.getSystemLoadAverage() * 100 / osMxBean.getAvailableProcessors();
        overview.put("cpuUsage", String.format("%.2f", cpuUsage));
        overview.put("cpuStatus", cpuUsage > 80 ? "WARNING" : "NORMAL");

        // 内存使用
        long totalMemory = memoryMxBean.getHeapMemoryUsage().getMax();
        long usedMemory = memoryMxBean.getHeapMemoryUsage().getUsed();
        double memoryUsage = (double) usedMemory / totalMemory * 100;
        overview.put("memoryUsage", String.format("%.2f", memoryUsage));
        overview.put("memoryStatus", memoryUsage > 80 ? "WARNING" : "NORMAL");
        overview.put("totalMemory", totalMemory / 1024 / 1024 + " MB");
        overview.put("usedMemory", usedMemory / 1024 / 1024 + " MB");

        // JVM信息
        Runtime runtime = Runtime.getRuntime();
        overview.put("jvmVersion", System.getProperty("java.version"));
        overview.put("jvmFreeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        overview.put("jvmTotalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        overview.put("jvmMaxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");

        // 实例信息
        overview.put("instanceId", INSTANCE_ID);
        overview.put("availableProcessors", osMxBean.getAvailableProcessors());
        overview.put("systemLoadAverage", osMxBean.getSystemLoadAverage());

        return overview;
    }

    @Override
    public void cleanExpiredMonitorData(Integer days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<SystemMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SystemMonitor::getMonitorTime, expireTime);

        int count = count(wrapper);
        if (count > 0) {
            remove(wrapper);
            log.info("清理过期监控数据: {} 条", count);
        }
    }

    /**
     * 定时收集系统性能数据
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void collectSystemMetrics() {
        try {
            // CPU监控
            double cpuUsage = osMxBean.getSystemLoadAverage() * 100 / osMxBean.getAvailableProcessors();
            recordMonitorData("CPU", "usage", cpuUsage);

            // 内存监控
            long totalMemory = memoryMxBean.getHeapMemoryUsage().getMax();
            long usedMemory = memoryMxBean.getHeapMemoryUsage().getUsed();
            double memoryUsage = (double) usedMemory / totalMemory * 100;
            recordMonitorData("MEMORY", "usage", memoryUsage);
            recordMonitorData("MEMORY", "used", (double) usedMemory / 1024 / 1024);
            recordMonitorData("MEMORY", "total", (double) totalMemory / 1024 / 1024);

            // JVM监控
            Runtime runtime = Runtime.getRuntime();
            recordMonitorData("JVM", "free", (double) runtime.freeMemory() / 1024 / 1024);
            recordMonitorData("JVM", "total", (double) runtime.totalMemory() / 1024 / 1024);
            recordMonitorData("JVM", "max", (double) runtime.maxMemory() / 1024 / 1024);

        } catch (Exception e) {
            log.error("收集系统性能数据失败", e);
        }
    }
}
