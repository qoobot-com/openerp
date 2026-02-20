package com.qoobot.qooerp.scheduler.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 定时任务 DTO
 */
@Data
@Schema(description = "定时任务DTO")
public class ScheduleJobDTO {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务编号")
    private String jobNo;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务类型")
    private String jobType;

    @Schema(description = "任务类全限定名")
    private String jobClass;

    @Schema(description = "Cron表达式")
    private String cronExpression;

    @Schema(description = "执行间隔(秒)")
    private Integer interval;

    @Schema(description = "执行策略")
    private String executeStrategy;

    @Schema(description = "重试策略")
    private String retryStrategy;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "超时时间(秒)")
    private Integer timeout;

    @Schema(description = "并发策略")
    private String concurrency;

    @Schema(description = "依赖任务ID")
    private Long dependentJobId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "下次执行时间")
    private LocalDateTime nextExecuteTime;

    @Schema(description = "上次执行时间")
    private LocalDateTime prevExecuteTime;

    @Schema(description = "执行次数")
    private Long executeCount;

    @Schema(description = "成功次数")
    private Long successCount;

    @Schema(description = "失败次数")
    private Long failCount;

    @Schema(description = "任务配置")
    private Map<String, String> config;
}
