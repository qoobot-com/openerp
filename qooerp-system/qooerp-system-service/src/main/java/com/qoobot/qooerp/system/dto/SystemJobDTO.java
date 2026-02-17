package com.qoobot.qooerp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 定时任务DTO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "定时任务DTO")
public class SystemJobDTO {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务名称", required = true)
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @Schema(description = "任务组", required = true)
    @NotBlank(message = "任务组不能为空")
    private String jobGroup;

    @Schema(description = "任务类名", required = true)
    @NotBlank(message = "任务类名不能为空")
    private String jobClass;

    @Schema(description = "Cron表达式", required = true)
    @NotBlank(message = "Cron表达式不能为空")
    private String cronExpression;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "状态：0-暂停 1-运行中")
    private Integer status;

    @Schema(description = "是否并发执行：0-否 1-是")
    private Integer concurrent;

    @Schema(description = "错过策略：1-立即执行 2-执行一次 3-放弃")
    private Integer misfirePolicy;
}
