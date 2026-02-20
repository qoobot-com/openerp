package com.qoobot.qooerp.scheduler.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 监控统计DTO
 */
@Data
@Schema(description = "监控统计DTO")
public class MonitorStatisticsDTO {

    @Schema(description = "任务总数")
    private Long totalJobs;

    @Schema(description = "运行中任务数")
    private Long runningJobs;

    @Schema(description = "暂停任务数")
    private Long pausedJobs;

    @Schema(description = "停止任务数")
    private Long stoppedJobs;

    @Schema(description = "总执行次数")
    private Long totalExecutions;

    @Schema(description = "总成功次数")
    private Long totalSuccess;

    @Schema(description = "总失败次数")
    private Long totalFailures;

    @Schema(description = "整体成功率")
    private String overallSuccessRate;

    @Schema(description = "今日执行次数")
    private Long todayExecutions;
}
