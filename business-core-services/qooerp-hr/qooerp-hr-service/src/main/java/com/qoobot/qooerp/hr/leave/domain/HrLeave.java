package com.qoobot.qooerp.hr.leave.domain;

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
 * 请假申请实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_leave")
public class HrLeave extends BaseEntity {

    /**
     * 请假ID
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
     * 请假类型：1-事假 2-病假 3-年假 4-婚假 5-产假 6-丧假 7-调休 8-其他
     */
    @TableField("leave_type")
    private Integer leaveType;

    /**
     * 请假开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 请假开始时间（空表示全天）
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 请假结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 请假结束时间（空表示全天）
     */
    @TableField("end_time")
    private String endTime;

    /**
     * 请假天数
     */
    @TableField("leave_days")
    private BigDecimal leaveDays;

    /**
     * 请假原因
     */
    @TableField("reason")
    private String reason;

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
     * 附件ID（多个逗号分隔）
     */
    @TableField("attachment_ids")
    private String attachmentIds;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
