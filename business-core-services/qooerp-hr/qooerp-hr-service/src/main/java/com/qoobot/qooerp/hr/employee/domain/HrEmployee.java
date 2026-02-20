package com.qoobot.qooerp.hr.employee.domain;

import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrEmployee extends BaseEntity {

    /** 员工编号 */
    private String employeeCode;

    /** 用户ID */
    private Long userId;

    /** 员工姓名 */
    private String employeeName;

    /** 性别 */
    private String gender;

    /** 出生日期 */
    private LocalDate birthDate;

    /** 手机号 */
    private String phoneNumber;

    /** 邮箱 */
    private String email;

    /** 身份证号 */
    private String idCard;

    /** 组织ID */
    private Long organizationId;

    /** 部门ID */
    private Long departmentId;

    /** 岗位ID */
    private Long positionId;

    /** 入职日期 */
    private LocalDate entryDate;

    /** 转正日期 */
    private LocalDate regularDate;

    /** 离职日期 */
    private LocalDate resignationDate;

    /** 员工状态 */
    private Integer status;

    /** 婚姻状况 */
    private String maritalStatus;

    /** 民族 */
    private String nation;

    /** 学历 */
    private String education;

    /** 政治面貌 */
    private String politicalStatus;

    /** 户籍地址 */
    private String domicileAddress;

    /** 现居住地址 */
    private String currentAddress;

    /** 紧急联系人 */
    private String emergencyContact;

    /** 紧急联系人电话 */
    private String emergencyPhone;
}
