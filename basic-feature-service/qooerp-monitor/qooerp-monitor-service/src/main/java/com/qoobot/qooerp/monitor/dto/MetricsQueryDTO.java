package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MetricsQueryDTO extends Page<MetricsQueryDTO> {
    private String serviceName;
    private String metricType;
    private String metricName;
    private LocalDateTime collectTimeStart;
    private LocalDateTime collectTimeEnd;
}
