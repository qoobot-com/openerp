package com.qoobot.qooerp.scheduler.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 任务执行 DTO
 */
@Data
@Schema(description = "任务执行DTO")
public class JobExecuteDTO {

    @Schema(description = "任务ID")
    private Long jobId;

    @Schema(description = "执行参数")
    private Map<String, Object> params;
}
