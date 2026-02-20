package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogQueryDTO extends Page<LogQueryDTO> {
    private String serviceName;
    private String logType;
    private String logLevel;
    private LocalDateTime logTimeStart;
    private LocalDateTime logTimeEnd;
    private String keyword;
}
