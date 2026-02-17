package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.entity.SystemHealthCheck;
import com.qoobot.qooerp.system.service.SystemHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/system/health")
@RequiredArgsConstructor
public class SystemHealthController {

    private final SystemHealthService healthService;

    /**
     * 执行健康检查
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> performHealthCheck() {
        return Result.success(healthService.performHealthCheck());
    }

    /**
     * 获取健康状态
     */
    @GetMapping("/status")
    public Result<String> getHealthStatus() {
        return Result.success(healthService.getHealthStatus());
    }

    /**
     * 获取数据库健康状态
     */
    @GetMapping("/database")
    public Result<Map<String, Object>> checkDatabase() {
        return Result.success(healthService.checkDatabase());
    }

    /**
     * 获取Redis健康状态
     */
    @GetMapping("/redis")
    public Result<Map<String, Object>> checkRedis() {
        return Result.success(healthService.checkRedis());
    }

    /**
     * 获取消息队列健康状态
     */
    @GetMapping("/mq")
    public Result<Map<String, Object>> checkMessageQueue() {
        return Result.success(healthService.checkMessageQueue());
    }

    /**
     * 检测故障
     */
    @GetMapping("/failures")
    public Result<List<Map<String, Object>>> detectFailures() {
        return Result.success(healthService.detectFailures());
    }

    /**
     * 分页查询健康检查记录
     */
    @GetMapping("/page")
    public Result<IPage<SystemHealthCheck>> pageHealthChecks(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String checkType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(healthService.pageHealthChecks(current, size, checkType,
                status, startTime, endTime));
    }

    /**
     * 获取最近健康检查记录
     */
    @GetMapping("/recent")
    public Result<List<SystemHealthCheck>> getRecentHealthChecks(
            @RequestParam(required = false, defaultValue = "100") Integer limit) {
        return Result.success(healthService.getRecentHealthChecks(limit));
    }

    /**
     * 清理过期健康检查记录
     */
    @DeleteMapping("/clean")
    public Result<Void> cleanExpiredHealthChecks(@RequestParam Integer days) {
        healthService.cleanExpiredHealthChecks(days);
        return Result.success();
    }
}
