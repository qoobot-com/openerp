package com.qoobot.qooerp.hr.employee.experience.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 工作经历实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_experience")
public class HrExperience extends BaseEntity {

    /**
     * 工作经历ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 员工ID
     */
    @TableField("employee_id")
    private Long employeeId;

    /**
     * 员工姓名
     */
    @TableField("employee_name")
    private String employeeName;

    /**
     * 公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 所属行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 职级
     */
    @TableField("rank")
    private String rank;

    /**
     * 入职时间
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 离职时间
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 工作年限（月）
     */
    @TableField("work_months")
    private Integer workMonths;

    /**
     * 工作地点
     */
    @TableField("work_location")
    private String workLocation;

    /**
     * 工作内容
     */
    @TableField("work_content")
    private String workContent;

    /**
     * 离职原因
     */
    @TableField("leave_reason")
    private String leaveReason;

    /**
     * 证明人姓名
     */
    @TableField("referee_name")
    private String refereeName;

    /**
     * 证明人电话
     */
    @TableField("referee_phone")
    private String refereePhone;

    /**
     * 是否离职：0-否 1-是
     */
    @TableField("is_resigned")
    private Integer isResigned;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
