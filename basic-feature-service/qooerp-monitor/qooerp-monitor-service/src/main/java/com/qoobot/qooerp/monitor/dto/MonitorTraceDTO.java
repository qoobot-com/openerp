package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MonitorTraceDTO {
    private Long id;
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String serviceName;
    private String operationName;
    private LocalDateTime startTime;
    private Long duration;
    private Integer statusCode;
    private List<Tag> tags;
    private List<LogEntry> logs;
    private LocalDateTime createTime;

    @Data
    public static class Tag {
        private String key;
        private String value;
    }

    @Data
    public static class LogEntry {
        private Long timestamp;
        private String message;
    }
}
