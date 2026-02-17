package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.entity.SystemHealthCheck;
import com.qoobot.qooerp.system.mapper.SystemHealthCheckMapper;
import com.qoobot.qooerp.system.service.SystemHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康检查服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemHealthServiceImpl extends ServiceImpl<SystemHealthCheckMapper, SystemHealthCheck>
        implements SystemHealthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DataSource dataSource;

    @Override
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", getHealthStatus());
        result.put("timestamp", LocalDateTime.now());

        Map<String, Object> components = new HashMap<>();
        components.put("database", checkDatabase());
        components.put("redis", checkRedis());
        components.put("messageQueue", checkMessageQueue());

        result.put("components", components);
        return result;
    }

    @Override
    public Map<String, Object> checkDatabase() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        String status = "UP";
        String errorMessage = null;

        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(5);
            if (!valid) {
                status = "DOWN";
                errorMessage = "Database connection is not valid";
            }
        } catch (Exception e) {
            status = "DOWN";
            errorMessage = e.getMessage();
            log.error("数据库健康检查失败", e);
        }

        result.put("status", status);
        result.put("responseTime", System.currentTimeMillis() - startTime);
        result.put("errorMessage", errorMessage);

        // 记录检查结果
        recordHealthCheck("DATABASE", "database", status,
                System.currentTimeMillis() - startTime, errorMessage);

        return result;
    }

    @Override
    public Map<String, Object> checkRedis() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        String status = "UP";
        String errorMessage = null;

        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
        } catch (Exception e) {
            status = "DOWN";
            errorMessage = e.getMessage();
            log.error("Redis健康检查失败", e);
        }

        result.put("status", status);
        result.put("responseTime", System.currentTimeMillis() - startTime);
        result.put("errorMessage", errorMessage);

        // 记录检查结果
        recordHealthCheck("REDIS", "redis", status,
                System.currentTimeMillis() - startTime, errorMessage);

        return result;
    }

    @Override
    public Map<String, Object> checkMessageQueue() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        String status = "UP"; // 暂时返回UP，实际应检查MQ连接
        String errorMessage = null;

        result.put("status", status);
        result.put("responseTime", System.currentTimeMillis() - startTime);
        result.put("errorMessage", errorMessage);

        // 记录检查结果
        recordHealthCheck("MQ", "messageQueue", status,
                System.currentTimeMillis() - startTime, errorMessage);

        return result;
    }

    @Override
    public List<Map<String, Object>> detectFailures() {
        List<Map<String, Object>> failures = new ArrayList<>();

        // 检查数据库
        Map<String, Object> dbCheck = checkDatabase();
        if ("DOWN".equals(dbCheck.get("status"))) {
            Map<String, Object> failure = new HashMap<>();
            failure.put("component", "database");
            failure.put("type", "CRITICAL");
            failure.put("message", "Database connection failed");
            failure.put("detectedAt", LocalDateTime.now());
            failures.add(failure);
        }

        // 检查Redis
        Map<String, Object> redisCheck = checkRedis();
        if ("DOWN".equals(redisCheck.get("status"))) {
            Map<String, Object> failure = new HashMap<>();
            failure.put("component", "redis");
            failure.put("type", "WARNING");
            failure.put("message", "Redis connection failed");
            failure.put("detectedAt", LocalDateTime.now());
            failures.add(failure);
        }

        return failures;
    }

    @Override
    public String getHealthStatus() {
        Map<String, Object> dbCheck = checkDatabase();
        Map<String, Object> redisCheck = checkRedis();

        if ("DOWN".equals(dbCheck.get("status"))) {
            return "DOWN";
        }
        if ("DOWN".equals(redisCheck.get("status"))) {
            return "WARNING";
        }
        return "UP";
    }

    @Override
    public IPage<SystemHealthCheck> pageHealthChecks(Long current, Long size, String checkType,
                                                      String status, LocalDateTime startTime, LocalDateTime endTime) {
        Page<SystemHealthCheck> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemHealthCheck> wrapper = new LambdaQueryWrapper<>();

        if (checkType != null) {
            wrapper.eq(SystemHealthCheck::getCheckType, checkType);
        }
        if (status != null) {
            wrapper.eq(SystemHealthCheck::getStatus, status);
        }
        if (startTime != null) {
            wrapper.ge(SystemHealthCheck::getCheckTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SystemHealthCheck::getCheckTime, endTime);
        }

        wrapper.orderByDesc(SystemHealthCheck::getCheckTime);

        return page(page, wrapper);
    }

    @Override
    public List<SystemHealthCheck> getRecentHealthChecks(Integer limit) {
        return list(new LambdaQueryWrapper<SystemHealthCheck>()
                .orderByDesc(SystemHealthCheck::getCheckTime)
                .last("LIMIT " + (limit != null ? limit : 100)));
    }

    @Override
    public void cleanExpiredHealthChecks(Integer days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<SystemHealthCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SystemHealthCheck::getCheckTime, expireTime);

        int count = count(wrapper);
        if (count > 0) {
            remove(wrapper);
            log.info("清理过期健康检查记录: {} 条", count);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordHealthCheck(String checkType, String checkName, String status,
                                    Long responseTime, String errorMessage) {
        try {
            SystemHealthCheck healthCheck = new SystemHealthCheck();
            healthCheck.setCheckType(checkType);
            healthCheck.setCheckName(checkName);
            healthCheck.setStatus(status);
            healthCheck.setResponseTime(responseTime);
            healthCheck.setErrorMessage(errorMessage);
            healthCheck.setCheckTime(LocalDateTime.now());
            healthCheck.setAlarmLevel("DOWN".equals(status) ? "ERROR" : "INFO");

            save(healthCheck);
        } catch (Exception e) {
            log.error("记录健康检查失败: checkType={}, checkName={}", checkType, checkName, e);
        }
    }

    /**
     * 定时执行健康检查
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void scheduledHealthCheck() {
        performHealthCheck();
        log.debug("定时健康检查执行完成");
    }
}
