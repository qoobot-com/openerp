package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.entity.SystemMonitor;
import com.qoobot.qooerp.system.service.SystemMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统监控控制器
 */
@RestController
@RequestMapping("/api/system/monitor")
@RequiredArgsConstructor
public class SystemMonitorController {

    private final SystemMonitorService monitorService;

    /**
     * 获取当前性能概览
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getCurrentPerformanceOverview() {
        return Result.success(monitorService.getCurrentPerformanceOverview());
    }

    /**
     * 获取CPU监控数据
     */
    @GetMapping("/cpu")
    public Result<List<SystemMonitor>> getCpuMonitorData(
            @RequestParam(required = false) String instanceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.getCpuMonitorData(instanceId, startTime, endTime));
    }

    /**
     * 获取内存监控数据
     */
    @GetMapping("/memory")
    public Result<List<SystemMonitor>> getMemoryMonitorData(
            @RequestParam(required = false) String instanceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.getMemoryMonitorData(instanceId, startTime, endTime));
    }

    /**
     * 获取磁盘监控数据
     */
    @GetMapping("/disk")
    public Result<List<SystemMonitor>> getDiskMonitorData(
            @RequestParam(required = false) String instanceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.getDiskMonitorData(instanceId, startTime, endTime));
    }

    /**
     * 获取网络监控数据
     */
    @GetMapping("/network")
    public Result<List<SystemMonitor>> getNetworkMonitorData(
            @RequestParam(required = false) String instanceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.getNetworkMonitorData(instanceId, startTime, endTime));
    }

    /**
     * 获取JVM监控数据
     */
    @GetMapping("/jvm")
    public Result<List<SystemMonitor>> getJvmMonitorData(
            @RequestParam(required = false) String instanceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.getJvmMonitorData(instanceId, startTime, endTime));
    }

    /**
     * 获取业务监控数据
     */
    @GetMapping("/business")
    public Result<List<SystemMonitor>> getBusinessMonitorData(
            @RequestParam String serviceName,
            @RequestParam String metricName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.getBusinessMonitorData(serviceName, metricName, startTime, endTime));
    }

    /**
     * 分页查询监控数据
     */
    @GetMapping("/page")
    public Result<IPage<SystemMonitor>> pageMonitorData(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String monitorType,
            @RequestParam(required = false) String instanceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(monitorService.pageMonitorData(current, size, monitorType,
                instanceId, startTime, endTime));
    }

    /**
     * 清理过期监控数据
     */
    @DeleteMapping("/clean")
    public Result<Void> cleanExpiredMonitorData(@RequestParam Integer days) {
        monitorService.cleanExpiredMonitorData(days);
        return Result.success();
    }
}
