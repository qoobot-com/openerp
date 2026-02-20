package com.qoobot.qooerp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "仪表盘项查询DTO")
public class DashboardItemQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "仪表盘ID")
    private Long dashboardId;

    @Schema(description = "报表ID")
    private Long reportId;

    @Schema(description = "项名称")
    private String itemName;

    @Schema(description = "页码")
    private Integer page = 1;

    @Schema(description = "每页大小")
    private Integer size = 10;
}
