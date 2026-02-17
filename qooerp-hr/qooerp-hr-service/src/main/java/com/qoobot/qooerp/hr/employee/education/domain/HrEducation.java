package com.qoobot.qooerp.hr.employee.education.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教育背景实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_education")
public class HrEducation extends BaseEntity {

    /**
     * 教育ID
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
     * 学历：1-高中 2-中专 3-大专 4-本科 5-硕士 6-博士 7-其他
     */
    @TableField("education_level")
    private Integer educationLevel;

    /**
     * 学历名称
     */
    @TableField("education_name")
    private String educationName;

    /**
     * 学校名称
     */
    @TableField("school_name")
    private String schoolName;

    /**
     * 专业
     */
    @TableField("major")
    private String major;

    /**
     * 学习形式：1-全日制 2-业余 3-函授 4-自考 5-网络教育 6-其他
     */
    @TableField("study_type")
    private Integer studyType;

    /**
     * 入学时间
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 毕业时间
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 学历证书编号
     */
    @TableField("certificate_no")
    private String certificateNo;

    /**
     * 学位证书URL
     */
    @TableField("certificate_url")
    private String certificateUrl;

    /**
     * 是否最高学历：0-否 1-是
     */
    @TableField("is_highest")
    private Integer isHighest;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
