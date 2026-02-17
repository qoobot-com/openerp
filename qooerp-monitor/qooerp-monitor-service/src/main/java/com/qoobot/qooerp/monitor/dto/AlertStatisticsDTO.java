package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AlertStatisticsDTO {
    private Long totalCount;
    private Long pendingCount;
    private Long handledCount;
    private Long closedCount;
    private Map<Integer, Long> countByLevel;
    private Map<String, Long> countByType;
    private Map<String, Long> countByService;
}
