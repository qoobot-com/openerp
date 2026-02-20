package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TraceQueryDTO extends Page<TraceQueryDTO> {
    private String traceId;
    private String serviceName;
    private String operationName;
    private Integer statusCode;
    private LocalDateTime startTimeStart;
    private LocalDateTime startTimeEnd;
    private Long minDuration;
    private Long maxDuration;
}
