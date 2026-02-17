package com.qoobot.qooerp.hr.employee.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 员工DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
public class HrEmployeeDTO {

    @NotBlank(message = "员工姓名不能为空")
    private String employeeName;

    private String gender;

    private LocalDate birthDate;

    private String phoneNumber;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String idCard;

    private Long organizationId;

    private Long departmentId;

    private Long positionId;

    private LocalDate entryDate;

    private String maritalStatus;

    private String nation;

    private String education;

    private String politicalStatus;

    private String domicileAddress;

    private String currentAddress;

    private String emergencyContact;

    private String emergencyPhone;
}
