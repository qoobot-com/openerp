package com.qoobot.qooerp.hr.employee.skill.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工技能实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_skill")
public class HrSkill extends BaseEntity {

    /**
     * 技能ID
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
     * 技能类型：1-语言能力 2-专业技能 3-证书资质 4-其他
     */
    @TableField("skill_type")
    private Integer skillType;

    /**
     * 技能名称
     */
    @TableField("skill_name")
    private String skillName;

    /**
     * 技能等级
     */
    @TableField("skill_level")
    private String skillLevel;

    /**
     * 获得日期
     */
    @TableField("obtain_date")
    private LocalDate obtainDate;

    /**
     * 颁发机构
     */
    @TableField("issue_org")
    private String issueOrg;

    /**
     * 证书编号
     */
    @TableField("certificate_no")
    private String certificateNo;

    /**
     * 有效期至
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 证书附件URL
     */
    @TableField("certificate_url")
    private String certificateUrl;

    /**
     * 熟练程度：1-一般 2-熟练 3-精通 4-专家
     */
    @TableField("proficiency")
    private Integer proficiency;

    /**
     * 是否永久有效：0-否 1-是
     */
    @TableField("permanent")
    private Integer permanent;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
