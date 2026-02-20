package com.qoobot.qooerp.finance.cost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本中心DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "成本中心DTO")
public class CostCenterDTO {

    @Schema(description = "成本中心ID")
    private Long id;

    @Schema(description = "成本中心编码")
    private String code;

    @Schema(description = "成本中心名称")
    private String name;

    @Schema(description = "上级成本中心ID")
    private Long parentId;

    @Schema(description = "上级成本中心名称")
    private String parentName;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "成本中心负责人")
    private String managerId;

    @Schema(description = "负责人姓名")
    private String managerName;

    @Schema(description = "年度预算")
    private BigDecimal annualBudget;

    @Schema(description = "已发生成本")
    private BigDecimal actualCost;

    @Schema(description = "状态（0-正常 1-停用）")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
