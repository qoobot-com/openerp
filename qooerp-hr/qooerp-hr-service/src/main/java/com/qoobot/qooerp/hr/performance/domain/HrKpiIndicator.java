package com.qoobot.qooerp.hr.performance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * KPI指标实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_kpi_indicator")
public class HrKpiIndicator extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 指标名称
     */
    @TableField("indicator_name")
    private String indicatorName;

    /**
     * 指标编码
     */
    @TableField("indicator_code")
    private String indicatorCode;

    /**
     * 指标分类：1-财务指标，2-客户指标，3-内部流程指标，4-学习成长指标
     */
    @TableField("indicator_type")
    private Integer indicatorType;

    /**
     * 指标说明
     */
    @TableField("indicator_desc")
    private String indicatorDesc;

    /**
     * 计量单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 最低目标值
     */
    @TableField("min_target")
    private BigDecimal minTarget;

    /**
     * 目标值
     */
    @TableField("target_value")
    private BigDecimal targetValue;

    /**
     * 挑战值
     */
    @TableField("challenge_value")
    private BigDecimal challengeValue;

    /**
     * 权重（%）
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 计算公式
     */
    @TableField("formula")
    private String formula;

    /**
     * 评估周期：1-月度，2-季度，3-半年度，4-年度
     */
    @TableField("assess_cycle")
    private Integer assessCycle;

    /**
     * 适用岗位ID
     */
    @TableField("position_id")
    private Long positionId;

    /**
     * 是否启用
     */
    @TableField("is_enabled")
    private Boolean isEnabled;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
