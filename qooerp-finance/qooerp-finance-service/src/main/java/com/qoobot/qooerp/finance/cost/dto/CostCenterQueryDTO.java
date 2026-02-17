package com.qoobot.qooerp.finance.cost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 成本中心查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "成本中心查询DTO")
public class CostCenterQueryDTO {

    @Schema(description = "成本中心编码（模糊查询）")
    private String costCenterCode;

    @Schema(description = "成本中心名称（模糊查询）")
    private String costCenterName;

    @Schema(description = "成本中心类型")
    private String costCenterType;

    @Schema(description = "所属部门ID")
    private Long departmentId;

    @Schema(description = "负责人ID")
    private Long managerId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}
