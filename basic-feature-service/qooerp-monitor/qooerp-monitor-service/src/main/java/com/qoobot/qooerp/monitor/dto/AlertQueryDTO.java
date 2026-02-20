package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertQueryDTO extends Page<AlertQueryDTO> {
    private String serviceName;
    private String alertType;
    private Integer alertLevel;
    private Integer status;
    private Long handlerId;
    private LocalDateTime alertTimeStart;
    private LocalDateTime alertTimeEnd;
}
