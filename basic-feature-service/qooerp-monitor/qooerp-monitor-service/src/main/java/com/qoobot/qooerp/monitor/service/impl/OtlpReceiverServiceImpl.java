package com.qoobot.qooerp.monitor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoobot.qooerp.monitor.entity.MonitorLog;
import com.qoobot.qooerp.monitor.entity.MonitorMetrics;
import com.qoobot.qooerp.monitor.entity.MonitorTrace;
import com.qoobot.qooerp.monitor.dto.OtlpLogData;
import com.qoobot.qooerp.monitor.dto.OtlpMetricData;
import com.qoobot.qooerp.monitor.dto.OtlpTraceData;
import com.qoobot.qooerp.monitor.mapper.MonitorLogMapper;
import com.qoobot.qooerp.monitor.mapper.MonitorMetricsMapper;
import com.qoobot.qooerp.monitor.mapper.MonitorTraceMapper;
import com.qoobot.qooerp.monitor.service.OtlpReceiverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OTLP 接收服务实现
 * 负责接收和解析 OpenTelemetry 协议数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtlpReceiverServiceImpl implements OtlpReceiverService {

    private final MonitorTraceMapper traceMapper;
    private final MonitorMetricsMapper metricsMapper;
    private final MonitorLogMapper logMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void receiveTraces(byte[] data, String contentType) {
        log.debug("接收 OTLP Traces 数据，大小: {} bytes, 类型: {}", data.length, contentType);

        try {
            List<OtlpTraceData> traces = parseOtlpTraces(data, contentType);
            saveTraces(traces);
            log.info("成功保存 {} 条 Traces 数据", traces.size());
        } catch (Exception e) {
            log.error("解析 Traces 数据失败", e);
            throw new RuntimeException("解析 Traces 数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void receiveMetrics(byte[] data, String contentType) {
        log.debug("接收 OTLP Metrics 数据，大小: {} bytes, 类型: {}", data.length, contentType);

        try {
            List<OtlpMetricData> metrics = parseOtlpMetrics(data, contentType);
            saveMetrics(metrics);
            log.info("成功保存 {} 条 Metrics 数据", metrics.size());
        } catch (Exception e) {
            log.error("解析 Metrics 数据失败", e);
            throw new RuntimeException("解析 Metrics 数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void receiveLogs(byte[] data, String contentType) {
        log.debug("接收 OTLP Logs 数据，大小: {} bytes, 类型: {}", data.length, contentType);

        try {
            List<OtlpLogData> logs = parseOtlpLogs(data, contentType);
            saveLogs(logs);
            log.info("成功保存 {} 条 Logs 数据", logs.size());
        } catch (Exception e) {
            log.error("解析 Logs 数据失败", e);
            throw new RuntimeException("解析 Logs 数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void batchReceiveTraces(byte[][] dataList, String contentType) {
        log.info("批量接收 {} 条 Traces 数据", dataList.length);

        List<MonitorTrace> allTraces = new ArrayList<>();
        for (byte[] data : dataList) {
            try {
                List<OtlpTraceData> traces = parseOtlpTraces(data, contentType);
                allTraces.addAll(convertTraces(traces));
            } catch (Exception e) {
                log.error("解析单条 Traces 数据失败", e);
            }
        }

        if (!allTraces.isEmpty()) {
            batchSaveTraces(allTraces);
            log.info("批量保存 {} 条 Traces 数据完成", allTraces.size());
        }
    }

    @Override
    @Async
    public void batchReceiveMetrics(byte[][] dataList, String contentType) {
        log.info("批量接收 {} 条 Metrics 数据", dataList.length);

        List<MonitorMetrics> allMetrics = new ArrayList<>();
        for (byte[] data : dataList) {
            try {
                List<OtlpMetricData> metrics = parseOtlpMetrics(data, contentType);
                allMetrics.addAll(convertMetrics(metrics));
            } catch (Exception e) {
                log.error("解析单条 Metrics 数据失败", e);
            }
        }

        if (!allMetrics.isEmpty()) {
            batchSaveMetrics(allMetrics);
            log.info("批量保存 {} 条 Metrics 数据完成", allMetrics.size());
        }
    }

    @Override
    @Async
    public void batchReceiveLogs(byte[][] dataList, String contentType) {
        log.info("批量接收 {} 条 Logs 数据", dataList.length);

        List<MonitorLog> allLogs = new ArrayList<>();
        for (byte[] data : dataList) {
            try {
                List<OtlpLogData> logs = parseOtlpLogs(data, contentType);
                allLogs.addAll(convertLogs(logs));
            } catch (Exception e) {
                log.error("解析单条 Logs 数据失败", e);
            }
        }

        if (!allLogs.isEmpty()) {
            batchSaveLogs(allLogs);
            log.info("批量保存 {} 条 Logs 数据完成", allLogs.size());
        }
    }

    /**
     * 解析 OTLP Traces 数据
     * 注意：实际项目应使用 OTLP Protobuf 解析库，这里简化为 JSON 格式
     */
    private List<OtlpTraceData> parseOtlpTraces(byte[] data, String contentType) throws Exception {
        if (contentType != null && contentType.contains("json")) {
            // JSON 格式解析
            String json = new String(data);
            Map<String, Object> request = objectMapper.readValue(json, Map.class);

            List<OtlpTraceData> traces = new ArrayList<>();
            List<Map<String, Object>> resourceSpans = (List<Map<String, Object>>) request.get("resourceSpans");
            if (resourceSpans != null) {
                for (Map<String, Object> rs : resourceSpans) {
                    String serviceName = getServiceName(rs);
                    String instance = getInstance(rs);

                    List<Map<String, Object>> spans = (List<Map<String, Object>>) rs.get("scopeSpans");
                    if (spans != null) {
                        for (Map<String, Object> s : spans) {
                            List<Map<String, Object>> spanList = (List<Map<String, Object>>) s.get("spans");
                            if (spanList != null) {
                                for (Map<String, Object> span : spanList) {
                                    OtlpTraceData trace = new OtlpTraceData();
                                    trace.setTraceId(getHex(span, "traceId"));
                                    trace.setSpanId(getHex(span, "spanId"));
                                    trace.setParentSpanId(getHex(span, "parentSpanId"));
                                    trace.setSpanName(getString(span, "name"));
                                    trace.setSpanKind(getString(span, "kind"));
                                    trace.setTimestamp(getInstant(span, "startTimeUnixNano"));
                                    trace.setDurationMs(getDurationMs(span));
                                    trace.setStatusCode(getStatusCode(span));
                                    trace.setStatusMessage(getStatusMessage(span));
                                    trace.setServiceName(serviceName);
                                    trace.setInstance(instance);
                                    trace.setTags(getAttributes(span));

                                    traces.add(trace);
                                }
                            }
                        }
                    }
                }
            }
            return traces;
        }

        // TODO: 实现 Protobuf 格式解析
        // 暂时返回空列表
        log.warn("Protobuf 格式解析待实现");
        return new ArrayList<>();
    }

    /**
     * 解析 OTLP Metrics 数据
     */
    private List<OtlpMetricData> parseOtlpMetrics(byte[] data, String contentType) throws Exception {
        if (contentType != null && contentType.contains("json")) {
            String json = new String(data);
            Map<String, Object> request = objectMapper.readValue(json, Map.class);

            List<OtlpMetricData> metrics = new ArrayList<>();
            List<Map<String, Object>> resourceMetrics = (List<Map<String, Object>>) request.get("resourceMetrics");
            if (resourceMetrics != null) {
                for (Map<String, Object> rm : resourceMetrics) {
                    String serviceName = getServiceName(rm);
                    String instance = getInstance(rm);

                    List<Map<String, Object>> scopeMetrics = (List<Map<String, Object>>) rm.get("scopeMetrics");
                    if (scopeMetrics != null) {
                        for (Map<String, Object> sm : scopeMetrics) {
                            List<Map<String, Object>> metricList = (List<Map<String, Object>>) sm.get("metrics");
                            if (metricList != null) {
                                for (Map<String, Object> metric : metricList) {
                                    OtlpMetricData m = new OtlpMetricData();
                                    m.setMetricName(getString(metric, "name"));
                                    m.setMetricType(getMetricType(metric));
                                    m.setValue(getMetricValue(metric));
                                    m.setTimestamp(getInstant(metric, "data", "points", String.valueOf(0), "timeUnixNano"));
                                    m.setServiceName(serviceName);
                                    m.setInstance(instance);
                                    m.setTags(getAttributes(metric));

                                    metrics.add(m);
                                }
                            }
                        }
                    }
                }
            }
            return metrics;
        }

        log.warn("Protobuf 格式解析待实现");
        return new ArrayList<>();
    }

    /**
     * 解析 OTLP Logs 数据
     */
    private List<OtlpLogData> parseOtlpLogs(byte[] data, String contentType) throws Exception {
        if (contentType != null && contentType.contains("json")) {
            String json = new String(data);
            Map<String, Object> request = objectMapper.readValue(json, Map.class);

            List<OtlpLogData> logs = new ArrayList<>();
            List<Map<String, Object>> resourceLogs = (List<Map<String, Object>>) request.get("resourceLogs");
            if (resourceLogs != null) {
                for (Map<String, Object> rl : resourceLogs) {
                    String serviceName = getServiceName(rl);
                    String instance = getInstance(rl);

                    List<Map<String, Object>> scopeLogs = (List<Map<String, Object>>) rl.get("scopeLogs");
                    if (scopeLogs != null) {
                        for (Map<String, Object> sl : scopeLogs) {
                            List<Map<String, Object>> logRecords = (List<Map<String, Object>>) sl.get("logRecords");
                            if (logRecords != null) {
                                for (Map<String, Object> record : logRecords) {
                                    OtlpLogData log = new OtlpLogData();
                                    log.setTimestamp(getInstant(record, "timeUnixNano"));
                                    log.setLevel(getSeverity(record));
                                    log.setMessage(getString(record, "body", "stringValue"));
                                    log.setTraceId(getHex(record, "traceId"));
                                    log.setSpanId(getHex(record, "spanId"));
                                    log.setServiceName(serviceName);
                                    log.setInstance(instance);
                                    log.setTags(getAttributes(record));

                                    logs.add(log);
                                }
                            }
                        }
                    }
                }
            }
            return logs;
        }

        log.warn("Protobuf 格式解析待实现");
        return new ArrayList<>();
    }

    /**
     * 保存 Traces 数据到数据库
     */
    private void saveTraces(List<OtlpTraceData> traces) {
        List<MonitorTrace> monitorTraces = convertTraces(traces);
        monitorTraces.forEach(traceMapper::insert);
    }

    /**
     * 批量保存 Traces 数据
     */
    private void batchSaveTraces(List<MonitorTrace> traces) {
        // 使用 MyBatis-Plus 批量插入
        traces.forEach(traceMapper::insert);
    }

    /**
     * 保存 Metrics 数据到数据库
     */
    private void saveMetrics(List<OtlpMetricData> metrics) {
        List<MonitorMetrics> monitorMetrics = convertMetrics(metrics);
        monitorMetrics.forEach(metricsMapper::insert);
    }

    /**
     * 批量保存 Metrics 数据
     */
    private void batchSaveMetrics(List<MonitorMetrics> metrics) {
        metrics.forEach(metricsMapper::insert);
    }

    /**
     * 保存 Logs 数据到数据库
     */
    private void saveLogs(List<OtlpLogData> logs) {
        List<MonitorLog> monitorLogs = convertLogs(logs);
        monitorLogs.forEach(logMapper::insert);
    }

    /**
     * 批量保存 Logs 数据
     */
    private void batchSaveLogs(List<MonitorLog> logs) {
        logs.forEach(logMapper::insert);
    }

    // ========== 转换方法 ==========

    private List<MonitorTrace> convertTraces(List<OtlpTraceData> traces) {
        List<MonitorTrace> result = new ArrayList<>();
        for (OtlpTraceData t : traces) {
            MonitorTrace trace = new MonitorTrace();
            trace.setTime(java.sql.Timestamp.from(t.getTimestamp()));
            trace.setTraceId(t.getTraceId());
            trace.setSpanId(t.getSpanId());
            trace.setParentSpanId(t.getParentSpanId());
            trace.setSpanName(t.getSpanName());
            trace.setSpanKind(t.getSpanKind());
            trace.setStartTime(java.sql.Timestamp.from(t.getTimestamp()));
            trace.setDurationMs(t.getDurationMs());
            trace.setStatusCode(t.getStatusCode());
            trace.setStatusMessage(t.getStatusMessage());
            trace.setServiceName(t.getServiceName());
            trace.setInstance(t.getInstance());
            trace.setTags(mapToJson(t.getTags()));
            result.add(trace);
        }
        return result;
    }

    private List<MonitorMetrics> convertMetrics(List<OtlpMetricData> metrics) {
        List<MonitorMetrics> result = new ArrayList<>();
        for (OtlpMetricData m : metrics) {
            MonitorMetrics metric = new MonitorMetrics();
            metric.setTime(java.sql.Timestamp.from(m.getTimestamp()));
            metric.setMetricName(m.getMetricName());
            metric.setMetricType(m.getMetricType());
            metric.setMetricValue(m.getValue());
            metric.setServiceName(m.getServiceName());
            metric.setInstance(m.getInstance());
            metric.setTags(mapToJson(m.getTags()));
            metric.setMetricUnit(m.getUnit());
            metric.setDescription(m.getDescription());
            result.add(metric);
        }
        return result;
    }

    private List<MonitorLog> convertLogs(List<OtlpLogData> logs) {
        List<MonitorLog> result = new ArrayList<>();
        for (OtlpLogData l : logs) {
            MonitorLog log = new MonitorLog();
            log.setTime(java.sql.Timestamp.from(l.getTimestamp()));
            log.setLevel(l.getLevel());
            log.setLoggerName(l.getLoggerName());
            log.setMessage(l.getMessage());
            log.setTraceId(l.getTraceId());
            log.setSpanId(l.getSpanId());
            log.setThreadName(l.getThreadName());
            log.setServiceName(l.getServiceName());
            log.setInstance(l.getInstance());
            log.setContext(mapToJson(l.getContext()));
            result.add(log);
        }
        return result;
    }

    // ========== 工具方法 ==========

    private String getServiceName(Map<String, Object> resource) {
        Map<String, Object> attributes = (Map<String, Object>) resource.get("attributes");
        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if ("service.name".equals(entry.getKey())) {
                    return entry.getValue().toString();
                }
            }
        }
        return "unknown";
    }

    private String getInstance(Map<String, Object> resource) {
        Map<String, Object> attributes = (Map<String, Object>) resource.get("attributes");
        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if ("service.instance.id".equals(entry.getKey())) {
                    return entry.getValue().toString();
                }
            }
        }
        return "localhost";
    }

    private Map<String, String> getAttributes(Map<String, Object> obj) {
        Map<String, Object> attrs = (Map<String, Object>) obj.get("attributes");
        Map<String, String> result = new HashMap<>();
        if (attrs != null) {
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                result.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }
        return result;
    }

    private String mapToJson(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Map 转 JSON 失败", e);
            return "{}";
        }
    }

    private String getString(Map<String, Object> obj, String... keys) {
        Object current = obj;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current != null ? current.toString() : null;
    }

    private String getHex(Map<String, Object> obj, String key) {
        Object value = obj.get(key);
        if (value != null) {
            // OTLP traceId/spanId 是字节数组，需要转换为十六进制字符串
            if (value instanceof byte[]) {
                return bytesToHex((byte[]) value);
            }
            return value.toString();
        }
        return null;
    }

    private Instant getInstant(Map<String, Object> obj, String... keys) {
        Long nanos = getLong(obj, keys);
        if (nanos != null) {
            return Instant.ofEpochSecond(0, nanos);
        }
        return Instant.now();
    }

    private Long getDurationMs(Map<String, Object> obj) {
        Long nanos = getLong(obj, "endTimeUnixNano");
        Long startNanos = getLong(obj, "startTimeUnixNano");
        if (nanos != null && startNanos != null) {
            return (nanos - startNanos) / 1_000_000;
        }
        return 0L;
    }

    private String getStatusCode(Map<String, Object> obj) {
        Map<String, Object> status = (Map<String, Object>) obj.get("status");
        if (status != null) {
            Integer code = (Integer) status.get("code");
            if (code != null) {
                return code == 0 ? "OK" : "ERROR";
            }
        }
        return "OK";
    }

    private String getStatusMessage(Map<String, Object> obj) {
        Map<String, Object> status = (Map<String, Object>) obj.get("status");
        if (status != null) {
            Map<String, Object> message = (Map<String, Object>) status.get("message");
            if (message != null) {
                return getString(message, "stringValue");
            }
        }
        return null;
    }

    private String getSeverity(Map<String, Object> obj) {
        Integer severity = (Integer) obj.get("severityNumber");
        if (severity != null) {
            if (severity <= 9) return "TRACE";
            if (severity <= 12) return "DEBUG";
            if (severity <= 16) return "INFO";
            if (severity <= 20) return "WARN";
            return "ERROR";
        }
        return "INFO";
    }

    private String getMetricType(Map<String, Object> obj) {
        String type = getString(obj, "metadata", "type");
        return type != null ? type.toUpperCase() : "GAUGE";
    }

    private Double getMetricValue(Map<String, Object> obj) {
        String type = getMetricType(obj);
        if ("GAUGE".equals(type)) {
            return getDouble(obj, "data", "points", String.valueOf(0), "asDouble");
        } else if ("COUNTER".equals(type)) {
            return getDouble(obj, "data", "points", String.valueOf(0), "asInt");
        }
        return 0.0;
    }

    private Long getLong(Map<String, Object> obj, String... keys) {
        Object current = obj;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current != null ? ((Number) current).longValue() : null;
    }

    private Double getDouble(Map<String, Object> obj, String... keys) {
        Object current = obj;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current != null ? ((Number) current).doubleValue() : null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
