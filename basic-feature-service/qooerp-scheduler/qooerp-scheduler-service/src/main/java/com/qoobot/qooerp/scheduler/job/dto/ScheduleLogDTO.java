package com.qoobot.qooerp.scheduler.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务日志 DTO
 */
@Data
@Schema(description = "任务日志DTO")
public class ScheduleLogDTO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long jobId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "执行时间")
    private LocalDateTime executeTime;

    @Schema(description = "执行结果")
    private String executeResult;

    @Schema(description = "执行时长(毫秒)")
    private Long executeDuration;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "异常信息")
    private String exceptionInfo;
}
