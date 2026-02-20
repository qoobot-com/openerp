package com.qoobot.qooerp.scheduler.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务统计DTO
 */
@Data
@Schema(description = "任务统计DTO")
public class JobStatisticsDTO {

    @Schema(description = "任务ID")
    private Long jobId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务类型")
    private String jobType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "执行次数")
    private Long executeCount;

    @Schema(description = "成功次数")
    private Long successCount;

    @Schema(description = "失败次数")
    private Long failCount;

    @Schema(description = "成功率")
    private String successRate;

    @Schema(description = "平均执行时长(毫秒)")
    private Long avgExecuteDuration;

    @Schema(description = "下次执行时间")
    private LocalDateTime nextExecuteTime;

    @Schema(description = "最后执行时间")
    private LocalDateTime lastExecuteTime;

    @Schema(description = "最后执行状态")
    private String lastStatus;
}
