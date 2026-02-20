package com.qoobot.qooerp.hr.performance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 绩效周期实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_performance_cycle")
public class HrPerformanceCycle extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 周期名称
     */
    @TableField("cycle_name")
    private String cycleName;

    /**
     * 周期编码
     */
    @TableField("cycle_code")
    private String cycleCode;

    /**
     * 周期类型：1-月度，2-季度，3-半年度，4-年度
     */
    @TableField("cycle_type")
    private Integer cycleType;

    /**
     * 年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 开始月份（季度/半年度/年度用）
     */
    @TableField("start_month")
    private Integer startMonth;

    /**
     * 结束月份
     */
    @TableField("end_month")
    private Integer endMonth;

    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 自评开始日期
     */
    @TableField("self_assess_start")
    private LocalDate selfAssessStart;

    /**
     * 自评结束日期
     */
    @TableField("self_assess_end")
    private LocalDate selfAssessEnd;

    /**
     * 主管评估开始日期
     */
    @TableField("supervisor_assess_start")
    private LocalDate supervisorAssessStart;

    /**
     * 主管评估结束日期
     */
    @TableField("supervisor_assess_end")
    private LocalDate supervisorAssessEnd;

    /**
     * 状态：0-未开始，1-自评中，2-主管评估中，3-已完成
     */
    @TableField("status")
    private Integer status;

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
