package com.qoobot.qooerp.scheduler.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务报警 DTO
 */
@Data
@Schema(description = "任务报警DTO")
public class ScheduleNotifyDTO {

    @Schema(description = "报警ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long jobId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "报警类型")
    private String notifyType;

    @Schema(description = "报警级别")
    private String notifyLevel;

    @Schema(description = "报警内容")
    private String notifyContent;

    @Schema(description = "报警时间")
    private LocalDateTime notifyTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "处理人")
    private String handler;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理备注")
    private String handleRemark;
}
