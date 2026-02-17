package com.qoobot.qooerp.finance.cost.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本中心
 */
@Data
@TableName("finance_cost_center")
public class FinanceCostCenter extends BaseEntity {

    /**
     * 成本中心编码
     */
    private String code;

    /**
     * 成本中心名称
     */
    private String name;

    /**
     * 上级成本中心ID
     */
    private Long parentId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 成本中心负责人
     */
    private String managerId;

    /**
     * 负责人姓名
     */
    private String managerName;

    /**
     * 年度预算
     */
    private BigDecimal annualBudget;

    /**
     * 已发生成本
     */
    private BigDecimal actualCost;

    /**
     * 状态（0-正常 1-停用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
