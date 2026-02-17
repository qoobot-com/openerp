package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricsQueryDTO extends Page<MetricsQueryDTO> {
    private String serviceName;
    private String metricType;
    private String metricName;
    private LocalDateTime collectTimeStart;
    private LocalDateTime collectTimeEnd;
}
