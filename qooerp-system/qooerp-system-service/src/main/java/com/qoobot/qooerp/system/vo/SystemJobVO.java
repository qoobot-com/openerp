package com.qoobot.qooerp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务VO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "定时任务VO")
public class SystemJobVO {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "任务类名")
    private String jobClass;

    @Schema(description = "Cron表达式")
    private String cronExpression;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "状态：0-暂停 1-运行中")
    private Integer status;

    @Schema(description = "是否并发执行：0-否 1-是")
    private Integer concurrent;

    @Schema(description = "错过策略：1-立即执行 2-执行一次 3-放弃")
    private Integer misfirePolicy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateBy;
}
