package com.qoobot.qooerp.hr.overtime.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 加班申请实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_overtime")
public class HrOvertime extends BaseEntity {

    /**
     * 加班ID
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
     * 加班类型：1-工作日加班 2-周末加班 3-节假日加班
     */
    @TableField("overtime_type")
    private Integer overtimeType;

    /**
     * 加班开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 加班结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 加班时长（小时）
     */
    @TableField("duration")
    private Double duration;

    /**
     * 加班原因
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
     * 调休状态：0-未使用 1-已调休 2-已过期
     */
    @TableField("leave_status")
    private Integer leaveStatus;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
