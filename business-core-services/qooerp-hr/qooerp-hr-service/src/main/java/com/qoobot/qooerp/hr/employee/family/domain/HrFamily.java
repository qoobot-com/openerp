package com.qoobot.qooerp.hr.employee.family.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭信息实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_family")
public class HrFamily extends BaseEntity {

    /**
     * 家庭信息ID
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
     * 关系：1-配偶 2-父亲 3-母亲 4-子女 5-其他
     */
    @TableField("relationship")
    private Integer relationship;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 性别：0-未知 1-男 2-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 出生日期
     */
    @TableField("birth_date")
    private String birthDate;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 工作单位
     */
    @TableField("work_unit")
    private String workUnit;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 紧急联系人：0-否 1-是
     */
    @TableField("emergency_contact")
    private Integer emergencyContact;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
