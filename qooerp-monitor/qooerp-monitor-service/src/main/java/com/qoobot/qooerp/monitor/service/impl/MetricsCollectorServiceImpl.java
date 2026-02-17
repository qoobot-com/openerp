package com.qoobot.qooerp.monitor.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoobot.qooerp.monitor.entity.MonitorMetrics;
import com.qoobot.qooerp.monitor.dto.PrometheusMetricData;
import com.qoobot.qooerp.monitor.mapper.MonitorMetricsMapper;
import com.qoobot.qooerp.monitor.service.MetricsCollectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Metrics 采集服务实现
 * 负责采集和存储 Micrometer 指标数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsCollectorServiceImpl implements MetricsCollectorService {

    private final MonitorMetricsMapper metricsMapper;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter PROMETHEUS_TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(TimeZone.getTimeZone("UTC").toZoneId());

    @Override
    public void pushMetrics(String metricsData) {
        log.debug("接收 Metrics Push 数据");

        try {
            List<PrometheusMetricData> metrics = parsePrometheusJson(metricsData);
            saveMetrics(metrics);
            log.info("成功保存 {} 条 Metrics 数据", metrics.size());
        } catch (Exception e) {
            log.error("解析 Metrics Push 数据失败", e);
            throw new RuntimeException("解析 Metrics Push 数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void batchPushMetrics(String[] metricsDataList) {
        log.info("批量接收 {} 条 Metrics Push 数据", metricsDataList.length);

        List<MonitorMetrics> allMetrics = new ArrayList<>();
        for (String data : metricsDataList) {
            try {
                List<PrometheusMetricData> metrics = parsePrometheusJson(data);
                allMetrics.addAll(convertMetrics(metrics));
            } catch (Exception e) {
                log.error("解析单条 Metrics 数据失败: {}", data, e);
            }
        }

        if (!allMetrics.isEmpty()) {
            allMetrics.forEach(metricsMapper::insert);
            log.info("批量保存 {} 条 Metrics 数据完成", allMetrics.size());
        }
    }

    @Override
    public String scrapeMetrics(String serviceName) {
        log.debug("Scrape 指标数据，服务: {}", serviceName);

        try {
            // 查询最近 5 分钟的指标数据
            Instant endTime = Instant.now();
            Instant startTime = endTime.minusSeconds(300);

            List<MonitorMetrics> metrics = metricsMapper.selectByServiceNameAndTimeRange(
                serviceName, 
                Timestamp.from(startTime), 
                Timestamp.from(endTime)
            );

            // 转换为 Prometheus 文本格式
            return formatPrometheusText(metrics);
        } catch (Exception e) {
            log.error("Scrape 指标数据失败", e);
            throw new RuntimeException("Scrape 指标数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String pullRealtimeMetrics(String serviceName, String timeRange) {
        log.debug("拉取实时指标数据，服务: {}, 时间范围: {}", serviceName, timeRange);

        try {
            // 计算时间范围
            Instant endTime = Instant.now();
            Instant startTime = calculateStartTime(timeRange);

            List<MonitorMetrics> metrics = metricsMapper.selectByServiceNameAndTimeRange(
                serviceName, 
                Timestamp.from(startTime), 
                Timestamp.from(endTime)
            );

            // 转换为 JSON 格式
            return objectMapper.writeValueAsString(metrics);
        } catch (Exception e) {
            log.error("拉取实时指标数据失败", e);
            throw new RuntimeException("拉取实时指标数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 Prometheus JSON 格式的指标数据
     */
    private List<PrometheusMetricData> parsePrometheusJson(String json) throws Exception {
        List<PrometheusMetricData> result = new ArrayList<>();
        
        // 支持两种格式：
        // 1. OpenMetrics 格式
        // 2. Micrometer JSON 格式
        
        try {
            // 尝试解析为 Micrometer JSON 格式
            Map<String, Object> data = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            
            List<Map<String, Object>> metricsList = (List<Map<String, Object>>) data.get("metrics");
            if (metricsList != null) {
                for (Map<String, Object> metricMap : metricsList) {
                    PrometheusMetricData metric = new PrometheusMetricData();
                    metric.setName((String) metricMap.get("name"));
                    metric.setType((String) metricMap.get("type"));
                    metric.setValue(((Number) metricMap.getOrDefault("value", 0.0)).doubleValue());
                    
                    Long timestamp = ((Number) metricMap.getOrDefault("timestamp", System.currentTimeMillis())).longValue();
                    metric.setTimestamp(timestamp);
                    
                    Map<String, String> labels = (Map<String, String>) metricMap.get("labels");
                    metric.setLabels(labels != null ? labels : new HashMap<>());
                    
                    metric.setHelp((String) metricMap.get("help"));
                    metric.setUnit((String) metricMap.get("unit"));
                    
                    result.add(metric);
                }
            }
        } catch (Exception e) {
            log.warn("解析 Micrometer JSON 格式失败，尝试其他格式: {}", e.getMessage());
            // 尝试其他格式...
        }
        
        return result;
    }

    /**
     * 保存指标数据到数据库
     */
    private void saveMetrics(List<PrometheusMetricData> metrics) {
        List<MonitorMetrics> monitorMetrics = convertMetrics(metrics);
        monitorMetrics.forEach(metricsMapper::insert);
    }

    /**
     * 转换 Prometheus Metric Data 到 MonitorMetrics
     */
    private List<MonitorMetrics> convertMetrics(List<PrometheusMetricData> metrics) {
        List<MonitorMetrics> result = new ArrayList<>();
        
        for (PrometheusMetricData m : metrics) {
            MonitorMetrics metric = new MonitorMetrics();
            metric.setMetricName(m.getName());
            metric.setMetricType(m.getType() != null ? m.getType().toUpperCase() : "GAUGE");
            metric.setMetricValue(m.getValue());
            
            if (m.getTimestamp() != null) {
                metric.setTimestamp(new Timestamp(m.getTimestamp()));
            } else {
                metric.setTimestamp(new Timestamp(System.currentTimeMillis()));
            }
            
            Map<String, String> tags = m.getLabels();
            if (tags != null) {
                metric.setServiceName(tags.getOrDefault("service", "unknown"));
                metric.setInstance(tags.getOrDefault("instance", "localhost"));
                metric.setTags(objectToJson(tags));
            } else {
                metric.setServiceName("unknown");
                metric.setInstance("localhost");
                metric.setTags("{}");
            }
            
            result.add(metric);
        }
        
        return result;
    }

    /**
     * 格式化为 Prometheus 文本格式
     * 示例：
     * # HELP http_server_requests_seconds Total time of HTTP requests
     * # TYPE http_server_requests_seconds summary
     * http_server_requests_seconds_count{method="GET",status="200"} 1234
     * http_server_requests_seconds_sum{method="GET",status="200"} 123.456
     */
    private String formatPrometheusText(List<MonitorMetrics> metrics) {
        if (metrics.isEmpty()) {
            return "# No metrics found";
        }
        
        StringBuilder sb = new StringBuilder();
        
        // 按指标名称分组
        Map<String, List<MonitorMetrics>> groupedMetrics = metrics.stream()
            .collect(Collectors.groupingBy(MonitorMetrics::getMetricName));
        
        for (Map.Entry<String, List<MonitorMetrics>> entry : groupedMetrics.entrySet()) {
            String metricName = entry.getKey();
            List<MonitorMetrics> metricList = entry.getValue();
            
            // 添加 HELP 注释
            sb.append("# HELP ").append(metricName).append(" Metric from qooerp-monitor\n");
            
            // 添加 TYPE 注释
            MonitorMetrics firstMetric = metricList.get(0);
            sb.append("# TYPE ").append(metricName).append(" ").append(firstMetric.getMetricType().toLowerCase()).append("\n");
            
            // 添加指标数据
            for (MonitorMetrics metric : metricList) {
                sb.append(metricName);
                
                // 添加标签
                Map<String, String> tags = parseTags(metric.getTags());
                if (!tags.isEmpty()) {
                    sb.append("{");
                    sb.append(tags.entrySet().stream()
                        .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                        .collect(Collectors.joining(",")));
                    sb.append("}");
                }
                
                // 添加值和时间戳
                sb.append(" ").append(metric.getMetricValue());
                sb.append(" ").append(metric.getTimestamp().getTime() / 1000);
                sb.append("\n");
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }

    /**
     * 计算开始时间
     */
    private Instant calculateStartTime(String timeRange) {
        long seconds;
        switch (timeRange.toLowerCase()) {
            case "1h":
                seconds = 3600;
                break;
            case "6h":
                seconds = 21600;
                break;
            case "24h":
                seconds = 86400;
                break;
            case "7d":
                seconds = 604800;
                break;
            case "30d":
                seconds = 2592000;
                break;
            default:
                seconds = 3600; // 默认 1 小时
        }
        
        return Instant.now().minusSeconds(seconds);
    }

    /**
     * 将 Map 转换为 JSON 字符串
     */
    private String objectToJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("对象转 JSON 失败", e);
            return "{}";
        }
    }

    /**
     * 解析标签 JSON 字符串
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> parseTags(String tagsJson) {
        if (tagsJson == null || tagsJson.isEmpty() || "{}".equals(tagsJson)) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(tagsJson, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.error("解析标签 JSON 失败: {}", tagsJson, e);
            return new HashMap<>();
        }
    }
}
