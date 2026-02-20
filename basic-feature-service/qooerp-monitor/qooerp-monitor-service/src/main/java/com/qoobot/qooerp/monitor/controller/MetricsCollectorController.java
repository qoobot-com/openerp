package com.qoobot.qooerp.monitor.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.service.MetricsCollectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Metrics 采集控制器
 * 提供 Micrometer 指标的 Push/Scrape 接口
 */
@Slf4j
@RestController
@RequestMapping("/monitor/api/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics 采集", description = "Micrometer 指标数据采集接口")
public class MetricsCollectorController {

    private final MetricsCollectorService metricsCollectorService;

    /**
     * 推送指标数据（JSON 格式）
     * 
     * 请求体格式示例：
     * {
     *   "metrics": [
     *     {
     *       "name": "http_server_requests_seconds",
     *       "type": "summary",
     *       "value": 0.123,
     *       "timestamp": 1234567890123,
     *       "labels": {
     *         "service": "qooerp-auth",
     *         "instance": "localhost:8081",
     *         "method": "GET",
     *         "status": "200"
     *       },
     *       "help": "Total time of HTTP requests",
     *       "unit": "seconds"
     *     }
     *   ]
     * }
     */
    @Operation(summary = "推送指标数据")
    @PostMapping(value = "/push", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> pushMetrics(@RequestBody String metricsData) {
        log.debug("接收 Metrics Push 请求");
        metricsCollectorService.pushMetrics(metricsData);
        return Result.success();
    }

    /**
     * 批量推送指标数据
     */
    @Operation(summary = "批量推送指标数据")
    @PostMapping(value = "/push/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> batchPushMetrics(@RequestBody String[] metricsDataList) {
        log.info("接收批量 Metrics Push 请求，数量: {}", metricsDataList.length);
        metricsCollectorService.batchPushMetrics(metricsDataList);
        return Result.success();
    }

    /**
     * Scrape 指标数据（Prometheus 格式）
     * 
     * 兼容 Prometheus 的 scrape 接口
     * 返回格式：text/plain（Prometheus 文本格式）
     * 
     * 查询参数：
     * - serviceName: 服务名称（可选）
     * 
     * 示例：
     * GET /monitor/api/metrics/scrape?serviceName=qooerp-auth
     * 
     * 返回示例：
     * # HELP http_server_requests_seconds Total time of HTTP requests
     * # TYPE http_server_requests_seconds summary
     * http_server_requests_seconds_count{method="GET",service="qooerp-auth"} 1234 1234567890
     * http_server_requests_seconds_sum{method="GET",service="qooerp-auth"} 123.456 1234567890
     */
    @Operation(summary = "Scrape 指标数据（Prometheus 格式）")
    @GetMapping(value = "/scrape", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String scrapeMetrics(
            @Parameter(description = "服务名称")
            @RequestParam(required = false) String serviceName) {
        
        log.debug("接收 Metrics Scrape 请求，服务: {}", serviceName);
        return metricsCollectorService.scrapeMetrics(serviceName);
    }

    /**
     * 拉取实时指标数据（JSON 格式）
     * 
     * 查询参数：
     * - serviceName: 服务名称（必填）
     * - timeRange: 时间范围（1h, 6h, 24h, 7d, 30d，默认 1h）
     */
    @Operation(summary = "拉取实时指标数据")
    @GetMapping(value = "/realtime", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> pullRealtimeMetrics(
            @Parameter(description = "服务名称", required = true)
            @RequestParam String serviceName,
            @Parameter(description = "时间范围（1h, 6h, 24h, 7d, 30d）")
            @RequestParam(defaultValue = "1h") String timeRange) {
        
        log.debug("拉取实时指标数据，服务: {}, 时间范围: {}", serviceName, timeRange);
        String metrics = metricsCollectorService.pullRealtimeMetrics(serviceName, timeRange);
        return Result.success(metrics);
    }

    /**
     * 健康检查
     */
    @Operation(summary = "Metrics 采集器健康检查")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Metrics Collector is running");
    }
}
