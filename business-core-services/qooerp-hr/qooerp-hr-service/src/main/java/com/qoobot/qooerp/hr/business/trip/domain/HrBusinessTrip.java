package com.qoobot.qooerp.hr.business.trip.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出差申请实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_business_trip")
public class HrBusinessTrip extends BaseEntity {

    /**
     * 出差ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 申请人ID
     */
    @TableField("applicant_id")
    private Long applicantId;

    /**
     * 申请人姓名
     */
    @TableField("applicant_name")
    private String applicantName;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 出差地点
     */
    @TableField("destination")
    private String destination;

    /**
     * 出差事由
     */
    @TableField("reason")
    private String reason;

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
     * 出差天数
     */
    @TableField("days")
    private Integer days;

    /**
     * 交通工具：1-飞机 2-火车 3-汽车 4-其他
     */
    @TableField("transport")
    private Integer transport;

    /**
     * 交通费预算
     */
    @TableField("transport_budget")
    private BigDecimal transportBudget;

    /**
     * 住宿费预算
     */
    @TableField("accommodation_budget")
    private BigDecimal accommodationBudget;

    /**
     * 餐饮费预算
     */
    @TableField("meal_budget")
    private BigDecimal mealBudget;

    /**
     * 其他费用预算
     */
    @TableField("other_budget")
    private BigDecimal otherBudget;

    /**
     * 总预算
     */
    @TableField("total_budget")
    private BigDecimal totalBudget;

    /**
     * 审批状态：0-待审批 1-已通过 2-已拒绝 3-已撤销
     */
    @TableField("approval_status")
    private Integer approvalStatus;

    /**
     * 审批人ID
     */
    @TableField("approver_id")
    private Long approverId;

    /**
     * 审批人姓名
     */
    @TableField("approver_name")
    private String approverName;

    /**
     * 审批时间
     */
    @TableField("approval_time")
    private LocalDateTime approvalTime;

    /**
     * 审批意见
     */
    @TableField("approval_comment")
    private String approvalComment;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
