package com.qoobot.qooerp.hr.lifecycle.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

/**
 * 调动管理实体
 */
@Data
@TableName("hr_transfer")
public class HrTransfer {

    /**
     * 调动ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 调动单号
     */
    private String transferNo;

    /**
     * 调动类型
     */
    private String transferType; // PROMOTION-晋升, DEMOTION-降职, TRANSFER-调动, DEPARTURE-离职

    /**
     * 调动状态
     */
    private String status; // DRAFT-草稿, PENDING-待审批, APPROVED-已批准, REJECTED-已拒绝, CANCELLED-已取消

    /**
     * 原部门ID
     */
    private Long fromDepartmentId;

    /**
     * 原岗位ID
     */
    private Long fromPositionId;

    /**
     * 原职位级别
     */
    private String fromPositionLevel;

    /**
     * 原工资
     */
    private Double fromSalary;

    /**
     * 新部门ID
     */
    private Long toDepartmentId;

    /**
     * 新岗位ID
     */
    private Long toPositionId;

    /**
     * 新职位级别
     */
    private String toPositionLevel;

    /**
     * 新工资
     */
    private Double toSalary;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 调动原因
     */
    private String reason;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 审批日期
     */
    private LocalDate approveDate;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;

    /**
     * 租户ID
     */
    private Long tenantId;
}
