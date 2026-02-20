package com.qoobot.qooerp.scheduler.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务查询 DTO
 */
@Data
@Schema(description = "任务查询DTO")
public class JobQueryDTO {

    @Schema(description = "任务编号")
    private String jobNo;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务类型")
    private String jobType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "当前页码")
    private Integer current = 1;

    @Schema(description = "每页条数")
    private Integer size = 10;
}
